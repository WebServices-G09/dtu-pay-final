package dtu.dtuPay.services;

import dtu.dtuPay.repositeries.PaymentRepository;
import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;

public class PaymentService {

    MessageQueue queue;
    PaymentRepository paymentRepository = PaymentRepository.getInstance();

    public PaymentService(RabbitMqQueue mq) {
        this.queue = mq;
        // Get payments
        this.queue.addHandler("GetPaymentsRequested", this::handleGetPaymentsRequested);
        // Get customerPayments
        this.queue.addHandler("GetCustomerPaymentsRequested", this::handleGetCustomerPaymentsRequested);
        // Get merchantPayments
        this.queue.addHandler("GetMerchantPaymentsRequested", this::handleGetMerchantPaymentsRequested);
        // Request payment
        this.queue.addHandler("PaymentRequested", this::handlePaymentRequested);
    }

    private void handlePaymentRequested(Event event) {
    }

    private void handleGetMerchantPaymentsRequested(Event event) {
    }

    private void handleGetCustomerPaymentsRequested(Event event) {
    }

    private void handleGetPaymentsRequested(Event event) {
    }

}
