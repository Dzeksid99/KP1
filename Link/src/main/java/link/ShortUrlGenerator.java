package link;

import java.util.Random;

public class ShortUrlGenerator {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LEN = 6;
    private static final Random RANDOM = new Random();

    public static String generateShortUrl() {
        StringBuilder sb = new StringBuilder("clck.ru/");
        for (int i = 0; i < LEN; i++) {
            int index = RANDOM.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }
}
