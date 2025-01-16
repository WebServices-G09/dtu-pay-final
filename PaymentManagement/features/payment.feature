Feature: Payment Service feature

  Scenario: Manager gets payment list
    When "GetPaymentsRequested" event to get all payments is received
    Then the payments are fetched and the "PaymentsFetched" event is sent
    And the manager gets the list of payments
