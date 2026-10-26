package Medco.PremuimFinancing.Dto.Response.Report;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PieSeriesResponse {
    private String key;
    private String title;
    private List<PieSliceResponse> slices;
}
