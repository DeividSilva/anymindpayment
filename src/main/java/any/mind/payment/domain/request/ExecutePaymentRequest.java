package any.mind.payment.domain.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record ExecutePaymentRequest(

        @NotBlank(message = "The parameter \"customerId\" must be provided.")
        @Schema(description = "Id of the customer who executed the payment", example = "12345")
        String customerId,

        @NotBlank(message = "The parameter \"price\" must be provided.")
        @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "The parameter \"price\" must be in the format \"100.00\"")
        @Schema(description = "Price as String. Format \"xxx.xx\"", example = "1000.23")
        String price,

        @NotBlank(message = "The parameter \"priceModifier\" must be provided.")
        @Schema(description = "Number to be multiplied to the price", example = "0.5")
        String priceModifier,

        @NotBlank(message = "The parameter \"paymentMethod\" must be provided.")
        @Schema(description = "Method of Payment (CASH, VISA, MASTERCARD, LINE_PAY, BANK_TRANSFER, etc)", example = "MASTERCARD")
        String paymentMethod,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        @NotNull(message = "The parameter \"datetime\" must be provided.")
        @Schema(description = "Date Time of the payment with timezone.", example = "2024-12-25T10:25:34Z", pattern = "yyyy-MM-dd'T'HH:mm:ss'Z")
        LocalDateTime datetime,

        @Schema(description = """
                    Additional parameters required based on the selected payment method. The parameters vary as follows:
                
                    | Payment Method   | Field(s)                | Description                                                 |
                    |------------------|-------------------------|-------------------------------------------------------------|
                    | CASH_ON_DELIVERY | courier                 | Name of the courier responsible for the delivery.           |
                    | VISA             | last4                   | Last four digits of the credit card number.                 |
                    | MASTERCARD       | last4                   | Last four digits of the credit card number.                 |
                    | AMEX             | last4                   | Last four digits of the credit card number.                 |
                    | JCB              | last4                   | Last four digits of the credit card number.                 |
                    | BANK_TRANSFER    | bankCode, accountNumber | The bank code and the account number used for the transfer. |
                    | CHEQUE           | bankCode, chequeNumber  | The bank code and the cheque number used for the payment.   |
                
                    For payment methods not listed above, this field should be set to null.
                """)
        PaymentAdditionalItem additionalItem
) {

}
