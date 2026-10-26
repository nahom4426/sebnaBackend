package Medco.PremuimFinancing.Dto.Request.Settings;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PricePerShareUpdateRequest {

    @NotNull
    private BigDecimal pricePerShare;
}
