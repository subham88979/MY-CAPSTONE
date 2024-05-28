import java.security.KeyPair;
import java.util.Base64;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import javax.crypto.Cipher;

public class RSAEXAMPLE {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        try {
            System.out.println("Enter a message to encrypt:");
            String userMessage = input.nextLine();

            KeyPair keyPair = generateRSAKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            byte[] cipherText = encrypt(userMessage, publicKey);

            System.out.println("Original text is: " + userMessage);
            System.out.println("Ciphertext is: " + Base64.getEncoder().encodeToString(cipherText));

            String originalMessage = decrypt(cipherText, privateKey);
            System.out.println("Decrypted text is: " + originalMessage);
        } finally {
            input.close(); // Ensure the scanner is closed
        }
    }

    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] encrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    public static String decrypt(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(cipherText);
        return new String(decryptedBytes); // Convert byte array to String
    }
}
