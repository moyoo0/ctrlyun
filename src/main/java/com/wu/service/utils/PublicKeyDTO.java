package com.wu.service.utils;

import java.io.Serializable;
import java.security.interfaces.RSAPublicKey;

public class PublicKeyDTO implements Serializable {
    private String modulus;
    private String publicExponent;

    public  PublicKeyDTO(RSAPublicKey publicKey) {
        this.modulus = publicKey.getModulus().toString(16);
        this.publicExponent = publicKey.getPublicExponent().toString(16);
    }

    public String getModulus() {
        return modulus;
    }

    public String getPublicExponent() {
        return publicExponent;
    }
}
