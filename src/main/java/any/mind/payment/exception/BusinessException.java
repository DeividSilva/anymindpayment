package any.mind.payment.exception;

import java.io.Serial;

import static any.mind.payment.exception.ErrorType.BUSINESS;

public class BusinessException extends AbstractErrorException {
    @Serial
    private static final long serialVersionUID = 1486297407038116871L;

    private final ErrorType errorType = BUSINESS;

    public BusinessException(String errorCode, String message) {
        super(errorCode, message);
    }

    @Override
    public ErrorType getErrorType() {
        return errorType;
    }
}