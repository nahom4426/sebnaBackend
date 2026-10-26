package Medco.PremuimFinancing.Dto.Response;



import lombok.Getter;

import java.util.List;
@Getter
public class AmortizationResponse {

    private final List<Object> schedule;
    private final double totalInterestPaid;

    public AmortizationResponse(List<Object> schedule, double totalInterestPaid) {
        this.schedule = schedule;
        this.totalInterestPaid = totalInterestPaid;
    }

    public List<Object> getSchedule() {
        return schedule;
    }

    public double getTotalInterestPaid() {
        return totalInterestPaid;
    }
}
