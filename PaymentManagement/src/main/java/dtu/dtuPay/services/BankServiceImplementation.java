package dtu.dtuPay.services;

import dtu.fastmoney.BankServiceService;
import dtu.ws.fastmoney.BankService;

import java.math.BigDecimal;

public class BankServiceImplementation {
    BankServiceService bankServiceService = new BankServiceService();
    BankService bankService = bankServiceService.getBankServicePort();

    public void transferMoney(String debtorAccountId, String creditorAccountId, BigDecimal amount, String description)
            throws dtu.ws.fastmoney.BankServiceException_Exception {
            bankService.transferMoneyFromTo(debtorAccountId, creditorAccountId, amount, description);
    }


}