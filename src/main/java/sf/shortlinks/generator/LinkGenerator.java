package sf.shortlinks.generator;


import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class LinkGenerator {
    @Value("${link.algorithm.prefix:leenk.com/}")
    private String prefix;

    @Value("${link.algorithm.length:5}")
    private int length;

    @Value("${link.algorithm.chars.letters:true}")
    private boolean letters;

    @Value("${link.algorithm.chars.numbers:true}")
    private boolean numbers;

    public String generateShortUrl() {
        return prefix + RandomStringUtils.random(
                length, 0, 0,
                letters, numbers,
                null, // RandomStringUtils.ALPHANUMERICAL_CHARS
                new Random());
    }
}
