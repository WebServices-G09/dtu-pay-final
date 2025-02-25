Feature: Using a token

  Scenario: A valid token is used to pay
    Given a payment service supplies 1 valid token with UUID "128e62be-42e7-42db-bb3a-9aab8db6e4d9" to pay
    And a registered DTU pay customer with UUID "49e3d00a-b113-460b-9a9e-71f7a4376ce8" is associated with the token
    When the event "UseTokenRequested" is received
    Then a response "ResponseTokenUsed" is sent and contains the customer UUID "49e3d00a-b113-460b-9a9e-71f7a4376ce8"
    And a response contains the token UUID "128e62be-42e7-42db-bb3a-9aab8db6e4d9"
    And the customer has 0 tokens

  Scenario: An already used token is used to pay
    Given a payment service supplies 1 used invalid token with UUID "bcc761ab-40e5-42ab-8c04-3b1836ddfb7c" to pay
    And a registered DTU pay customer with UUID "49e3d00a-b113-460b-9a9e-71f7a4376ce8" is associated with the token
    When the event "UseTokenRequested" is received
    Then a response event "ResponseTokenUsed" is sent and contains an exception "Token does not exist"

  Scenario: A non-existing token is used to pay
    Given a payment service supplies an token with UUID "bf369932-446d-4dc2-a397-1489c6acbbed" to pay
    When the event "UseTokenRequested" is received
    Then a response event "ResponseTokenUsed" is sent and contains an exception "Token does not exist"
