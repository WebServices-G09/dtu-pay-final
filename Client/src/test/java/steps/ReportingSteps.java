package steps;

import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.dtos.Payment;
import models.dtos.PaymentRequestDto;
import models.dtos.TokenRequestDto;
import models.dtos.UserRequestDto;
import services.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReportingSteps
{
    User userCustomer;
    User userMerchant;
    private UUID customerId;
    private UUID merchantId;
    private String customerBankAccountId;
    private String merchantBankAccountId;
    private String exceptionMessage;

    List<Payment> payments;
    List<Payment> merchantPayments;
    List<Payment> customerPayments;

    private CustomerService customerService = new CustomerService();
    private MerchantService merchantService = new MerchantService();
    private ReportingService reportingService = new ReportingService();
    private PaymentService paymentService = new PaymentService();
    private TokenService tokenService = new TokenService();
    BankServiceImplementation bankService = new BankServiceImplementation();

    @Given("customer with name {string}, last name {string}, and CPR {string} is registered with DTU Pay")
    public void customer_with_name_last_name_and_cpr_is_registered_with_dtu_pay(String firstName, String lastName, String cpr) throws Exception
    {
        userCustomer = new User();
        userCustomer.setFirstName(firstName);
        userCustomer.setLastName(lastName);
        userCustomer.setCprNumber(cpr);

        try
        {
            customerBankAccountId = bankService.createAccount(
                    userCustomer.getFirstName(),
                    userCustomer.getLastName(),
                    userCustomer.getCprNumber(),
                    new BigDecimal(1000)
            );
        }
        catch (BankServiceException_Exception e)
        {
            customerBankAccountId = bankService.getAccountByCPR(userCustomer.getCprNumber()).getId();
        }

        UserRequestDto payloadUser = new UserRequestDto();
        payloadUser.setFirstName(userCustomer.getFirstName());
        payloadUser.setLastName(userCustomer.getLastName());
        payloadUser.setCpr(userCustomer.getCprNumber());
        payloadUser.setBankAccountId(customerBankAccountId);

        customerId = customerService.createCustomer(payloadUser);
        assertNotNull(customerId, "Customer ID should not be null");

        int nTokensCreated = tokenService.createTokens(new TokenRequestDto(customerId, 5));
        assertEquals(nTokensCreated, 5);
    }

    @Given("merchant with name {string}, last name {string}, and CPR {string} is registered with DTU Pay")
    public void merchant_with_name_last_name_and_cpr_is_registered_with_dtu_pay(String firstName, String lastName, String cpr) throws Exception
    {
        userMerchant = new User();
        userMerchant.setFirstName(firstName);
        userMerchant.setLastName(lastName);
        userMerchant.setCprNumber(cpr);

        try
        {
            merchantBankAccountId = bankService.createAccount(
                    userMerchant.getFirstName(),
                    userMerchant.getLastName(),
                    userMerchant.getCprNumber(),
                    new BigDecimal(1000)
            );
        }
        catch (BankServiceException_Exception e)
        {
            merchantBankAccountId = bankService.getAccountByCPR(userMerchant.getCprNumber()).getId();
        }

        UserRequestDto payloadUser = new UserRequestDto();
        payloadUser.setFirstName(userMerchant.getFirstName());
        payloadUser.setLastName(userMerchant.getLastName());
        payloadUser.setCpr(userMerchant.getCprNumber());
        payloadUser.setBankAccountId(merchantBankAccountId);

        merchantId = merchantService.createMerchant(payloadUser);
        assertNotNull(merchantId, "Customer ID should not be null");
    }

    @Given("a list of payments are present in the repository")
    public void a_list_of_payments_are_present_in_he_repository() throws Exception
    {
        for(int i = 0; i < 4; ++i)
        {
            UUID customerToken = tokenService.getTokens(customerId).getFirst();
            paymentService.pay(new PaymentRequestDto(customerToken, merchantId, 1));
        }
    }

    @When("a manager requests all reports")
    public void a_manager_requests_all_reports() throws Exception
    {
        payments = reportingService.getAllPayments();
    }

    @Then("a report with all payments is returned to the manager")
    public void a_report_with_all_payments_is_returned_to_the_manager()
    {
        assertEquals(4, payments.size());
    }

    @When("merchant requests a report of all his payments")
    public void merchant_requests_a_report_of_all_his_payments() throws Exception
    {
        merchantPayments = reportingService.getMerchantPayments(merchantId);
    }

    @When("customer requests a report of all his payments")
    public void customerRequestsAReportOfAllHisPayments() throws Exception
    {
        customerPayments = reportingService.getCustomerPayments(customerId);
    }

    @Then("a report with all payments involving the customer is returned")
    public void a_report_with_all_payments_involving_the_customer_is_returned() throws Exception
    {
        assertEquals(4, customerPayments.size());
    }

    @Then("a report with all payments made by the merchant is returned")
    public void aReportWithAllPaymentsMadeByTheMerchantIsReturned()
    {
        assertEquals(4, merchantPayments.size());
    }

}
