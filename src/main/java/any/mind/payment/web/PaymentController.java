package any.mind.payment.web;

import any.mind.payment.domain.request.ExecutePaymentRequest;
import any.mind.payment.domain.response.ExecutePaymentResponse;
import any.mind.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService service;

    @ResponseStatus(OK)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ExecutePaymentResponse executePayment(@RequestBody @Validated final ExecutePaymentRequest request) {
        return service.executePayment(request);
    }

}
