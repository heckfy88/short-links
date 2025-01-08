package sf.shortlinks.api.exception;

import lombok.Getter;

@Getter
public class ConstraintException extends RuntimeException {
    private final String description;

    public ConstraintException(String message, String description) {
        super(message);
        this.description = description;
    }
}
