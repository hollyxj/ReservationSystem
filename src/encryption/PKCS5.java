package encryption;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class PKCS5 {

    public static void main(String[] args) {
        try {
            String plaintext = "Hello, World! This is a PKCS5 encryption example.";
            String password = "SecretPassword123"; // Your secret password

            // Encrypt the plaintext
            String encryptedMessage = encrypt(plaintext, plaintext);
            System.out.println("Encrypted message: " + encryptedMessage);

            // Decrypt the encrypted message
            String decryptedMessage = decrypt(encryptedMessage, plaintext);
            System.out.println("Decrypted message: " + decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String encrypt(String plaintext) throws Exception {
    	// Encrypts with plaintext string
        return encrypt(plaintext, plaintext);
    }

    public static String encrypt(String plaintext, String password) throws Exception {
    	// Encrypts with password
        // Generate random salt (16 bytes)
        byte[] salt = generateSalt();

        // Derive key from password and salt using PBKDF2
        SecretKey secretKey = deriveKey(password, salt);

        // Initialize AES cipher in CBC mode with PKCS5Padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Generate random IV (16 bytes) for AES CBC mode
        byte[] iv = generateIV();

        // Initialize cipher with encryption mode and secret key + IV
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // Encrypt the plaintext
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        // Combine salt, IV, and encrypted data for storage or transmission
        byte[] combined = concat(salt, iv, encryptedBytes);

        // Encode the combined data as Base64 for readability and transmission
        return Base64.getEncoder().encodeToString(combined);
    }
    
    public static String decrypt(String encryptedMessage) throws Exception {
       return decrypt(encryptedMessage, encryptedMessage);
    }
    

    public static String decrypt(String encryptedMessage, String password) throws Exception {
        // Decode Base64 encoded data
        byte[] combined = Base64.getDecoder().decode(encryptedMessage);

        // Split combined data into salt, IV, and encrypted data
        byte[] salt = Arrays.copyOfRange(combined, 0, 16);
        byte[] iv = Arrays.copyOfRange(combined, 16, 32);
        byte[] encryptedData = Arrays.copyOfRange(combined, 32, combined.length);

        // Derive key using PBKDF2 with same salt and password
        SecretKey secretKey = deriveKey(password, salt);

        // Initialize AES cipher in CBC mode with PKCS5Padding for decryption
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        // Decrypt the data and return as string
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes);
    }

    private static byte[] generateSalt() {
        // Generate random salt (16 bytes)
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    private static SecretKey deriveKey(String password, byte[] salt) throws Exception {
        // Derive key using PBKDF2 with SHA-256 and 65536 iterations
        int iterations = 65536;
        int keyLength = 256;
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static byte[] generateIV() {
        // Generate random IV (16 bytes) for AES CBC mode
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    private static byte[] concat(byte[]... arrays) {
        // Concatenate multiple byte arrays into one
        int totalLength = Arrays.stream(arrays).mapToInt(arr -> arr.length).sum();
        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] arr : arrays) {
            System.arraycopy(arr, 0, result, offset, arr.length);
            offset += arr.length;
        }
        return result;
    }
}
