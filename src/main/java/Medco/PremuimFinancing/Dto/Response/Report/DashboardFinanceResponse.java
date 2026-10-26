package Medco.PremuimFinancing.Dto.Response.Report;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DashboardFinanceResponse {
    private BigDecimal pricePerShare;

    private BigDecimal costBasisTotalExpectedAmount;
    private BigDecimal costBasisTotalRemainingAmount;

    private BigDecimal marketTotalValue;
    private BigDecimal marketUnrealizedGainAmount;
    private BigDecimal marketUnrealizedGainPercent;

    private BigDecimal totalSharesSold;
    private BigDecimal totalExpectedAmount;
    private BigDecimal totalDepositedAmount;
    private BigDecimal totalRemainingAmount;

    private long shareholdersCount;
    private long fullyPaidShareholdersCount;
    private long outstandingShareholdersCount;

    private BigDecimal collectionRatePercent;
    private BigDecimal avgSharesPerShareholder;
    private BigDecimal avgPaidPerShareholder;
}
