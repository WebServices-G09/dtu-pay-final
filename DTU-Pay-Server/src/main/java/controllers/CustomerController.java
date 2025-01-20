package controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import models.dtos.CreateCustomerDto;
import services.customer.CustomerService;
import services.customer.CustomerServiceFactory;

import java.util.UUID;

@Path("customers")
public class CustomerController {
    CustomerService service = new CustomerServiceFactory().getService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCustomer(CreateCustomerDto customerRequest) {
        System.out.println("Registering Customer");
        try {
            var newCustomer = service.createCustomer(customerRequest);

            return Response.status(Response.Status.OK)
                    .entity(newCustomer)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Customer creation failed")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @DELETE
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("customerId") UUID id) {
        boolean isDeleted = service.deregisterCustomer(id);

        if (!isDeleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Customer does not exist")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity("Customer deleted successfully")
                .type(MediaType.APPLICATION_JSON)
                .build();

    }

}
