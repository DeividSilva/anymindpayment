package any.mind.payment.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private BigDecimal priceMultiplierFrom;

    @Column(nullable = false)
    private BigDecimal priceMultiplierTo;

    @Column(nullable = false)
    private BigDecimal pointsMultiplier;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_rule", referencedColumnName = "id")
    private Rule rule;

    @Column(nullable = false)
    private boolean active;

}
