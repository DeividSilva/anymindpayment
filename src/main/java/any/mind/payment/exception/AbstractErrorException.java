package any.mind.payment.exception;

import java.io.Serial;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

@Getter
public abstract class AbstractErrorException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8004100005407012963L;

    private final String errorCode;

    public AbstractErrorException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public abstract ErrorType getErrorType();



}