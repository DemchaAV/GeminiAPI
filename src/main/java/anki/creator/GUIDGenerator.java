package anki.creator;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class GUIDGenerator {
    private static final char[] BASE91_TABLE = ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!#$%&()*+,-./:;<=>?@[\\]^_{|}~")
            .toCharArray();

    public static String guidFor(Object... values) {
        log.debug("Generating GUID for values: {}");

        // 1. Объединяем значения через "__"
        StringBuilder hashStr = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) hashStr.append("__");
            hashStr.append(values[i].toString());
        }
        log.debug("Concatenated input string: {}", hashStr);

        // 2. SHA-256 хеширование
        byte[] hashBytes;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            hashBytes = digest.digest(hashStr.toString().getBytes(StandardCharsets.UTF_8));
            log.debug("SHA-256 hash generated successfully");
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found", e);
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }

        // 3. Берём первые 8 байт и конвертируем в число
        long hashInt = 0;
        for (int i = 0; i < 8; i++) {
            hashInt = (hashInt << 8) | (hashBytes[i] & 0xFF);
        }
        log.debug("Converted hash to integer: {}", hashInt);

        // 4. Конвертируем в Base91
        StringBuilder result = new StringBuilder();
        while (hashInt > 0) {
            result.append(BASE91_TABLE[(int) (hashInt % BASE91_TABLE.length)]);
            hashInt /= BASE91_TABLE.length;
        }

        String guid = result.reverse().toString();
        log.debug("Generated GUID: {}", guid);
        return guid;
    }
}
