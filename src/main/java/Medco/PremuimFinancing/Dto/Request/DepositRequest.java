package Medco.PremuimFinancing.Dto.Request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DepositRequest {
    private String quotationUuid;
    private String receiptNumber;
    private String paymentType;
    private  String paymentMethod;
    private double amount;

}
