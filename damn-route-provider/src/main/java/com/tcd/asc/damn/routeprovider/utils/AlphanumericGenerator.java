package com.tcd.asc.damn.routeprovider.utils;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AlphanumericGenerator {

    private static final Random random = new Random();

    public String generateAlphanumericString() {
        // Define the character set: A-Z, a-z, 0-9
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 10;

        StringBuilder result = new StringBuilder(length);

        // Generate a random character from the character set 'length' times
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }
}