package any.mind.payment.util.mapper;

import java.util.List;
import java.util.stream.Collectors;

import any.mind.payment.domain.dto.PaymentMethodDTO;
import any.mind.payment.domain.dto.RuleDTO;
import any.mind.payment.domain.entity.Courier;
import any.mind.payment.domain.entity.PaymentMethod;
import any.mind.payment.domain.entity.Rule;

public class PaymentMethodMapper {

    public static PaymentMethodDTO toDTO(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }

        PaymentMethodDTO.PaymentMethodDTOBuilder paymentMethodDTO = PaymentMethodDTO.builder();

        paymentMethodDTO.id(paymentMethod.getId());
        paymentMethodDTO.name(paymentMethod.getName());
        paymentMethodDTO.priceMultiplierFrom(paymentMethod.getPriceMultiplierFrom());
        paymentMethodDTO.priceMultiplierTo(paymentMethod.getPriceMultiplierTo());
        paymentMethodDTO.pointsMultiplier(paymentMethod.getPointsMultiplier());
        paymentMethodDTO.rule(ruleToRuleDTO(paymentMethod.getRule()));
        paymentMethodDTO.active(paymentMethod.isActive());

        return paymentMethodDTO.build();
    }

    public static RuleDTO ruleToRuleDTO(Rule rule) {
        if (rule == null) {
            return null;
        }

        RuleDTO.RuleDTOBuilder ruleDTO = RuleDTO.builder();

        ruleDTO.name(rule.getName());
        ruleDTO.storeCreditCardLastFour(rule.isStoreCreditCardLastFour());
        ruleDTO.storeBankCode(rule.isStoreBankCode());
        ruleDTO.storeAccountNumber(rule.isStoreAccountNumber());
        ruleDTO.storeCheckNumber(rule.isStoreCheckNumber());
        ruleDTO.validateCourier(rule.isValidateCourier());
        ruleDTO.description(rule.getDescription());
        List<Courier> list = rule.getCouriers();
        if (list != null) {
            ruleDTO.couriers(list.stream().map(Courier::getName).collect(Collectors.toList()));
        }

        return ruleDTO.build();
    }

}
