package any.mind.payment.domain.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record HourlySalesRequest(
    @NotNull(message = "The parameter 'startDateTime' must be provided.")
    @Schema(description = "Date and time marking the start of the search.", example = "2024-12-10T00:25:34Z", pattern = "yyyy-MM-dd'T'HH:mm:ss'Z")
    LocalDateTime startDateTime,

    @NotNull(message = "The parameter 'endDateTime' must be provided.")
    @Schema(description = "Date and time marking the end of the search.", example = "2024-12-25T10:25:34Z", pattern = "yyyy-MM-dd'T'HH:mm:ss'Z")
    LocalDateTime endDateTime) {

}
