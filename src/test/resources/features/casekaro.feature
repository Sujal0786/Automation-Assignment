@CaseKaro @Smoke
Feature: CaseKaro mobile cover cart validation
  As a QA intern
  I want to automate the CaseKaro mobile cover purchase flow
  So that search, product variants, and cart can be validated end-to-end

  Scenario: Add Hard, Soft and Glass material variants of iPhone 16 Pro case to cart
    Given user opens CaseKaro website
    When user clicks on Mobile Covers
    And user searches Apple in phone model search
    Then only Apple phone models should be visible
    And other brand phone models should not be visible
    When user selects exactly iPhone 16 Pro from suggestions
    And user clicks Choose Options on first product card
    And user adds Hard material variant to cart
    And user adds Soft material variant to cart
    And user adds Glass material variant to cart
    Then cart should contain all 3 material variants
    And user prints material price and product link in console
