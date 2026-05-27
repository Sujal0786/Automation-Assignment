# CaseKaro Automation Assignment: Refactoring & Architecture Report

**Prepared By:** Sujal Arora  
**Technology Stack:** Java, Playwright, Cucumber BDD, Maven  

During my review and execution of the initial test framework, I identified several architectural bottlenecks, code smells, and synchronization issues. I have refactored the entire codebase to conform to enterprise-level software design patterns (Page Object Model) and to make it fully data-driven. 

Below is the technical breakdown of the issues I resolved, the design changes I implemented, and before-and-after code comparisons.

---

## 1. Technical Issues I Discovered & Resolved

### A. Shopify AJAX Add-to-Cart Race Condition (First Product Page Load)
* **The Issue**: During test execution, the first variant ("Hard") was clicked but not successfully added to the cart, leading to missing validation items.
* **The Cause**: The HTML DOM loads quickly, but Shopify’s custom theme scripts (which bind AJAX handlers to the Add to Cart form) take a couple of seconds to register event listeners. Triggering the click immediately meant the button was clicked before the JavaScript event bindings completed.
* **My Solution**: I introduced a 2000ms delay (`page.waitForTimeout(2000)`) right after page navigation. This ensures the event listeners have bound to the form, guaranteeing that the click event triggers the AJAX call correctly.

### B. Mobile/Desktop DOM Element Duplication
* **The Issue**: Scraping the cart page returned 4 items instead of 3, resulting in `Material: Unknown` and `Price: N/A` for duplicate rows.
* **The Cause**: The e-commerce site renders two layouts simultaneously for responsive design: one desktop grid and one mobile list. Both share the CSS class `.cart-item`. Playwright matched both, including the hidden mobile elements which returned empty strings for text.
* **My Solution**: I appended the `:visible` pseudo-class (e.g. `tr.cart-item:visible`) to the Playwright locators. This filters out hidden layouts and matches only active visible desktop elements.

---

## 2. Key Framework Improvements I Implemented

1. **Config Separation & Reader Utility**:
   - I moved hardcoded configurations (browser, headless, target URL, window sizes, timeouts) into a central properties file (`config.properties`) and built a thread-safe `ConfigReader` wrapper.
2. **Page Object Model Base Layer (`BasePage`)**:
   - I created `BasePage` as a parent page object. It wraps standard Playwright operations (click, fill, navigate, screenshot) with wait conditions and logging. All page classes now extend `BasePage`.
3. **Explicit Wait & Custom Assertion Utilities**:
   - I decoupled explicit waits and verification actions into `WaitUtils` and `AssertionUtils`. This separates business assertion logs from core page interactions.
4. **Stateful Step Definition Flow**:
   - I added a private field `currentSearchedBrand` inside `CaseKaroSteps` to track searched brands dynamically. This allows the negative verification step (`other brand phone models should not be visible`) to dynamically filter results based on the searched brand context.

---

## 3. Before vs. After Code Comparisons

### A. Gherkin Scenarios (`casekaro.feature`)
* **Before (Hardcoded Steps)**:
  ```gherkin
  Scenario: Add Hard, Soft and Glass material variants of iPhone 16 Pro case to cart
    And user searches Apple in phone model search
    Then only Apple phone models should be visible
    When user selects exactly iPhone 16 Pro from suggestions
    And user adds Hard material variant to cart
    And user adds Soft material variant to cart
    And user adds Glass material variant to cart
  ```
* **After (Dynamic Scenario Outline)**:
  ```gherkin
  Scenario Outline: Validate cart details for different mobile models and material variants
    And user searches "<brand>" in phone model search
    Then only "<brand>" phone models should be visible
    When user selects exactly "<model>" from suggestions
    And user adds "<material1>" material variant to cart
    And user adds "<material2>" material variant to cart
    And user adds "<material3>" material variant to cart
    And user opens the cart
    Then cart should contain all "<expectedCount>" material variants

    Examples:
      | brand | model         | material1 | material2 | material3 | expectedCount |
      | Apple | iPhone 16 Pro | Hard      | Soft      | Glass     | 3             |
  ```

### B. Step Definitions (`CaseKaroSteps.java`)
* **Before (Redundant step boilerplate)**:
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
* **After (Consolidated Parameterized step)**:
  ```java
  @And("user adds {string} material variant to cart")
  public void userAddsMaterialVariantToCart(String material) {
      productPage.addMaterialVariantToCart(material);
  }
  ```

### C. Selector Filtering (`CartPage.java`)
* **Before (Matched empty/hidden responsive layout elements)**:
  ```java
  this.cartLineItems = page.locator("tr.cart-item, .cart-item");
  ```
* **After (Uses Playwright visible matching)**:
  ```java
  this.cartLineItems = page.locator(
          "#main-cart-items .cart-item:visible, " +
          ".cart__items .cart-item:visible, " +
          "tr.cart-item:visible, " +
          ".cart-item:visible"
  );
  ```

---

## 4. Key Architectural Patterns in My Code

* **Page Object Model (POM)**: Ensures test cases only define high-level scenario actions, while element locator logic and page interactions are kept within page classes.
* **Single Responsibility Principle (SRP)**: Each class has only one reason to change. Configuration parsing (`ConfigReader`), waiting rules (`WaitUtils`), UI wrappers (`BasePage`), and test assertions (`AssertionUtils`) are separate concerns.
* **Data-Driven Framework (DDT)**: Decoupled test parameters from Java step definitions so that adding new execution datasets (e.g. testing Samsung variants) can be done entirely within the `.feature` file.
* **DRY (Don't Repeat Yourself)**: Eliminated repeated variant methods and consolidated step definitions, keeping the codebase clean and maintainable.
