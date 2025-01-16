package services.interfaces;

import java.math.BigDecimal;

import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankServiceException_Exception;
public interface IBankService {

    String createAccount(String firstName, String lastName, String cpr, BigDecimal initialBalance);
    Account getAccount(String accountId) throws BankServiceException_Exception;

    void deleteAccount(String accountId);
}
