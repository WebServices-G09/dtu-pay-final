package dtu.dtuPay;


import messaging.implementations.RabbitMqQueue;
import dtu.dtuPay.services.CustomerService;
import dtu.dtuPay.services.MerchantService;

public class StartUp {
    public static void main(String[] args) throws Exception {
        new StartUp().startUp();
    }

    private void startUp() throws Exception {
        System.out.println("startup");
        String environment = System.getenv("Environment");
        String hostname = environment != null && environment.equalsIgnoreCase("development")
                ? "localhost" : "rabbitMq_container";

        var mq = new RabbitMqQueue(hostname);
        new CustomerService(mq);
        new MerchantService(mq);
    }
}
