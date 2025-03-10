/**
 * @author Hussein Dirani s223518
 */
package dtu.dtuPay.services;

import messaging.Event;
import messaging.MessageQueue;
import dtu.dtuPay.models.AccountEventMessage;
import dtu.dtuPay.models.CorrelationId;
import dtu.dtuPay.models.Merchant;
import dtu.dtuPay.repositories.MerchantRepository;

public class MerchantService {
    private static final String MERCHANT_REGISTRATION_REQUESTED = "MerchantRegistrationRequested";
    private static final String MERCHANT_CREATED = "MerchantCreated";
    private static final String MERCHANT_DEREGISTRATION_REQUESTED = "MerchantDeregistrationRequested";
    private static final String MERCHANT_DEREGISTERED = "MerchantDeregistered";
    private static final String VALIDATE_MERCHANT_ACCOUNT_REQUESTED = "ValidateMerchantAccountRequested";
    private static final String MERCHANT_ACCOUNT_VALIDATED = "MerchantAccountValidated";

    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;

    MessageQueue queue;
    MerchantRepository merchantRepository = MerchantRepository.getInstance();


    public MerchantService(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler(MERCHANT_REGISTRATION_REQUESTED, this::handleMerchantRegistrationRequested);
        this.queue.addHandler(MERCHANT_DEREGISTRATION_REQUESTED, this::handleMerchantDeregistrationRequested);
        this.queue.addHandler(VALIDATE_MERCHANT_ACCOUNT_REQUESTED, this::handleValidateMerchantAccountRequested);
    }

    public void handleMerchantRegistrationRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        Merchant merchant=
                new Merchant(
                        eventMessage.getFirstName(),
                        eventMessage.getLastName(),
                        eventMessage.getCpr(),
                        eventMessage.getBankAccount());
        merchantRepository.addMerchant(merchant);

        System.out.println("I created "+merchant.getFirstName());

        eventMessage.setRequestResponseCode(OK);
        eventMessage.setMerchantId(merchant.getId());

        Event event = new Event(MERCHANT_CREATED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleMerchantDeregistrationRequested(Event ev) {
        CorrelationId correlationId=ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        boolean isDeleted = merchantRepository.removeMerchant(eventMessage.getMerchantId());
        System.out.println(isDeleted);

        eventMessage.setIsAccountDeleted(isDeleted);
        eventMessage.setRequestResponseCode(OK);

        Event event = new Event(MERCHANT_DEREGISTERED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }

    public void handleValidateMerchantAccountRequested(Event ev) {
        CorrelationId correlationId = ev.getArgument(0, CorrelationId.class);
        AccountEventMessage eventMessage = ev.getArgument(1, AccountEventMessage.class);

        Merchant merchant = merchantRepository.getMerchant(eventMessage.getMerchantId());
        boolean isValid = merchant != null;

        eventMessage.setBankAccount(isValid ? merchant.getBankAccountId() : null);
        eventMessage.setIsValidAccount(isValid);
        eventMessage.setRequestResponseCode(isValid ? OK : BAD_REQUEST);
        eventMessage.setExceptionMessage(isValid ? null : "Merchant account does not exist.");

        Event event = new Event(MERCHANT_ACCOUNT_VALIDATED, new Object[] { correlationId, eventMessage });
        queue.publish(event);
    }
}
