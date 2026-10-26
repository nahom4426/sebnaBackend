package Medco.PremuimFinancing.Dto.Response.User;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MyShareStatsResponse {
    private String userUuid;
    private String roleName;

    private String firstName;
    private String fatherName;
    private String userName;
    private String profilePicture;

    private BigDecimal sharesBoughtAmount;
    private BigDecimal pricePerShare;
    private BigDecimal purchasePricePerShare;
    private BigDecimal totalCost;
    private BigDecimal amountPaid;
    private BigDecimal amountRemaining;

    private BigDecimal currentMarketValue;
    private BigDecimal valueIncreaseAmount;
    private BigDecimal valueIncreasePercent;

    private BigDecimal paymentProgressPercent;
    private String paymentStatus;
}
