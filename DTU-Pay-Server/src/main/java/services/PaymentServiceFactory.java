package services;

import messaging.implementations.RabbitMqQueue;

public class PaymentServiceFactory {
    static PaymentService service = null;

    public synchronized PaymentService getService() {
        if (service != null) {
            return service;
        }

        var mq = new RabbitMqQueue("rabbitMq");
        service = new PaymentService(mq);
        return service;
    }
}
