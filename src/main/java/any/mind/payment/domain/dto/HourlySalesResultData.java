package any.mind.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlySalesResultData {
    private Object datetime;
    private Object sales;
    private Object points;
}