/**
 * @author Hussein Dirani s223518
 */
package dtu.dtuPay.models;

import java.util.UUID;

public class Merchant {
    private UUID id;
    private String firstName;
    private String lastName;
    private String cpr;
    private String bankAccountId;

    public Merchant() {}

    public Merchant(String firstName, String lastName, String cpr, String bankAccountId) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpr = cpr;
        this.bankAccountId = bankAccountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }
    public UUID getId() {
        return id;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
