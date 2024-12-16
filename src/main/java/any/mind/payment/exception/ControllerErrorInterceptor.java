package any.mind.payment.exception;

import any.mind.payment.domain.Message;
import any.mind.payment.helper.MessageHelper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ControllerErrorInterceptor {

    @Autowired
    private MessageHelper messageHelper;

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(BusinessException exception) {
        log.error("Error Business {}", exception.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ErrorResponseData.build(exception)),
            exception.getErrorType().getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("Error Generic {}", exception.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ErrorResponseData.build(exception)), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
        final var fieldError = exception.getBindingResult().getFieldErrors().getFirst();

        log.error("Error Field Validation {}", exception.getMessage());

        return ResponseEntity.badRequest().body(new ErrorResponse(ErrorResponseData.builder()
            .errorCode(Message.INVALID_REQUEST_FIELD.name())
            .errorType(ErrorType.VALIDATION)
            .message(fieldError.getDefaultMessage())
            .build()));
    }

}