package any.mind.payment.web;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import any.mind.payment.domain.request.HourlySalesRequest;
import any.mind.payment.domain.response.HourlySalesResponse;
import any.mind.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class PaymentAdminController {

    private final PaymentService service;

    @ResponseStatus(OK)
    @PostMapping(value = "/sales", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<HourlySalesResponse> getHourlySales(@RequestBody @Validated final HourlySalesRequest request) {
        return service.getHourlySales(request.startDateTime(), request.endDateTime());
    }

}
