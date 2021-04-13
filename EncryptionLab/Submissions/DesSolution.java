import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.crypto.*;
import java.util.Base64;


public class DesSolution {
    public static void main(String[] args) throws Exception {
        String[] fileName = {"shorttext.txt","longtext.txt"};
        String[] data = new String[fileName.length];
        
        for (int i=0;i<fileName.length;i++) {
            data[i] = "";
            String line;
            BufferedReader bufferedReader = new BufferedReader( new FileReader(fileName[i]));
            while((line= bufferedReader.readLine())!=null){
                data[i] = data[i] +"\n" + line;
            }
            System.out.println("Original content of " + fileName[i] + ": ");
            System.out.println("==========BEGIN CONTENT==========");
            System.out.println(data[i]);
            System.out.println("==========END CONTENT==========\n");
        }

        // generate secret key using DES algorithm
        KeyGenerator keygen = KeyGenerator.getInstance("DES");
        SecretKey desKey = keygen.generateKey();

        // create cipher object, initialize the ciphers with the given key, choose encryption mode as DES
        Cipher desEncryptionCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        desEncryptionCipher.init(Cipher.ENCRYPT_MODE,desKey);

        // do encryption, by calling method Cipher.doFinal().
        byte[][] encryptedResults = new byte[fileName.length][];
        for (int i=0;i<fileName.length;i++) {
            encryptedResults[i] = desEncryptionCipher.doFinal(data[i].getBytes());
        }

        // print the length of output encrypted byte[], compare the length of file shorttext.txt and longtext.txt
        for (int i=0;i<fileName.length;i++) {
            System.out.println("Length of encryption output for " + fileName[i] + ": " + encryptedResults[i].length + "\n");
        }

        String[] encryptedStringResults = new String[fileName.length];
        for (int i=0;i<fileName.length;i++) {
            // do format conversion. Turn the encrypted byte[] format into base64format String using Base64
            encryptedStringResults[i] = Base64.getEncoder().encodeToString(encryptedResults[i]);

            // print the encrypted message (in base64format String format)
            System.out.println("Encryption output for " + fileName[i] + ": ");
            System.out.println("==========BEGIN ENCRYPTION OUTPUT==========");
            System.out.println(encryptedStringResults[i]);
            System.out.println("==========END ENCRYPTION OUTPUT==========\n");
        }

        // create cipher object, initialize the ciphers with the given key, choose decryption mode as DES
        Cipher desDecryptionCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        desDecryptionCipher.init(Cipher.DECRYPT_MODE,desKey);

        // do decryption, by calling method Cipher.doFinal().
        byte[][] decryptedResults = new byte[fileName.length][];
        for (int i=0;i<fileName.length;i++) {
            decryptedResults[i] = desDecryptionCipher.doFinal(encryptedResults[i]);
        }

        String[] decryptedStringResults = new String[fileName.length];
        for (int i=0;i<fileName.length;i++) {
            // do format conversion. Convert the decrypted byte[] to String, using "String a = new String(byte_array);"
            decryptedStringResults[i] = new String(decryptedResults[i]);

            // print the decrypted String text and compare it with original text
            System.out.println("Encryption output for " + fileName[i] + ": ");
            System.out.println("==========BEGIN DECRYPTION OUTPUT==========");
            System.out.println(decryptedStringResults[i]);
            System.out.println("==========END DECRYPTION OUTPUT==========");
            System.out.println(
                decryptedStringResults[i].equals(data[i])? "Decryption result matches input file\n" : "Decryption result DOES NOT match input file!\n"
            );
        }

        experiments(2,encryptedResults);

    }

    public static void experiments(int length,byte[][] encryptedResults) {
        for (int i=0;i<length;i++) {
            System.out.println("Test: print encryption result of file " + i + ": ");
            System.out.println(new String(encryptedResults[i]) + "\n");
        }
        
    }
}