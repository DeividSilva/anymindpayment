package any.mind.payment.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import any.mind.payment.domain.dto.PaymentMethodDTO;
import any.mind.payment.repository.PaymentMethodRepository;
import any.mind.payment.util.mapper.PaymentMethodMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private final PaymentMethodRepository repository;

    @Cacheable(cacheNames = "paymentMethods", unless="#result == null")
    public PaymentMethodDTO getPaymentMethodByName(String name) {
        return PaymentMethodMapper.toDTO(repository.getByName(name).orElse(null));
    }

}
