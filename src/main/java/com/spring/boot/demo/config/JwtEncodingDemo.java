package com.spring.boot.demo.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtEncodingDemo {
    public static void main(String[] args) {
        String originalPayload = "{\"sub\":\"1234567890\",\"name\":\"John Doe\",\"role\":\"admin\",\"iat\":1732365000}";

        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(originalPayload.getBytes(StandardCharsets.UTF_8));

        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedPayload);
        String decodedPayload = new String(decodedBytes,StandardCharsets.UTF_8);

        System.out.println("Original json payload : "+originalPayload);
        System.out.println("Base64URL Encoded : "+encodedPayload);
        System.out.println("Decoded Json Payload : "+decodedPayload);

        boolean isMatch = originalPayload.equals(decodedPayload);
        System.out.println("Verification Match : "+isMatch);
    }
}
