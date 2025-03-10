Feature: Issuing one token

  Scenario: Customer with at least one token tries to get one token issued
     Given a registered customer with id "9b6b1fd5-e37f-479d-a262-6533405e781d" with at least 1 tokens
     When the event CustomerTokensRequest "GetTokensRequested" is sent
     Then a response CustomerTokensReturned "ResponseGetTokensReturned" is sent and a customer receives a list of tokens

  Scenario: Customer with no tokens tries to get one token issued
     Given a registered customer with id "9b6b1fd6-e37f-479d-a262-6533405e781d" with 0 tokens
     When the event CustomerTokensRequest "GetTokensRequested" is sent
     Then a response CustomerTokensReturned "ResponseGetTokensReturned" is sent and the system throws an exception with message "You have no more tokens. Request more tokens."
