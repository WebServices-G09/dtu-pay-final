package steps;

import dtu.dtuPay.models.Payment;
import dtu.dtuPay.repositeries.PaymentRepository;
import dtu.dtuPay.services.PaymentService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PaymentSteps {
    private MessageQueue queue = mock(MessageQueue.class);
    private PaymentService service = new PaymentService(queue);
    private PaymentRepository repository = PaymentRepository.getInstance();
    private UUID merchantId;
    private UUID customerToken;
    private double amount;
    private Payment expectedPayment;
    private List<Payment> expectedPaymentList;

    public PaymentSteps() {}

    // pay service test
    @When("{string} event to execute a payment is recived")
    public void eventToExecuteAPaymentIsRecived(String eventName) {
        // Needs to be updated for token and merchant id validation?
        customerToken = UUID.randomUUID();
        merchantId = UUID.randomUUID();
        amount = 20.0;

        Event event = new Event(eventName, new Object[] {customerToken, merchantId, amount});
        service.handlePaymentRequested(event);
    }

    @Then("the payment is executed and the {string} event is sent")
    public void thePaymentIsExecutedAndTheEventIsSent(String eventName) {
        expectedPayment = repository.getMerchantPayments(merchantId)
                .stream().filter(payment ->
                        payment.getCustomerToken().equals(customerToken) &&
                        payment.getAmount() == amount)
                .findFirst().orElse(null);

        Event event = new Event(eventName, new Object[] {expectedPayment});
        verify(queue).publish(event);
    }

    @Then("the payment confirmation is received by the merchant")
    public void thePaymentConfirmationIsReceivedByTheMerchant() {
        assertNotNull(expectedPayment.getId());
    }

    // getPayment service test
    @Given("a list of payments are present in the payment repository")
    public void aListOfPaymentsArePresentInThePaymentRepository() {
        expectedPaymentList = new ArrayList<>(){};
        expectedPaymentList.add(new Payment(UUID.randomUUID(),UUID.randomUUID(),10));
        expectedPaymentList.add(new Payment(UUID.randomUUID(),UUID.randomUUID(),20));
        expectedPaymentList.add(new Payment(UUID.randomUUID(),UUID.randomUUID(),13));

        for (Payment payment : expectedPaymentList) {
            repository.addPayment(payment);
        }
    }

    @When("{string} event to get all payments is received")
    public void eventToGetAllPaymentsIsReceived(String eventName) {
        Event event = new Event(eventName, new Object[] {});
        service.handleGetPaymentsRequested(event);
    }

    @Then("the payments are fetched and the {string} event is sent")
    public void thePaymentsAreFetchedAndTheEventIsSent(String eventName) {
        Event event = new Event(eventName, new Object[] {expectedPaymentList});
        verify(queue).publish(event);
    }

    @Then("the manager gets the list of payments")
    public void the_manager_gets_the_list_of_payments() {
        assertFalse(expectedPaymentList.isEmpty());
    }
}
