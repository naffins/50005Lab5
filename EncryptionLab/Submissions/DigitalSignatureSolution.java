import java.util.Base64;
import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;


public class DigitalSignatureSolution {

    public static void main(String[] args) throws Exception {
        //Read the text file and save to String data
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

        // generate a RSA keypair, initialize as 1024 bits, get public key and private key from this keypair.
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair keyPair = keyGen.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();

        // Calculate message digest, using MD5 hash function
        byte[][] digests = new byte[fileName.length][];
        MessageDigest md = MessageDigest.getInstance("MD5");
        for (int i=0;i<fileName.length;i++) {
            md.update(data[i].getBytes());
            digests[i] = md.digest();
        }

        // print the length of output digest byte[], compare the length of file shorttext.txt and longtext.txt
        for (int i=0;i<fileName.length;i++) {
            System.out.println("Length of digest for " + fileName[i] + ": " + digests[i].length + "\n");
        }
        // Create RSA("RSA/ECB/PKCS1Padding") cipher object and initialize is as encrypt mode, use PRIVATE key.
        Cipher rsaEncryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaEncryptCipher.init(Cipher.ENCRYPT_MODE,privateKey);

        byte[][] encryptedDigests = new byte[fileName.length][];
        for (int i=0;i<fileName.length;i++) {
            // encrypt digest message
            encryptedDigests[i] = rsaEncryptCipher.doFinal(digests[i]);

            // print the encrypted message (in base64format String using Base64)
            System.out.println("Encrypted digest for " + fileName[i] + ": " + Base64.getEncoder().encodeToString(encryptedDigests[i]) + "\n");
        }
        
        // Create RSA("RSA/ECB/PKCS1Padding") cipher object and initialize is as decrypt mode, use PUBLIC key.          
        Cipher rsaDecryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaDecryptCipher.init(Cipher.DECRYPT_MODE,publicKey);

        byte[][] decryptedDigests = new byte[fileName.length][];
        for (int i=0;i<fileName.length;i++) {
            // decrypt message
            decryptedDigests[i] = rsaDecryptCipher.doFinal(encryptedDigests[i]);

            // print the decrypted message (in base64format String using Base64), compare with origin digest
            System.out.println("Decrypted digest for " + fileName[i] + ": " + Base64.getEncoder().encodeToString(decryptedDigests[i]));
            System.out.println(
                Base64.getEncoder().encodeToString(digests[i]).equals(Base64.getEncoder().encodeToString(decryptedDigests[i]))?
                "Decrypted digest for " + fileName[i] + " matches original digest.\n" : "Decrypted digest for " + fileName[i] + " DOES NOT match original digest.\n"
            );
        }

    }

}