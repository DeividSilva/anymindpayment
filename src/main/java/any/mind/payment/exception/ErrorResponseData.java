package any.mind.payment.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

import static any.mind.payment.exception.ErrorType.INTERNAL_ERROR;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponseData implements Serializable {

    ErrorType errorType;
    String message;
    String errorCode;

    public static ErrorResponseData build(final Exception ex) {
        if (ex instanceof AbstractErrorException) {
            return build((AbstractErrorException) ex);
        }

        return buildDefault();
    }

    public static ErrorResponseData build(final AbstractErrorException ex) {
        return ErrorResponseData.builder()
                .errorType(ex.getErrorType())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .build();
    }

    public static ErrorResponseData buildDefault() {
        return ErrorResponseData.builder()
                .errorType(INTERNAL_ERROR)
                .message("Falha inesperada")
                .errorCode("GENERIC_INTERNAL_ERROR")
                .build();
    }

}