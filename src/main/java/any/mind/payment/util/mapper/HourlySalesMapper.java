package any.mind.payment.util.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import any.mind.payment.domain.dto.HourlySalesResultData;
import any.mind.payment.domain.response.HourlySalesResponse;

public class HourlySalesMapper {

    public static HourlySalesResponse toResponse(HourlySalesResultData data) {
        return HourlySalesResponse.builder()
            .sales(data.getSales().toString())
            .datetime((LocalDateTime) data.getDatetime())
            .points((Long) data.getPoints())
            .build();
    }

}
