package sf.shortlinks.api.exception;

import lombok.Getter;

@Getter
public class LinkNotFound extends RuntimeException {

    public LinkNotFound(String message, String shortUrl) {
        super(message + shortUrl);
    }
}