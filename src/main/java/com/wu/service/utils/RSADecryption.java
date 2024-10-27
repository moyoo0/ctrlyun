package com.wu.service.utils;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.security.*;
import java.util.Base64;

public class RSADecryption {


    // RSA私钥解密函数
    public static byte[] decryptWithPrivateKey(PrivateKey privateKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }
    public static byte[] encryptWithPublicKey(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }
}
