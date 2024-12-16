package any.mind.payment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import any.mind.payment.domain.dto.HourlySalesResultData;
import any.mind.payment.domain.response.HourlySalesResponse;
import any.mind.payment.domain.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT new any.mind.payment.domain.dto.HourlySalesResultData(
                    FUNCTION('date_trunc', 'hour', p.datetime),
                    SUM(p.finalPrice),
                    SUM(p.points)
                )
                FROM Payment p
                WHERE p.datetime BETWEEN :startDateTime AND :endDateTime
                GROUP BY FUNCTION('date_trunc', 'hour', p.datetime)
                ORDER BY FUNCTION('date_trunc', 'hour', p.datetime)
    """)
    List<HourlySalesResultData> findHourlySales(
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );
}