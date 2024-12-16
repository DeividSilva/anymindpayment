package any.mind.payment.domain.dto;

import java.io.Serializable;
import java.util.List;

import any.mind.payment.domain.entity.Courier;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record RuleDTO(
        String name,
        boolean storeCreditCardLastFour,
        boolean storeBankCode,
        boolean storeAccountNumber,
        boolean storeCheckNumber,
        boolean validateCourier,
        String description,
        List<String> couriers) implements Serializable {
}