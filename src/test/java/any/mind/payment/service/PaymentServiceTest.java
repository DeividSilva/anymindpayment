package any.mind.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import any.mind.payment.domain.dto.HourlySalesResultData;
import any.mind.payment.domain.dto.PaymentMethodDTO;
import any.mind.payment.domain.dto.RuleDTO;
import any.mind.payment.domain.entity.Payment;
import any.mind.payment.domain.request.ExecutePaymentRequest;
import any.mind.payment.domain.request.HourlySalesRequest;
import any.mind.payment.domain.request.PaymentAdditionalItem;
import any.mind.payment.domain.response.ExecutePaymentResponse;
import any.mind.payment.domain.response.HourlySalesResponse;
import any.mind.payment.exception.BusinessException;
import any.mind.payment.exception.ErrorType;
import any.mind.payment.helper.MessageHelper;
import any.mind.payment.repository.PaymentRepository;

@ActiveProfiles("test")
@SpringBootTest
public class PaymentServiceTest {

    @MockitoBean
    private PaymentMethodService paymentMethodService;

    @MockitoBean
    private PaymentRepository repository;

    @Autowired
    private MessageHelper messageHelper;

    @Autowired
    private PaymentService paymentService;

    @Test
    void testExecutePayment_Success() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.1")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .last4("1234")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .pointsMultiplier(new BigDecimal("0.01"))
            .rule(RuleDTO.builder()
                .storeCreditCardLastFour(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);
        when(repository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExecutePaymentResponse response = paymentService.executePayment(request);

        assertThat(response).isNotNull();
        assertThat(response.finalPrice()).isEqualTo("110.00");
        assertThat(response.points()).isEqualTo(1);
    }

    @Test
    void testExecutePayment_InvalidPaymentMethod() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("INVALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .last4("1234")
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("INVALID_METHOD")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            paymentService.executePayment(request);
        });

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo("The provided payment method \"INVALID_METHOD\" is not valid.");
    }

    @Test
    void testExecutePayment_InvalidPriceModifier() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("2.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .last4("1234")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeCreditCardLastFour(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "The provided price modifier \"2.0\" is not valid for the payment method \"VALID_METHOD\".");
    }

    @Test
    void testExecutePayment_InvalidBankCode() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "Bank code must be provided (additionalItem.bankCode field).");
    }

    @Test
    void testExecutePayment_accountNumberNull() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .storeAccountNumber(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "Account number must be provided (additionalItem.accountNumber field).");
    }

    @Test
    void testExecutePayment_accountNumberEmpty() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .accountNumber("")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .storeAccountNumber(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "Account number must be provided (additionalItem.accountNumber field).");
    }

    @Test
    void testExecutePayment_checkNumberNull() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .storeCheckNumber(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "Check number must be provided (additionalItem.checkNumber field).");
    }

    @Test
    void testExecutePayment_checkNumberEmpty() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .chequeNumber("")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .storeCheckNumber(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "Check number must be provided (additionalItem.checkNumber field).");
    }

    @Test
    void testExecutePayment_creditCardLastFourNull() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .storeCreditCardLastFour(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "Credit card last four numbers must be provided (additionalItem.last4 field).");
    }

    @Test
    void testExecutePayment_invalidCreditCardLastFour_threeNumbers() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .last4("123")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .storeCreditCardLastFour(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "The provided credit card last four numbers is not valid.");
    }

    @Test
    void testExecutePayment_invalidCreditCardLastFour_notOnlyNumbers() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .last4("1a23")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .storeCreditCardLastFour(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "The provided credit card last four numbers is not valid.");
    }

    @Test
    void testExecutePayment_courierNull() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .last4("6547")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .storeBankCode(true)
                .storeCreditCardLastFour(true)
                .validateCourier(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "Courier must be provided (additionalItem.courier field).");
    }

    @Test
    void testExecutePayment_invalidCourier() {
        ExecutePaymentRequest request = ExecutePaymentRequest.builder()
            .customerId("customerId")
            .price("100.00")
            .priceModifier("1.0")
            .paymentMethod("VALID_METHOD")
            .datetime(LocalDateTime.now())
            .additionalItem(PaymentAdditionalItem.builder()
                .bankCode("026")
                .last4("6547")
                .courier("NOT_VALID_COURIER")
                .build())
            .build();

        PaymentMethodDTO paymentMethod = PaymentMethodDTO.builder()
            .name("VALID_METHOD")
            .priceMultiplierFrom(new BigDecimal("0.5"))
            .priceMultiplierTo(new BigDecimal("1.5"))
            .rule(RuleDTO.builder()
                .couriers(List.of("YAMATO", "SAWAGA"))
                .storeBankCode(true)
                .storeCreditCardLastFour(true)
                .validateCourier(true)
                .build())
            .build();

        when(paymentMethodService.getPaymentMethodByName("VALID_METHOD")).thenReturn(paymentMethod);

        BusinessException exception = assertThrows(BusinessException.class,
            () -> paymentService.executePayment(request));

        assertEquals(exception.getErrorType(), ErrorType.BUSINESS);
        assertThat(exception.getMessage()).isEqualTo(
            "The provided courier \"NOT_VALID_COURIER\" is not valid.");
    }

    @Test
    void testGetHourlySales_Success() {
        List<HourlySalesResultData> resultData = List.of(
            new HourlySalesResultData(LocalDateTime.now().plusHours(1), new BigDecimal("1500.50"), 35L),
            new HourlySalesResultData(LocalDateTime.now().plusHours(2), new BigDecimal("7500.50"), 97L));

        when(repository.findHourlySales(any(), any())).thenReturn(resultData);

        List<HourlySalesResponse> response = paymentService.getHourlySales(LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        for (int i = 0; i < response.size(); i++) {
            HourlySalesResponse rsp = response.get(i);
            assertEquals(resultData.get(i).getSales().toString(), rsp.sales());
            assertTrue(rsp.datetime().isEqual((LocalDateTime) resultData.get(i).getDatetime()));
            assertEquals((Long) resultData.get(i).getPoints(), resultData.get(i).getPoints());
        }
    }

}
