package org.example.Security;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {
    private final String PRIVATE_KEY_FILE = "private_key.pem";
    private final String PUBLIC_KEY_FILE = "public_key.pem";
    private PublicKey publicKey;
    private PrivateKey privateKey;
    public RSA() throws Exception {
        String path = String.valueOf(Paths.get(PUBLIC_KEY_FILE).toAbsolutePath());
        File file = new File(path);

        if (file.exists()){
             publicKey = loadPublicKeyFromFile(PUBLIC_KEY_FILE);
             privateKey = loadPrivateKeyFromFile(PRIVATE_KEY_FILE);
        }
        else {
            // Generate RSA key pair
            KeyPair keyPair = generateRSAKeyPair();

            // Get public and private keys
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            // Save keys to files
            saveKeyToFile(privateKey, PRIVATE_KEY_FILE);
            saveKeyToFile(publicKey, PUBLIC_KEY_FILE);
        }
    }

    public KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Key size of 2048 bits
        return keyPairGenerator.generateKeyPair();
    }
    public String encrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    public String decrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
    public void saveKeyToFile(java.security.Key key, String fileName) throws Exception {
        byte[] keyBytes = key.getEncoded();
        Files.write(Paths.get(fileName), keyBytes);
    }
    public PublicKey loadPublicKeyFromFile(String fileName) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
    public PrivateKey loadPrivateKeyFromFile(String fileName) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }
    public PublicKey getPublicKey(){
        return publicKey;
    }

    public PrivateKey getPrivateKey(){
        return privateKey;
    }
}
