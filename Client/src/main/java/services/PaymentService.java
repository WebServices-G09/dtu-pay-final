package services;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import models.dtos.PaymentRequestDto;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import services.interfaces.IPaymentService;

public class PaymentService {
    ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
    ResteasyWebTarget baseURL = client.target("http://localhost:8080");
    IPaymentService service = baseURL.proxy(IPaymentService.class);

    public boolean pay(PaymentRequestDto payment) throws Exception {
        Response response = service.pay(payment);

        if (response.getStatus() != 200) {
            throw new Exception(response.readEntity(String.class));
        }

        return response.readEntity(Boolean.class);
    }

}
