@CaseKaro @Smoke
Feature: CaseKaro mobile cover cart validation
  As a QA intern
  I want to automate the CaseKaro mobile cover purchase flow
  So that search, product variants, and cart can be validated end-to-end

  Scenario Outline: Validate cart details for different mobile models and material variants
    Given user opens CaseKaro website
    When user clicks on Mobile Covers
    And user searches "<brand>" in phone model search
    Then only "<brand>" phone models should be visible
    And other brand phone models should not be visible
    When user selects exactly "<model>" from suggestions
    And user clicks Choose Options on first product card
    And user adds "<material1>" material variant to cart
    And user adds "<material2>" material variant to cart
    And user adds "<material3>" material variant to cart
    And user opens the cart
    Then cart should contain all "<expectedCount>" material variants
    And user prints material price and product link in console

    Examples:
      | brand | model         | material1 | material2 | material3 | expectedCount |
      | Apple | iPhone 16 Pro | Hard      | Soft      | Glass     | 3             |
