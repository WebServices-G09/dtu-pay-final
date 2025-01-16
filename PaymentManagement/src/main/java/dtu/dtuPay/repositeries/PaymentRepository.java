package dtu.dtuPay.repositeries;

import dtu.dtuPay.models.Payment;

import java.util.HashMap;
import java.util.UUID;

public class PaymentRepository {
    private static PaymentRepository instance;
    private HashMap<UUID, Payment> payments;

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
}
