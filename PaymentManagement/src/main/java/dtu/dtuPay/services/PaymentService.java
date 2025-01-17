package dtu.dtuPay.services;

import dtu.dtuPay.models.Payment;
import dtu.dtuPay.repositeries.PaymentRepository;
import messaging.Event;
import messaging.MessageQueue;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentService {

    private static final String GET_PAYMENTS_REQUESTED = "GetPaymentsRequested";
    private static final String PAYMENTS_FETCHED = "PaymentsFetched";
    private static final String GET_CUSTOMER_PAYMENTS_REQUESTED = "GetCustomerPaymentsRequested";
    private static final String CUSTOMER_PAYMENTS_FETCHED = "CustomerPaymentsFetched";
    private static final String GET_MERCHANT_PAYMENTS_REQUESTED = "GetMerchantPaymentsRequested";
    private static final String MERCHANT_PAYMENTS_FETCHED = "MerchantPaymentsFetched";
    private static final String PAYMENT_REQUESTED = "PaymentRequested";
    private static final String PAYMENT_COMPLETED = "PaymentCompleted";

    // External events
    private static final String TOKEN_VALIDATION_REQUESTED = "TokenValidationRequest";
    private static final String TOKEN_VALIDATION_RETURNED = "TokenValidationReturned";
    private static final String VALIDATE_MERCHANT_ACCOUNT_REQUESTED = "ValidateMerchantAccountRequested";
    private static final String MERCHANT_ACCOUNT_VALIDATION_RESPONSE = "MerchantAccountValidationResponse";

    private static final String USE_TOKEN_REQUEST = "UseTokenRequest";
    private static final String USE_TOKEN_RESPONSE = "UseTokenResponse";

    private MessageQueue queue;
    private PaymentRepository paymentRepository = PaymentRepository.getInstance();
    private Map<CorrelationId, CompletableFuture<Boolean>> correlations = new ConcurrentHashMap<>();
    private Map<CorrelationId, CompletableFuture<UUID>> customerTokenValidation = new ConcurrentHashMap<>();

    public PaymentService(MessageQueue mq) {
        this.queue = mq;
        // Get payments
        this.queue.addHandler(GET_PAYMENTS_REQUESTED, this::handleGetPaymentsRequested);
        // Get customerPayments
        this.queue.addHandler(GET_CUSTOMER_PAYMENTS_REQUESTED, this::handleGetCustomerPaymentsRequested);
        // Get merchantPayments
        this.queue.addHandler(GET_MERCHANT_PAYMENTS_REQUESTED, this::handleGetMerchantPaymentsRequested);
        // Request payment
        this.queue.addHandler(PAYMENT_REQUESTED, this::handlePaymentRequested);

        this.queue.addHandler(TOKEN_VALIDATION_RETURNED, this::handleTokenValidationReturned);
        this.queue.addHandler(USE_TOKEN_RESPONSE, this::handleUseTokenResponse);
        this.queue.addHandler(MERCHANT_ACCOUNT_VALIDATION_RESPONSE, this::handleMerchantAccountValidationResponse);
    }

    private void handleMerchantAccountValidationResponse(Event event) {
        boolean merchantIdValidated = event.getArgument(1, boolean.class);

    }

    private void handleUseTokenResponse(Event event) {

    }

    private void publishPaymentFailureException(Exception exception) {
        Event failureEvent = new Event(PAYMENT_COMPLETED, new Object[] { false, exception});
        queue.publish(failureEvent);
    }

    private void validateCustomerToken(UUID customerToken, UUID paymentId) {
        CorrelationId customerValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<UUID> futureCustomerTokenValidation = new CompletableFuture<>();
        customerTokenValidation.put(customerValidationCorrelationId, futureCustomerTokenValidation);

        Event customerTokenValidationEvent = new Event(TOKEN_VALIDATION_REQUESTED,
                new Object[] { customerToken, customerValidationCorrelationId });
        queue.publish(customerTokenValidationEvent);

        futureCustomerTokenValidation.whenComplete((responseCustomerId, throwable) -> {
            if (throwable != null || responseCustomerId == null) {
                Exception exception = new Exception("Customer Token Validation Failed");
                publishPaymentFailureException(exception);
            } else {
                paymentRepository.addCustomerPayment(responseCustomerId, paymentId);
            }
        });
    }

    public void handlePaymentRequested(Event ev) {
        boolean isPaymentSuccess = false;
        UUID customerToken = ev.getArgument(0, UUID.class);
        UUID merchantId = ev.getArgument(1, UUID.class);
        double amount = ev.getArgument(2, double.class);

        Payment payment = new Payment(customerToken, merchantId, amount);

        // Merchant ID validation
        CorrelationId merchantValidationCorrelationId = CorrelationId.randomId();
        CompletableFuture<Boolean> futureMerchantValidation = new CompletableFuture<>();
        correlations.put(merchantValidationCorrelationId, futureMerchantValidation);

        Event merchantAccountValidationEvent = new Event(VALIDATE_MERCHANT_ACCOUNT_REQUESTED,
                new Object[] { merchantId, merchantValidationCorrelationId });
        queue.publish(merchantAccountValidationEvent);

        futureMerchantValidation.whenComplete((result, throwable) -> {
            if (throwable != null || result == null) {
                Exception exception = new Exception("Merchant Account Validation Failed");
                publishPaymentFailureException(exception);
            }
        });

        // Customer Token Validation
        validateCustomerToken(customerToken, payment.getId());

        // Mark Token as used
        CorrelationId useTokenCorrelationId = CorrelationId.randomId();
        CompletableFuture<Boolean> futureUseToken = new CompletableFuture<>();
        correlations.put(useTokenCorrelationId, futureUseToken);

        Event useTokenEvent = new Event(USE_TOKEN_REQUEST, new Object[] { customerToken, useTokenCorrelationId });
        queue.publish(useTokenEvent);

        futureUseToken.whenComplete((isTokenUsed, throwable) -> {
            if (throwable != null || !isTokenUsed) {
                Exception exception = new Exception("Use Token Failed");
                publishPaymentFailureException(exception);
            }
        });

        paymentRepository.addMerchantPayment(merchantId, payment.getId());

        paymentRepository.addPayment(payment);
    }

    private void handleTokenValidationReturned(Event ev) {
        UUID customerId = ev.getArgument(0, UUID.class);
        boolean isValid = ev.getArgument(1, boolean.class);
        CorrelationId correlationId = ev.getArgument(2, CorrelationId.class);

        if (!isValid) {
            correlations.get(correlationId).complete(null);
            Event event = new Event(PAYMENT_COMPLETED, new Object[] { success });
        }


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
