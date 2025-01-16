package dtu.dtuPay.services;

import dtu.dtuPay.models.Payment;
import dtu.dtuPay.repositeries.PaymentRepository;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

import java.util.List;
import java.util.UUID;

public class PaymentService {

    private static final String GET_PAYMENTS_REQUESTED = "GetPaymentsRequested";
    private static final String PAYMENTS_FETCHED = "PaymentsFetched";
    private static final String GET_CUSTOMER_PAYMENTS_REQUESTED = "GetCustomerPaymentsRequested";
    private static final String CUSTOMER_PAYMENTS_FETCHED = "CustomerPaymentsFetched";
    private static final String GET_MERCHANT_PAYMENTS_REQUESTED = "GetMerchantPaymentsRequested";
    private static final String MERCHANT_PAYMENTS_FETCHED = "MerchantPaymentsFetched";
    private static final String PAYMENT_REQUESTED = "PaymentRequested";
    private static final String PAYMENT_COMPLETED = "PaymentCompleted";
    MessageQueue queue;
    PaymentRepository paymentRepository = PaymentRepository.getInstance();

    public PaymentService(RabbitMqQueue mq) {
        this.queue = mq;
        // Get payments
        this.queue.addHandler(GET_PAYMENTS_REQUESTED, this::handleGetPaymentsRequested);
        // Get customerPayments
        this.queue.addHandler(GET_CUSTOMER_PAYMENTS_REQUESTED, this::handleGetCustomerPaymentsRequested);
        // Get merchantPayments
        this.queue.addHandler(GET_MERCHANT_PAYMENTS_REQUESTED, this::handleGetMerchantPaymentsRequested);
        // Request payment
        this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);
    }

    public void handlePaymentRequested(Event ev) {

        Event event = new Event(PAYMENT_COMPLETED, new Object[] { });
        queue.publish(event);
    }

    public void handleGetMerchantPaymentsRequested(Event ev) {
        UUID merchantId = ev.getArgument(0, UUID.class);
        List<Payment> paymentList = paymentRepository.getMerchantPayments(merchantId);

        Event event = new Event(MERCHANT_PAYMENTS_FETCHED, new Object[] {paymentList});
        queue.publish(event);
    }

    public void handleGetCustomerPaymentsRequested(Event ev) {
        UUID customerId = ev.getArgument(0, UUID.class);
        List<Payment> paymentList = paymentRepository.getCustomerPayments(customerId);

        Event event = new Event(CUSTOMER_PAYMENTS_FETCHED, new Object[] {paymentList});
        queue.publish(event);
    }

    public void handleGetPaymentsRequested(Event ev) {
        List<Payment> paymentList = paymentRepository.getPayments();

        Event event = new Event(PAYMENTS_FETCHED, new Object[] {paymentList});
        queue.publish(event);
    }

}
