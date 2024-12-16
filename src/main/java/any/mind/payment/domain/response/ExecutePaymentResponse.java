package any.mind.payment.domain.response;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record ExecutePaymentResponse(String finalPrice, Integer points) {

}