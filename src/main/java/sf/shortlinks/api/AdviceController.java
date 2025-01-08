package sf.shortlinks.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import sf.shortlinks.api.exception.ConstraintException;
import sf.shortlinks.api.exception.ErrorMessage;
import sf.shortlinks.api.exception.LinkNotFound;

import java.time.LocalDateTime;

@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(value = {ConstraintException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage constraintException(ConstraintException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now(),
                ex.getMessage(),
                ex.getDescription());
    }

    @ExceptionHandler(value = {LinkNotFound.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage linkNotFoundException(LinkNotFound ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                ex.getMessage(),
                null
        );
    }
}
