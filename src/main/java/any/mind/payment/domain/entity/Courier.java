package any.mind.payment.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 250)
    private String description;

}

