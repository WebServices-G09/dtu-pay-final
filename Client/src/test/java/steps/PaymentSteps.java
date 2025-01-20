package steps;

import dtu.ws.fastmoney.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.dtos.UserRequestDto;
import services.BankServiceImplementation;
import services.CustomerService;
import services.MerchantService;
import services.PaymentService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentSteps {
    User userCustomer;
    User userMerchant;
    private String accountId;
    private UUID customerId;
    private UUID merchantId;
    private boolean paymentIsSuccessfull;
    BankServiceImplementation bankService = new BankServiceImplementation();

    private static List<String> createdAccountIds = new ArrayList<>();
    private CustomerService customerService = new CustomerService();
    private MerchantService merchantService = new MerchantService();
    private PaymentService paymentService = new PaymentService();

    @io.cucumber.java.After
    public void cleanupAccounts() {
        for (String accountId : createdAccountIds) {
            bankService.deleteAccount(accountId);
        }

        createdAccountIds.clear();
    }

    @Given("customer with name {string}, last name {string}, and CPR {string}")
    public void customer_with_name_last_name_and_cpr(String firstName, String lastName, String cpr) {
        userCustomer = new User();
        userCustomer.setFirstName(firstName);
        userCustomer.setLastName(lastName);
        userCustomer.setCprNumber(cpr);
    }

    @Given("customer is registered with the bank with an initial balance of {double} kr")
    public void customer_is_registered_with_the_bank_with_an_initial_balance_of_kr(Double balance) {
        accountId = bankService.createAccount(
                userCustomer.getFirstName(),
                userCustomer.getLastName(),
                userCustomer.getCprNumber(),
                new BigDecimal(balance)
        );

        createdAccountIds.add(accountId);
    }

    @Given("customer is registered with Simple DTU Pay using their bank account")
    public void customer_is_registered_with_simple_dtu_pay_using_their_bank_account() {
        UserRequestDto payloadUser = new UserRequestDto();
        payloadUser.setFirstName(userCustomer.getFirstName());
        payloadUser.setLastName(userCustomer.getLastName());
        payloadUser.setCpr(userCustomer.getCprNumber());
        payloadUser.setBankAccountNumber(accountId);

        customerId = customerService.createCustomer(payloadUser);
        assertNotNull(customerId, "Customer ID should not be null");
    }

    @Given("merchant with name {string}, last name {string}, and CPR {string}")
    public void merchant_with_name_last_name_and_cpr(String firstName, String lastName, String cpr) {
        userMerchant = new User();
        userMerchant.setFirstName(firstName);
        userMerchant.setLastName(lastName);
        userMerchant.setCprNumber(cpr);
    }

    @Given("merchant is registered with the bank with an initial balance of {double} kr")
    public void merchant_is_registered_with_the_bank_with_an_initial_balance_of_kr(Double balance) {
        accountId = bankService.createAccount(
                userMerchant.getFirstName(),
                userMerchant.getLastName(),
                userMerchant.getCprNumber(),
                new BigDecimal(balance)
        );

        createdAccountIds.add(accountId);
    }

    @Given("merchant is registered with Simple DTU Pay using their bank account")
    public void merchant_is_registered_with_simple_dtu_pay_using_their_bank_account() {
        UserRequestDto payloadUser = new UserRequestDto();
        payloadUser.setFirstName(userMerchant.getFirstName());
        payloadUser.setLastName(userMerchant.getLastName());
        payloadUser.setCpr(userMerchant.getCprNumber());
        payloadUser.setBankAccountNumber(accountId);

        merchantId = merchantService.createMerchant(payloadUser);
        assertNotNull(merchantId, "merchant ID should not be null");
    }

    @When("the merchant initiates a payment for {int} kr by the customer")
    public void the_merchant_initiates_a_payment_for_kr_by_the_customer(Integer amount) {
        paymentIsSuccessfull = paymentService.pay(customerToken, merchantId, amount);
    }

    @Then("the payment is successful")
    public void the_payment_is_successful() {
        assertTrue(paymentIsSuccessfull);
    }

    @Then("the balance of the customer at the bank is {int} kr")
    public void the_balance_of_the_customer_at_the_bank_is_kr(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the balance of the merchant at the bank is {int} kr")
    public void the_balance_of_the_merchant_at_the_bank_is_kr(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
