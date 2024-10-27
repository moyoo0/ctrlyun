package com.wu.service.utils;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.KeyFactory;
import java.security.cert.Certificate;
import java.math.BigInteger;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;


import org.springframework.stereotype.Component;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

// 公私钥生成，RSA
@Component
public class KeyGenerator {
    // 文件路径
    private static final String KEYSTORE_PATH = "keystore.jks";
    // 读取密钥文件的密码
    private static final String KEYSTORE_PASSWORD = "1234";
    // 密钥对在密钥文件中的别名
    private static final String ALIAS = "mykeypair";
    // 读取密钥文件中私钥的密码
    private static final String KEY_PASSWORD = "12345";

    public void generateAndStoreKeys() throws Exception {
        // Register Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());

        // Generate key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024); // Key 64 bit
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Get public and private keys
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // Create KeyStore and store keys
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, KEYSTORE_PASSWORD.toCharArray());

        // Create a self-signed certificate for the key pair
        Certificate certificate = createSelfSignedCertificate(publicKey, privateKey);

        // Store the key pair in the KeyStore with a password and the certificate
        keyStore.setKeyEntry(ALIAS, privateKey, KEY_PASSWORD.toCharArray(), new Certificate[]{certificate});

        // Save the KeyStore to a file
        try (FileOutputStream fos = new FileOutputStream(KEYSTORE_PATH)) {
            keyStore.store(fos, KEYSTORE_PASSWORD.toCharArray());
        }

        System.out.println("Public and private keys generated and stored in keystore.jks.");
    }

    // A helper method to create a self-signed certificate using Bouncy Castle library
    private Certificate createSelfSignedCertificate(PublicKey publicKey, PrivateKey privateKey) throws IOException, OperatorCreationException, CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        X500Name issuer = new X500Name("CN=" + ALIAS);
        X500Name subject = issuer;
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + 365 * 24 * 60 * 60 * 1000L); // Valid for one year

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey1 = keyFactory.generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));

        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(issuer, serialNumber, notBefore, notAfter, subject,publicKey1);

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider()).getCertificate(certificateHolder);
    }
    public static void main(String[] args) throws Exception {
        // Create a KeyGenerator object
        KeyGenerator keyGenerator = new KeyGenerator();

        // Call the generateAndStoreKeys method
        keyGenerator.generateAndStoreKeys();

        // Create a KeyStore object and load the keystore.jks file
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream("keystore.jks"), "1234".toCharArray());

        // Get the private key from the KeyStore
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("mykeypair", "12345".toCharArray());

        // Get the certificate from the KeyStore
        Certificate certificate = keyStore.getCertificate("mykeypair");

        // Get the public key from the certificate
        PublicKey publicKey = certificate.getPublicKey();


        // Print the private key, public key and certificate
        System.out.println("Private key: " + privateKey);
        System.out.println("Public key: " + publicKey);
        System.out.println("Certificate: " + certificate);


        // Create a Cipher object for encryption and decryption
        Cipher cipher = Cipher.getInstance("RSA");

        // The data to be encrypted and decrypted
        String data = "Hello World!";

        // Encrypt the data using the public key
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        System.out.println("Encrypted data: " + new String(encryptedData));

        // Decrypt the data using the private key
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        System.out.println("Decrypted data: " + new String(decryptedData));
    }
}
