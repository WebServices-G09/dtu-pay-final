package steps;

import dtu.dtuPay.models.Payment;
import dtu.dtuPay.services.PaymentService;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PaymentSteps {
    private MessageQueue queue = mock(MessageQueue.class);
    private PaymentService service = new PaymentService(queue);
    private List<Payment> expectedPayments;

    public PaymentSteps() {}

    @When("{string} event to get all payments is received")
    public void eventToGetAllPaymentsIsReceived(String eventName) {
        Event event = new Event(eventName, new Object[] {});
        service.handleGetPaymentsRequested(event);
    }

    @Then("the payments are fetched and the {string} event is sent")
    public void thePaymentsAreFetchedAndTheEventIsSent(String eventName) {
        expectedPayments = new ArrayList<>(){};
//        expectedPayments.add(new Payment(UUID.randomUUID(),UUID.randomUUID(),10));
//        expectedPayments.add(new Payment(UUID.randomUUID(),UUID.randomUUID(),20));
//        expectedPayments.add(new Payment(UUID.randomUUID(),UUID.randomUUID(),13));

        Event event = new Event(eventName, new Object[] {expectedPayments});
        verify(queue).publish(event);
    }

    @Then("the manager gets the list of payments")
    public void the_manager_gets_the_list_of_payments() {
        assertTrue(expectedPayments.isEmpty());
    }
}
