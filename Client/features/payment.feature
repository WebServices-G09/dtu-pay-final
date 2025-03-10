Feature: Generate payment

  Scenario: Successful Payment
    Given customer with name "Susan", last name "Baldwin", and CPR "1292912-2347"
    And customer is registered with the bank with an initial balance of 1000.0 kr
    And customer is registered with DTU Pay using their bank account
    And merchant with name "Daniel", last name "Oliver", and CPR "195397-7627"
    And merchant is registered with the bank with an initial balance of 1000.0 kr
    And merchant is registered with DTU Pay using their bank account
    And the customer request 3 tokens
    When the merchant initiates a payment for 10 kr by using the customer token
    Then the payment is successful
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 1010 kr
    And the customer has 2 tokens

  Scenario: Unsuccessful Payment reusing a customer token
    Given customer with name "Susan", last name "Baldwin", and CPR "229129-1429"
    And customer is registered with the bank with an initial balance of 1000.0 kr
    And customer is registered with DTU Pay using their bank account
    And merchant with name "Daniel", last name "Oliver", and CPR "124291-1629"
    And merchant is registered with the bank with an initial balance of 1000.0 kr
    And merchant is registered with DTU Pay using their bank account
    And the customer request 3 tokens
    And the merchant initiates a payment for 10 kr by using the customer token
    When the merchant initiates a new payment for 10 kr reusing the customer token
    Then the payment is unsuccessful and the exception message "Token does not exist" is returned

  Scenario: Unsuccessful Payment due to low balance
    Given customer with name "Susan", last name "Baldwin", and CPR "129291-1429"
    And customer is registered with the bank with an initial balance of 5 kr
    And customer is registered with DTU Pay using their bank account
    And merchant with name "Daniel", last name "Oliver", and CPR "192297-1649"
    And merchant is registered with the bank with an initial balance of 1000 kr
    And merchant is registered with DTU Pay using their bank account
    And the customer request 3 tokens
    When the merchant initiates a payment for 10 kr by using the customer token
    Then the payment is unsuccessful and the exception message "Debtor balance will be negative" is returned
    And the balance of the customer at the bank is 5 kr
    And the balance of the merchant at the bank is 1000 kr
    And the customer has 2 tokens

  Scenario: Payment fails due to merchant account not being valid
    Given customer with name "Susan", last name "Baldwin", and CPR "129291-1429"
    And customer is registered with the bank with an initial balance of 1000.0 kr
    And customer is registered with DTU Pay using their bank account
    And merchant with name "Ion", last name "Caramida", and CPR "19972-4916"
    And merchant is not registered with DTU Pay
    And the customer request 3 tokens
    When the merchant initiates a payment for 10 kr by using the customer token
    Then the payment is unsuccessful and the exception message "Merchant account does not exist." is returned

  Scenario: Payment fails due to customer account not being valid
    Given customer with name "Susan", last name "Baldwin", and CPR "129291-1429"
    And customer is registered with the bank with an initial balance of 1000.0 kr
    And customer is registered with DTU Pay using their bank account
    And merchant with name "Daniel", last name "Oliver", and CPR "192297-1649"
    And merchant is registered with the bank with an initial balance of 1000.0 kr
    And merchant is registered with DTU Pay using their bank account
    And the customer request 3 tokens
    And the customer gets unregistered
    And the merchant initiates a payment for 10 kr by using the customer token
    Then the payment is unsuccessful and the exception message "Customer account does not exist." is returned

