package com.brcm.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

/**
 * AES/GCM utility for encrypting config secrets such as login.password.
 *
 * Usage:
 *   setx BRCM_SECRET_KEY "<base64-encoded AES key>"
 *   java com.brcm.utils.CryptoUtils encrypt "MyPlainPassword"
 *
 * Then store the returned ENC(...) value in credentials.properties.
 */
public final class CryptoUtils {

    private static final String KEY_PROPERTY = "brcm.secret.key";
    private static final String KEY_ENV = "BRCM_SECRET_KEY";
    private static final String LEGACY_KEY_PROPERTY = "BRCM_SECRET_KEY";
    private static final String ENCRYPTED_PREFIX = "ENC(";
    private static final String ENCRYPTED_SUFFIX = ")";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int IV_LENGTH_BYTES = 12;
    private static final String[] SECRET_FILES = {"secrets.properties"};

    private CryptoUtils() {}

    public static boolean isEncrypted(String value) {
        return value != null
                && value.startsWith(ENCRYPTED_PREFIX)
                && value.endsWith(ENCRYPTED_SUFFIX);
    }

    public static String encrypt(String plaintext) {
        if (plaintext == null) {
            throw new IllegalArgumentException("plaintext cannot be null");
        }

        try {
            byte[] iv = new byte[IV_LENGTH_BYTES];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, resolveSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(combined) + ENCRYPTED_SUFFIX;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to encrypt secret", e);
        }
    }

    public static String decrypt(String encryptedValue) {
        if (!isEncrypted(encryptedValue)) {
            return encryptedValue;
        }

        try {
            byte[] combined = Base64.getDecoder().decode(
                    encryptedValue.substring(ENCRYPTED_PREFIX.length(), encryptedValue.length() - ENCRYPTED_SUFFIX.length())
            );

            if (combined.length <= IV_LENGTH_BYTES) {
                throw new IllegalArgumentException("Encrypted value is too short");
            }

            byte[] iv = new byte[IV_LENGTH_BYTES];
            byte[] ciphertext = new byte[combined.length - IV_LENGTH_BYTES];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH_BYTES);
            System.arraycopy(combined, IV_LENGTH_BYTES, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, resolveSecretKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] decrypted = cipher.doFinal(ciphertext);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to decrypt secret", e);
        }
    }

    public static String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to generate AES key", e);
        }
    }

    private static SecretKey resolveSecretKey() {
        String encodedKey = System.getProperty(KEY_PROPERTY);
        if (encodedKey == null || encodedKey.isBlank()) {
            encodedKey = System.getenv(KEY_ENV);
        }
        if (encodedKey == null || encodedKey.isBlank()) {
            encodedKey = loadKeyFromSecretFiles(KEY_PROPERTY);
        }
        if (encodedKey == null || encodedKey.isBlank()) {
            encodedKey = loadKeyFromSecretFiles(LEGACY_KEY_PROPERTY);
        }

        if (encodedKey == null || encodedKey.isBlank()) {
            throw new IllegalStateException(
                    "Missing encryption key. Set JVM property '" + KEY_PROPERTY + "', environment variable '" + KEY_ENV
                            + "', or add '" + KEY_PROPERTY + "' to secrets.properties."
            );
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(encodedKey.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Encryption key must be Base64-encoded", e);
        }

        int length = keyBytes.length;
        if (length != 16 && length != 24 && length != 32) {
            throw new IllegalStateException("AES key must be 128, 192, or 256 bits after Base64 decoding");
        }

        return new SecretKeySpec(keyBytes, "AES");
    }

    private static String loadKeyFromSecretFiles(String key) {
        for (String fileName : SECRET_FILES) {
            String fromClasspath = loadPropertyFromClasspath(fileName, key);
            if (fromClasspath != null && !fromClasspath.isBlank()) {
                return fromClasspath;
            }
        }
        return "";
    }

    private static String loadPropertyFromClasspath(String fileName, String key) {
        Properties properties = new Properties();
        try (InputStream in = CryptoUtils.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                return null;
            }
            properties.load(in);
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + fileName, e);
        }
    }

    public static boolean canDecrypt(String encryptedValue) {
        try {
            decrypt(encryptedValue);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        if (args.length == 1 && "generate-key".equalsIgnoreCase(args[0])) {
            System.out.println(generateKey());
            return;
        }

        if (args.length == 2 && "encrypt".equalsIgnoreCase(args[0])) {
            System.out.println(encrypt(args[1]));
            return;
        }

        if (args.length == 2 && "decrypt".equalsIgnoreCase(args[0])) {
            System.out.println(decrypt(args[1]));
            return;
        }

        if (args.length == 2 && "validate".equalsIgnoreCase(args[0])) {
            System.out.println(canDecrypt(args[1]) ? "VALID" : "INVALID");
            return;
        }

        System.out.println("Usage:");
        System.out.println("  java com.brcm.utils.CryptoUtils generate-key");
        System.out.println("  java com.brcm.utils.CryptoUtils encrypt <plainText>");
        System.out.println("  java com.brcm.utils.CryptoUtils decrypt <ENC(...)>");
        System.out.println("  java com.brcm.utils.CryptoUtils validate <ENC(...)>");
    }
}
