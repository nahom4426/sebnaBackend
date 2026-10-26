package Medco.PremuimFinancing.Dto.Response.Report;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BarSeriesResponse {
    private String key;
    private String title;
    private List<ChartPointResponse> points;
}
