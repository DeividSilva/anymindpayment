package any.mind.payment.domain.request;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record PaymentAdditionalItem(String courier,
                                    String last4,
                                    String bankCode,
                                    String accountNumber,
                                    String chequeNumber) {
}