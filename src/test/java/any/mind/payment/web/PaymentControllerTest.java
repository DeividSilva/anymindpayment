package any.mind.payment.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import any.mind.payment.domain.request.ExecutePaymentRequest;
import any.mind.payment.domain.request.PaymentAdditionalItem;
import any.mind.payment.domain.response.ExecutePaymentResponse;
import any.mind.payment.helper.MessageHelper;
import any.mind.payment.service.PaymentService;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageHelper messageHelper;

    @Test
    void whenRequestIsValid_thenReturnsOk() throws Exception {
        ExecutePaymentRequest validRequest = ExecutePaymentRequest.builder()
            .customerId("12345")
            .price("1000.23")
            .priceModifier("0.5")
            .paymentMethod("MASTERCARD")
            .datetime(LocalDateTime.parse("2024-12-25T10:25:34"))
            .additionalItem(PaymentAdditionalItem.builder()
                .last4("1234")
                .build())
            .build();

        ExecutePaymentResponse mockResponse = ExecutePaymentResponse.builder()
            .finalPrice("500.12")
            .points(20)
            .build();

        when(paymentService.executePayment(any(ExecutePaymentRequest.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/api/payment")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.finalPrice").value("500.12"))
            .andExpect(jsonPath("$.points").value(20));
    }

    @Test
    void whenRequestHasMissingFields_thenReturnsBadRequest() throws Exception {
        ExecutePaymentRequest invalidRequest = ExecutePaymentRequest.builder()
            .price("1000.23")
            .priceModifier("0.5")
            .paymentMethod("MASTERCARD")
            .datetime(LocalDateTime.parse("2024-12-25T10:25:34"))
            .additionalItem(PaymentAdditionalItem.builder()
                .last4("1234")
                .build())
            .build();

        mockMvc.perform(post("/api/payment")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.message").value("The parameter \"customerId\" must be provided."));
    }

    @Test
    void whenAdditionalItemIsNullForCash_thenReturnsOk() throws Exception {
        ExecutePaymentRequest validRequest = ExecutePaymentRequest.builder()
            .customerId("12345")
            .price("1000.23")
            .priceModifier("0.5")
            .paymentMethod("CASH")
            .datetime(LocalDateTime.parse("2024-12-25T10:25:34"))
            .additionalItem(null)
            .build();

        ExecutePaymentResponse mockResponse = ExecutePaymentResponse.builder()
            .finalPrice("500.00")
            .points(100)
            .build();

        when(paymentService.executePayment(any(ExecutePaymentRequest.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/api/payment")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.finalPrice").value("500.00"))
            .andExpect(jsonPath("$.points").value(100));
    }

}