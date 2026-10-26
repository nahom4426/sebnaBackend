package Medco.PremuimFinancing.Service.Settings;

import java.math.BigDecimal;

public interface AppSettingService {
    BigDecimal getPricePerShare();

    BigDecimal setPricePerShare(BigDecimal pricePerShare);
}
