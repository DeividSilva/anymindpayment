package any.mind.payment.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import any.mind.payment.domain.request.HourlySalesRequest;
import any.mind.payment.domain.response.HourlySalesResponse;
import any.mind.payment.helper.MessageHelper;
import any.mind.payment.service.PaymentService;

@WebMvcTest(PaymentAdminController.class)
class PaymentAdminControllerTest {

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
        HourlySalesRequest request = HourlySalesRequest.builder()
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.now().plusDays(1))
            .build();

        List<HourlySalesResponse> response = List.of(
            new HourlySalesResponse(LocalDateTime.now().plusHours(1), "1500.50", 35L),
            new HourlySalesResponse(LocalDateTime.now().plusHours(2), "7500.50", 97L));

        when(paymentService.getHourlySales(any(), any()))
            .thenReturn(response);

        mockMvc.perform(post("/api/admin/sales")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].datetime").isNotEmpty())
            .andExpect(jsonPath("$[0].sales").value(response.getFirst().sales()))
            .andExpect(jsonPath("$[0].points").value(response.getFirst().points()))
            .andExpect(jsonPath("$[1].datetime").isNotEmpty())
            .andExpect(jsonPath("$[1].sales").value(response.get(1).sales()))
            .andExpect(jsonPath("$[1].points").value(response.get(1).points()));
    }

}
