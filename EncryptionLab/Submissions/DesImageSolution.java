import java.lang.Object;
import javax.imageio.ImageIO;
import java.io.*;
import java.awt.image.BufferedImage;
import java.nio.*;
import javax.crypto.*;
import java.util.Base64;


// Modified for triangle problem
public class DesImageSolution {
    public static void main(String[] args) throws Exception{
        int image_width = 200;
        int image_length = 200;
        // read image file and save pixel value into int[][] imageArray
        BufferedImage img = ImageIO.read(new File("triangle.bmp"));
        image_width = img.getWidth();
        image_length = img.getHeight();
        // byte[][] imageArray = new byte[image_width][image_length];
        int[][] imageArray = new int[image_width][image_length];
        for(int idx = 0; idx < image_width; idx++) {
            for(int idy = 0; idy < image_length; idy++) {
                int color = img.getRGB(idx, idy);
                imageArray[idx][idy] = color;            
            }
        } 
        
        // generate secret key using DES algorithm
        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey desKey = keygen.generateKey();


        // Create cipher object, initialize the ciphers with the given key, choose encryption algorithm/mode/padding,
        // you need to try both ECB and CBC mode, use PKCS5Padding padding method
        Cipher desEncryptionCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        desEncryptionCipher.init(Cipher.ENCRYPT_MODE,desKey);
        

        // define output BufferedImage, set size and format
        BufferedImage outImage = new BufferedImage(image_width,image_length, BufferedImage.TYPE_3BYTE_BGR);

        for(int idx = 0; idx < image_width; idx++) {
            // convert each column int[] into a byte[] (each_width_pixel)
            byte[] each_width_pixel = new byte[4*image_length];
            for(int idy = 0; idy < image_length; idy++) {
                ByteBuffer dbuf = ByteBuffer.allocate(4);
                dbuf.putInt(imageArray[idx][idy]);
                byte[] bytes = dbuf.array();
                System.arraycopy(bytes, 0, each_width_pixel, (image_length-idy-1)*4, 4);
            }

            // encrypt each column or row bytes
            byte[] each_width_encrypted_pixel = desEncryptionCipher.doFinal(each_width_pixel);
            
            // convert the encrypted byte[] back into int[] and write to outImage (use setRGB)
            int[] each_width_encrypted_pixel_int = new int[image_length];
            for (int idy = 0; idy < image_length ; idy++) {
                byte[] temp = {
                    each_width_encrypted_pixel[(image_length-idy-1)*4],
                    each_width_encrypted_pixel[(image_length-idy-1)*4+1],
                    each_width_encrypted_pixel[(image_length-idy-1)*4+2],
                    each_width_encrypted_pixel[(image_length-idy-1)*4+3]
                };
                ByteBuffer dbuf = ByteBuffer.wrap(temp);
                
                each_width_encrypted_pixel_int[idy] = dbuf.getInt();
                outImage.setRGB(idx,idy,each_width_encrypted_pixel_int[idy]);
            }
        }
        //write outImage into file
        ImageIO.write(outImage, "BMP",new File("triangle_new.bmp"));
    }
}