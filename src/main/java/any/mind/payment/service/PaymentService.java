package any.mind.payment.service;

import static any.mind.payment.domain.Message.INVALID_COURIER;
import static any.mind.payment.domain.Message.INVALID_LAST_FOUR;
import static any.mind.payment.domain.Message.INVALID_PAYMENT_METHOD;
import static any.mind.payment.domain.Message.INVALID_PRICE_MODIFIER;
import static any.mind.payment.domain.Message.PROVIDE_ACCOUNT_NUMBER;
import static any.mind.payment.domain.Message.PROVIDE_BANK_CODE;
import static any.mind.payment.domain.Message.PROVIDE_CHECK_NUMBER;
import static any.mind.payment.domain.Message.PROVIDE_COURIER;
import static any.mind.payment.domain.Message.PROVIDE_LAST_FOUR;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import any.mind.payment.domain.dto.PaymentMethodDTO;
import any.mind.payment.domain.dto.RuleDTO;
import any.mind.payment.domain.entity.Payment;
import any.mind.payment.domain.request.ExecutePaymentRequest;
import any.mind.payment.domain.response.ExecutePaymentResponse;
import any.mind.payment.domain.response.HourlySalesResponse;
import any.mind.payment.exception.BusinessException;
import any.mind.payment.helper.MessageHelper;
import any.mind.payment.repository.PaymentRepository;
import any.mind.payment.util.mapper.HourlySalesMapper;
import any.mind.payment.util.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMethodService paymentMethodService;
    private final MessageHelper messageHelper;
    private final PaymentRepository repository;

    public ExecutePaymentResponse executePayment(ExecutePaymentRequest request) {
        PaymentMethodDTO paymentMethod = validateRequest(request);
        Payment payment = PaymentMapper.toEntity(request, paymentMethod);
        repository.save(payment);
        return ExecutePaymentResponse.builder()
            .finalPrice(payment.getFinalPrice().toString())
            .points(payment.getPoints()).build();
    }

    public List<HourlySalesResponse> getHourlySales(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return repository.findHourlySales(startDateTime, endDateTime).stream().map(HourlySalesMapper::toResponse)
            .collect(Collectors.toList());
    }

    private PaymentMethodDTO validateRequest(ExecutePaymentRequest request) {
        final PaymentMethodDTO paymentMethod = Optional.ofNullable(paymentMethodService.getPaymentMethodByName(
                request.paymentMethod()))
            .orElseThrow(() -> new BusinessException(INVALID_PAYMENT_METHOD.name(), messageHelper.get(
                INVALID_PAYMENT_METHOD, request.paymentMethod())));

        validatePriceModifier(request, paymentMethod);

        final RuleDTO rule = paymentMethod.rule();

        validateBankCode(request, rule);
        validateAccountNumber(request, rule);
        validateCheckNumber(request, rule);
        validateCreditCardLastFour(request, rule);
        validateCourier(request, rule);

        return paymentMethod;
    }

    private void validateCourier(final ExecutePaymentRequest request, final RuleDTO rule) {
        if (rule.validateCourier()) {
            if (request.additionalItem() == null
                || request.additionalItem().courier() == null
                || request.additionalItem().courier().isEmpty()) {
                throw new BusinessException(PROVIDE_COURIER.name(), messageHelper.get(
                    PROVIDE_COURIER));
            }
            if (rule.couriers().stream().noneMatch((c) -> c.equalsIgnoreCase(request.additionalItem().courier()))) {
                throw new BusinessException(INVALID_COURIER.name(), messageHelper.get(
                    INVALID_COURIER, request.additionalItem().courier()));
            }
        }
    }

    private void validateCreditCardLastFour(final ExecutePaymentRequest request, final RuleDTO rule) {
        if (rule.storeCreditCardLastFour()) {
            if (request.additionalItem() == null
                || request.additionalItem().last4() == null
                || request.additionalItem().last4().isEmpty()) {
                throw new BusinessException(PROVIDE_LAST_FOUR.name(), messageHelper.get(
                    PROVIDE_LAST_FOUR));
            }
            if (!isValidLastFour(request.additionalItem().last4())) {
                throw new BusinessException(INVALID_LAST_FOUR.name(), messageHelper.get(
                    INVALID_LAST_FOUR, request.priceModifier(), request.paymentMethod()));
            }
        }
    }

    private void validateCheckNumber(final ExecutePaymentRequest request, final RuleDTO rule) {
        if (rule.storeCheckNumber() &&
            (request.additionalItem() == null
             || request.additionalItem().chequeNumber() == null
             || request.additionalItem().chequeNumber().isEmpty())) {
            throw new BusinessException(PROVIDE_CHECK_NUMBER.name(), messageHelper.get(
                PROVIDE_CHECK_NUMBER));
        }
    }

    private void validateAccountNumber(final ExecutePaymentRequest request, final RuleDTO rule) {
        if (rule.storeAccountNumber() &&
            (request.additionalItem() == null
             || request.additionalItem().accountNumber() == null
             || request.additionalItem().accountNumber().isEmpty())) {
            throw new BusinessException(PROVIDE_ACCOUNT_NUMBER.name(), messageHelper.get(
                PROVIDE_ACCOUNT_NUMBER));
        }
    }

    private void validateBankCode(final ExecutePaymentRequest request, final RuleDTO rule) {
        if (rule.storeBankCode() &&
            (request.additionalItem() == null
             || request.additionalItem().bankCode() == null
             || request.additionalItem().bankCode().isEmpty())) {
            throw new BusinessException(PROVIDE_BANK_CODE.name(), messageHelper.get(
                PROVIDE_BANK_CODE));
        }
    }

    private void validatePriceModifier(final ExecutePaymentRequest request, final PaymentMethodDTO paymentMethod) {
        if (new BigDecimal(request.priceModifier()).compareTo(paymentMethod.priceMultiplierFrom()) < 0
            || new BigDecimal(request.priceModifier()).compareTo(paymentMethod.priceMultiplierTo()) > 0) {
            throw new BusinessException(INVALID_PRICE_MODIFIER.name(), messageHelper.get(
                INVALID_PRICE_MODIFIER, request.priceModifier(), request.paymentMethod()));
        }
    }

    private boolean isValidLastFour(String lastFour) {
        return lastFour.matches("\\d{4}");
    }

}