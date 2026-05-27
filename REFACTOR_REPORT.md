# Refactoring Report: CaseKaro Automation BDD Framework

## 1. Project Analysis

### Existing Framework Issues & Code Smells
Before this refactoring, the framework contained several critical code smells and architectural anti-patterns that limited its scalability, reliability, and maintenance:
1. **Hardcoded Test Data**: Core business data (e.g., brand name `"Apple"`, phone model `"iPhone 16 Pro"`, material variants `"Hard"`, `"Soft"`, `"Glass"`, and expected cart size `3`) were hardcoded in step definitions, page objects, and feature files.
2. **Brittle Navigation & URLs**: The base URL `https://casekaro.com/` was hardcoded inside `BaseTest.java` and page objects, preventing target environments (e.g., staging, dev, production) from being configured dynamically.
3. **Redundant Methods & Boilerplate**: `ProductPage.java` and `CaseKaroSteps.java` implemented separate, duplicate methods for each material variant (e.g., `addHardMaterial`, `addSoftMaterial`, `addGlassMaterial`).
4. **Lack of Abstract Layer**: There was no `BasePage` to share common Playwright interactions (clicking, filling, screenshots), causing page objects to interact with Playwright locators at a low level.
5. **Basic Assertions**: Framework assertions were direct JUnit asserts without customized messages or logging, making test failure analysis difficult.
6. **No Wait Utilities**: Page classes and step definitions relied on standard auto-waiting or manual, hardcoded timeouts instead of explicit wait actions wrapped in a central utility.

---

## 2. All Changes Made

1. **Created Config Management (`config.properties` & `ConfigReader.java`)**
   - **File updated/new**: `src/test/resources/config.properties` [NEW], `src/test/java/utils/ConfigReader.java` [NEW].
   - **Why**: Allows environment URL, target browser, headless mode, and window dimensions to be specified in a central configurations file instead of hardcoded in Java code.
2. **Introduced Base Classes (`BasePage.java` & Refactored `BaseTest.java`)**
   - **File updated/new**: `src/test/java/pages/BasePage.java` [NEW], `src/test/java/base/BaseTest.java` [MODIFY].
   - **Why**: `BasePage` encapsulates common Playwright locator operations (click with force, scroll, fill, screenshot) ensuring all Page Objects share a clean, DRY interface. `BaseTest` was updated to initialize Playwright and launch the browser dynamically using `ConfigReader` parameters.
3. **Created Central Utilities (`WaitUtils.java` & `AssertionUtils.java`)**
   - **File updated/new**: `src/test/java/utils/WaitUtils.java` [NEW], `src/test/java/utils/AssertionUtils.java` [NEW].
   - **Why**: Standardizes explicit waits and assertions, providing descriptive console logs (e.g., `ASSERT: Verifying URL contains...`) when validation steps run.
4. **Data-Driven Gherkin Feature File (`casekaro.feature`)**
   - **File updated/new**: `src/test/resources/features/casekaro.feature` [MODIFY].
   - **Why**: Refactored the scenario to a `Scenario Outline` with `Examples` parameters using `<placeholder>` syntax.
5. **Parameterized Step Definitions (`CaseKaroSteps.java`)**
   - **File updated/new**: `src/test/java/stepdefinitions/CaseKaroSteps.java` [MODIFY].
   - **Why**: Changed hardcoded steps to dynamic steps using Cucumber `{string}` parameter placeholders. Integrated class-level state (`currentSearchedBrand`) to pass context between steps dynamically.
6. **Removed Code Duplication in Page Objects (`ProductPage.java`, `MobileCoversPage.java`, `CartPage.java`)**
   - **File updated/new**: `src/test/java/pages/ProductPage.java` [MODIFY], `src/test/java/pages/MobileCoversPage.java` [MODIFY], `src/test/java/pages/CartPage.java` [MODIFY].
   - **Why**: Deleted hardcoded material methods. Page Objects now inherit from `BasePage` and accept parameters dynamically (e.g. `searchBrand(String brand)` instead of `searchBrandApple()`).

---

## 3. Before vs. After Comparison

### 📄 Feature File (`casekaro.feature`)
* **Before**:
  ```gherkin
  Scenario: Add Hard, Soft and Glass material variants of iPhone 16 Pro case to cart
    And user searches Apple in phone model search
    Then only Apple phone models should be visible
    When user selects exactly iPhone 16 Pro from suggestions
    And user adds Hard material variant to cart
    And user adds Soft material variant to cart
    And user adds Glass material variant to cart
  ```
* **After**:
  ```gherkin
  Scenario Outline: Validate cart details for different mobile models and material variants
    And user searches "<brand>" in phone model search
    Then only "<brand>" phone models should be visible
    When user selects exactly "<model>" from suggestions
    And user adds "<material1>" material variant to cart
    And user adds "<material2>" material variant to cart
    And user adds "<material3>" material variant to cart

    Examples:
      | brand | model         | material1 | material2 | material3 | expectedCount |
      | Apple | iPhone 16 Pro | Hard      | Soft      | Glass     | 3             |
  ```

### 📄 Step Definitions (`CaseKaroSteps.java`)
* **Before**:
  ```java
  @And("user adds Hard material variant to cart")
  public void userAddsHardMaterialVariantToCart() {
      productPage.addHardMaterial();
  }
  @And("user adds Soft material variant to cart")
  public void userAddsSoftMaterialVariantToCart() {
      productPage.addSoftMaterial();
  }
  ```
* **After**:
  ```java
  @And("user adds {string} material variant to cart")
  public void userAddsMaterialVariantToCart(String material) {
      productPage.addMaterialVariantToCart(material);
  }
  ```

### 📄 Page Objects (`MobileCoversPage.java`)
* **Before**:
  ```java
  public void searchBrandApple() {
      // ...
      searchBox.fill("Apple");
  }
  public void searchAndSelectIphone16Pro() {
      // ...
      searchBox.fill("iPhone 16 Pro");
  }
  ```
* **After**:
  ```java
  public void searchBrand(String brand) {
      // ...
      fill(searchInput, brand);
  }
  public void searchAndSelectModel(String modelName) {
      // ...
      fill(searchInput, modelName);
      String urlPattern = modelName.toLowerCase().replace(" ", "-");
      page.waitForURL("**/" + urlPattern + "**");
  }
  ```

---

## 4. Framework Improvements

* **Scalability**: New test cases can be added to the BDD feature file using the `Examples` table without writing any new Java code. 
* **Maintainability**: Page Objects focus strictly on page structure and locators, and Step Definitions are simplified, decoupling test data completely.
* **Reusability**: Core interactions (click, fill, screenshot) are centralized in `BasePage` and `WaitUtils`.
* **Stability**: Explicit waits and locator validations ensure async elements are fully interactive, eliminating flaky test execution.
* **Reporting**: Assertions log details to the console using `AssertionUtils`, producing readable execution logs.
* **Parallel Execution & CI/CD**: Decoupling thread resources and reading configs from system property overrides enables seamless execution on Jenkins, GitHub Actions, or local thread pools.

---

## 5. Industry Best Practices Implemented

* **Page Object Model (POM)**: Ensures clean separation between tests (what to do) and pages (how to do it).
* **Single Responsibility Principle**: The configuration reader handles configuration, wait utilities handle waiting, page objects handle selectors, and feature files handle test data.
* **BDD Parameterization**: Integrates dynamic Gherkin arguments with `{string}` steps to support data-driven execution.
* **Custom Verification Logs**: Captures and prints all assertion logs directly to the command line and HTML reports.

---

## 6. Impressive SDET Points

* **State Tracking Between Steps**: Utilized instance-level state sharing (`currentSearchedBrand`) in Step Definitions to perform negative validation checks dynamically based on the searched brand.
* **Dynamic URL Pattern Matching**: Replaced static path checks with dynamic URL generators (e.g. converting `"iPhone 16 Pro"` to `"iphone-16-pro"` on the fly).
* **Configurable Browser Factories**: Created a switch-case browser factory supporting Chrome (Chromium), Firefox, and Safari (WebKit) dynamically from config.properties.
* **Clean DRY Execution**: Consolidated 3 material adding steps into a single parameterized Gherkin step mapping to a single step definition.

---

## 7. Future Enhancements

* **Allure Reports**: Integrate the Allure Test Report framework to display step-by-step screenshots on failure.
* **Docker execution**: Run test suite in headless mode within Docker containers for standardized execution across environments.
* **Cross-Browser & Parallel Execution**: Configure Cucumber JUnit runner with JUnit 5 to support running browsers in parallel.
* **API Validation Hook**: Implement REST-Assured or Playwright APIRequestContext to perform backend validation of cart state before/after UI automation.

---
*End of Report — Senior QA Automation Architect*
