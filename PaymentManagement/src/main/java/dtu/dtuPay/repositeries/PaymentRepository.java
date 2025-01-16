package dtu.dtuPay.repositeries;

import dtu.dtuPay.models.Payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PaymentRepository {
    private static PaymentRepository instance;
    private HashMap<UUID, Payment> payments;
    // <UUID paymentId, UUID customerId>
    private HashMap<UUID, List<UUID>> customerPayments;
    // <UUID paymentId, UUID merchantId>
    private HashMap<UUID, List<UUID>> merchantPayments;

    private PaymentRepository() {
        this.payments = new HashMap<>();
    }

    public static PaymentRepository getInstance() {
        if (instance == null) {
            synchronized (PaymentRepository.class) {
                if (instance == null) {
                    instance = new PaymentRepository();
                }
            }
        }
        return instance;
    }

    public void addPayment(Payment payment) {
        this.payments.put(payment.getId(), payment);
    }

    public List<Payment> getPayments() {
        List<Payment> paymentList = new ArrayList<>(this.payments.values());
        return paymentList;
    }

    public List<Payment> getCustomerPayments(UUID customerId) {
        List<UUID> customerPaymnetsIdList = customerPayments.get(customerId);
        List<Payment> paymentList = getPayments().stream().filter(
                payment -> customerPaymnetsIdList.contains(payment.getId())
        ).toList();

        return paymentList;
    }

    public List<Payment> getMerchantPayments(UUID merchantId) {
        List<UUID> merchantPaymnetsIdList = merchantPayments.get(merchantId);
        List<Payment> paymentList = getPayments().stream().filter(
                payment -> merchantPaymnetsIdList.contains(payment.getId())
        ).toList();

        return paymentList;
    }
}
