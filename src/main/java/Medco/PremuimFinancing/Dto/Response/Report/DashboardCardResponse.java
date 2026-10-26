package Medco.PremuimFinancing.Dto.Response.Report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardCardResponse {
    private String key;
    private String title;
    private long value;
    private Long previousValue;
    private Double changePercent;
}
