Feature: Payment Service feature

  Scenario: Merchant requests a payment
    When "PaymentRequested" event to execute a payment is recived
    Then the payment is executed and the "PaymentCompleted" event is sent
    And the payment confirmation is received by the merchant

  Scenario: Manager gets payment list
    Given a list of payments are present in the payment repository
    When "GetPaymentsRequested" event to get all payments is received
    Then the payments are fetched and the "PaymentsFetched" event is sent
    And the manager gets the list of payments
