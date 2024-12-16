package any.mind.payment.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_payment_method")
    private Long idPaymentMethod;

    @Column(nullable = false)
    private BigDecimal initialPrice;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @Column(nullable = false)
    private BigDecimal priceModifier;

    @Column(nullable = false)
    private BigDecimal pointsMultiplier;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false, length = 36)
    private String idCustomer;

    @Column(nullable = false)
    private LocalDateTime datetime;

    @Column
    private String creditCardLastFour;

    @Column
    private String bankCode;

    @Column
    private String accountNumber;

    @Column
    private String checkNumber;

    @Column
    private String courier;

}
