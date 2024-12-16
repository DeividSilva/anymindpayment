package any.mind.payment.domain.dto;

import lombok.Builder;
import lombok.With;

import java.io.Serializable;
import java.math.BigDecimal;

@With
@Builder
public record PaymentMethodDTO(
        Long id,
        String name,
        BigDecimal priceMultiplierFrom,
        BigDecimal priceMultiplierTo,
        BigDecimal pointsMultiplier,
        RuleDTO rule,
        boolean active) implements Serializable {
}