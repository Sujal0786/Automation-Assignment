# CaseKaro QA Automation Assignment

## 1. Project Title

**CaseKaro Mobile Cover Cart Validation — Java + Playwright + Cucumber BDD**

## 2. Objective

Automate the end-to-end CaseKaro mobile cover purchase flow: navigate the site, validate Apple phone model search (including negative brand checks), select **iPhone 16 Pro** exactly, add **Hard**, **Soft**, and **Glass** variants to the cart, validate the cart, and print item details to the console.

## 3. Website Under Test

[https://casekaro.com/](https://casekaro.com/)

## 4. Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Programming language |
| Playwright | Browser automation |
| Cucumber | BDD framework |
| JUnit 4 | Assertions and test runner |
| Maven | Build and dependency management |

## 5. Framework Design

- **Page Object Model (POM):** UI locators and actions live in page classes.
- **Cucumber BDD:** Business-readable scenarios in Gherkin.
- **BaseTest:** Central browser setup and teardown.
- **Step Definitions:** Glue between feature steps and page objects.
- **TestRunner:** Executes Cucumber with HTML reporting.

## 6. Folder Structure

```
QA-Automation-Assignment/
├── pom.xml
├── README.md
├── Assignment_Report.md
├── .gitignore
├── screenshots/
└── src/test/
    ├── java/
    │   ├── base/BaseTest.java
    │   ├── pages/
    │   │   ├── HomePage.java
    │   │   ├── MobileCoversPage.java
    │   │   ├── ProductPage.java
    │   │   └── CartPage.java
    │   ├── stepdefinitions/CaseKaroSteps.java
    │   └── runners/TestRunner.java
    └── resources/features/casekaro.feature
```

## 7. Test Scenario

**Feature:** CaseKaro mobile cover cart validation  

**Scenario:** Add Hard, Soft and Glass material variants of iPhone 16 Pro case to cart

Steps cover: homepage → Mobile Covers → Apple search → negative brand validation → exact iPhone 16 Pro selection → product variants → cart validation → console output.

## 8. Assertions Covered

- Homepage loaded (logo/URL)
- Mobile Covers navigation successful
- Apple/iPhone models visible after search
- Samsung, OnePlus, Vivo, Oppo, Realme not visible (negative test)
- Exact **iPhone 16 Pro** selected (not Pro Max)
- Product page opens via Choose Options
- All three materials added
- Cart contains 3 items with expected materials
- Material, price, and product link printed

## 9. How to Install

**Prerequisites:** JDK 17+, Maven 3.8+, stable internet

```bash
cd QA-Automation-Assignment
mvn clean install
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

## 10. How to Run

```bash
mvn test
```

Run headed (default in `BaseTest.java`):

```java
public static final boolean HEADED = true;
```

Set `HEADED = false` in `BaseTest.java` for headless CI runs.

## 11. Expected Console Output

```
Material: Hard
Price: ₹499
Link: https://casekaro.com/products/...

Material: Soft
Price: ₹499
Link: https://casekaro.com/products/...

Material: Glass
Price: ₹499
Link: https://casekaro.com/products/...
```

*(Prices and links depend on live site data.)*

## 12. Reports Generated

| Report | Location |
|--------|----------|
| Cucumber HTML | `target/cucumber-report.html` |
| Surefire logs | `target/surefire-reports/` |

Open the HTML report in a browser after `mvn test`.

## 13. Notes

- Uses Playwright auto-waiting; avoid `Thread.sleep` except where unavoidable.
- No try-catch in test code; failures surface via JUnit assertions.
- Locators use role, placeholder, and exact text match for stability.
- Site UI may change; update page objects if selectors break.

---

**Author:** Sujal Arora | **Assignment:** QA Intern Automation
