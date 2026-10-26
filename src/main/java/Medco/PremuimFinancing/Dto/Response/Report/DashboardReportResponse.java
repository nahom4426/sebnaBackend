package Medco.PremuimFinancing.Dto.Response.Report;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class DashboardReportResponse {
    private String range;
    private String scope;
    private Instant generatedAt;

    private DashboardFinanceResponse finance;

    private List<DashboardCardResponse> cards;
    private List<BarSeriesResponse> barSeries;
    private List<PieSeriesResponse> pieSeries;
}
