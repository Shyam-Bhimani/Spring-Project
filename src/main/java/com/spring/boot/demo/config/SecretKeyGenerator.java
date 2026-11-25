
package com.spring.boot.demo.config;

import io.jsonwebtoken.Jwts;

import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        var secretKey = Jwts.SIG.HS256.key().build();

        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        System.out.println(encodedKey);
    }
}
