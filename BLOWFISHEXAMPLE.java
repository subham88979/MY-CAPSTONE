import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class BLOWFISHEXAMPLE {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the plain text:");
        String inputPlainText = in.nextLine();

        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
        SecretKey secretKey = keyGenerator.generateKey();

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedTextByteArray = cipher.doFinal(inputPlainText.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(encryptedTextByteArray);
        System.out.println("Encrypted text of original plaintext is: " + encryptedText);

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] originalPlainTextArray = cipher.doFinal(decodedBytes);
        String originalPlainText = new String(originalPlainTextArray);

        System.out.println("Original text is: " + originalPlainText);
        in.close();
    }

}
