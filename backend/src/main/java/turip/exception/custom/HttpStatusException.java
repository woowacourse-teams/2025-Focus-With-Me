package turip.exception.custom;

import org.springframework.http.HttpStatus;

public class HttpStatusException extends RuntimeException {

    private final HttpStatus status;

    public HttpStatusException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
