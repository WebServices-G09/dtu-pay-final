package dtu.dtuPay.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Data
public class AccountEventMessage {

    private UUID merchantId;
    private UUID customerId;
    private String merchantBankAccount;
    private String customerBankAccount;
    private Boolean isMerchantValid;
    private int requestResponseCode;
    private String exceptionMessage;

    public AccountEventMessage() {}
}
