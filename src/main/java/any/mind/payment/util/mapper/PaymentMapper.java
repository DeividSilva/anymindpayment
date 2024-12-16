package any.mind.payment.util.mapper;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_DOWN;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;

import any.mind.payment.domain.dto.PaymentMethodDTO;
import any.mind.payment.domain.dto.RuleDTO;
import any.mind.payment.domain.entity.Payment;
import any.mind.payment.domain.request.ExecutePaymentRequest;

public class PaymentMapper {

    public static Payment toEntity(ExecutePaymentRequest request, PaymentMethodDTO paymentMethodDTO) {
        final RuleDTO rule = paymentMethodDTO.rule();
        String courier = null;
        String bankCode = null;
        String accountNumber = null;
        String checkNumber = null;
        String lastFour = null;

        BigDecimal finalPrice = new BigDecimal(request.price()).multiply(new BigDecimal(request.priceModifier()))
            .setScale(2, HALF_DOWN); // The round rule wasn't provided.

        Integer points = new BigDecimal(request.price())
            .multiply(ofNullable(paymentMethodDTO.pointsMultiplier()).orElse(ZERO))
            .intValue(); // Returns Integer, removing any decimal values

        if (rule.storeBankCode()) {
            bankCode = request.additionalItem().bankCode();
        }
        if (rule.storeAccountNumber()) {
            accountNumber = request.additionalItem().accountNumber();
        }
        if (rule.storeCheckNumber()) {
            checkNumber = request.additionalItem().chequeNumber();
        }
        if (rule.storeCreditCardLastFour()) {
            lastFour = request.additionalItem().last4();
        }
        if (rule.validateCourier()) {
            courier = request.additionalItem().courier();
        }

        return Payment.builder()
            .idPaymentMethod(paymentMethodDTO.id())
            .bankCode(bankCode)
            .accountNumber(accountNumber)
            .checkNumber(checkNumber)
            .creditCardLastFour(lastFour)
            .courier(courier)
            .initialPrice(new BigDecimal(request.price()))
            .finalPrice(finalPrice)
            .points(points)
            .priceModifier(new BigDecimal(request.priceModifier()))
            .pointsMultiplier(paymentMethodDTO.pointsMultiplier())
            .datetime(request.datetime())
            .idCustomer(request.customerId())
            .build();
    }

}