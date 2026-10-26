package Medco.PremuimFinancing.ServiceImpl.Settings;

import Medco.PremuimFinancing.Entity.Settings.AppSetting;
import Medco.PremuimFinancing.Repository.Settings.AppSettingRepository;
import Medco.PremuimFinancing.Service.Settings.AppSettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AppSettingServiceImpl implements AppSettingService {

    public static final String PRICE_PER_SHARE_KEY = "PRICE_PER_SHARE";
    private static final BigDecimal DEFAULT_PRICE_PER_SHARE = new BigDecimal("10000");

    private final AppSettingRepository appSettingRepository;

    public AppSettingServiceImpl(AppSettingRepository appSettingRepository) {
        this.appSettingRepository = appSettingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getPricePerShare() {
        return appSettingRepository.findByKey(PRICE_PER_SHARE_KEY)
                .map(s -> s.getValue() == null ? DEFAULT_PRICE_PER_SHARE : s.getValue())
                .orElse(DEFAULT_PRICE_PER_SHARE);
    }

    @Override
    @Transactional
    public BigDecimal setPricePerShare(BigDecimal pricePerShare) {
        if (pricePerShare == null) {
            throw new IllegalArgumentException("pricePerShare is required");
        }
        if (pricePerShare.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("pricePerShare must be greater than 0");
        }

        AppSetting s = appSettingRepository.findByKey(PRICE_PER_SHARE_KEY).orElseGet(() -> {
            AppSetting created = new AppSetting();
            created.setKey(PRICE_PER_SHARE_KEY);
            return created;
        });
        s.setValue(pricePerShare);
        appSettingRepository.save(s);
        return s.getValue();
    }
}
