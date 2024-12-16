package any.mind.payment.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private boolean storeCreditCardLastFour;

    @Column(nullable = false)
    private boolean storeBankCode;

    @Column(nullable = false)
    private boolean storeAccountNumber;

    @Column(nullable = false)
    private boolean storeCheckNumber;

    @Column(nullable = false)
    private boolean validateCourier;

    @Column(length = 250)
    private String description;

    @ManyToMany
    @JoinTable(
        name = "courier_rule",
        joinColumns = @JoinColumn(name = "id_rule"),
        inverseJoinColumns = @JoinColumn(name = "id_courier")
    )
    private List<Courier> couriers;

}

