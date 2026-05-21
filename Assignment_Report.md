# QA Automation Assignment

---

## 1. Title Page

| Field | Details |
|-------|---------|
| **Assignment** | QA Automation Assignment |
| **Submitted By** | Sujal Arora |
| **Technology Used** | Java, Playwright, Cucumber |
| **Website** | [https://casekaro.com/](https://casekaro.com/) |
| **Date** | May 2026 |

---

## 2. Objective

The objective of this assignment is to automate the CaseKaro mobile cover user journey using **Java**, **Playwright**, and **Cucumber BDD**. The automation validates website navigation, Apple brand search with negative brand checks, exact selection of **iPhone 16 Pro**, addition of three material variants (**Hard**, **Soft**, **Glass**) to the cart, cart validation, and structured console output of price and product links.

---

## 3. Scope of Automation

| Area | Description |
|------|-------------|
| Website navigation | Open homepage and verify load |
| Mobile Covers page | Navigate via top menu |
| Apple search validation | Search "Apple" in phone model search |
| Negative validation | Ensure Samsung, OnePlus, Vivo, Oppo, Realme are not visible |
| Exact model selection | Select **iPhone 16 Pro** only (not Pro Max) |
| Product variant handling | Add Hard, Soft, and Glass materials |
| Cart validation | Verify 3 items in cart |
| Console output | Print material, price, and product link |

---

## 4. Tools and Technologies Used

| Tool / Technology | Purpose |
|-------------------|---------|
| Java | Programming language |
| Playwright | Browser automation |
| Cucumber | BDD framework |
| Maven | Build tool |
| JUnit | Assertions |
| GitHub | Version control |

---

## 5. Framework Architecture

The framework follows the **Page Object Model (POM)**:

```
Feature File (Gherkin)
        ↓
Step Definitions (CaseKaroSteps.java)
        ↓
Page Objects (HomePage, MobileCoversPage, ProductPage, CartPage)
        ↓
BaseTest (Playwright browser lifecycle)
```

- **BaseTest** — Launches Chromium, creates context and page, handles teardown.
- **Page classes** — Encapsulate locators and user actions per screen.
- **Step definitions** — Map Cucumber steps to page methods.
- **TestRunner** — Runs Cucumber with pretty console and HTML report plugins.

---

## 6. Test Scenario Covered

**Scenario:** Add Hard, Soft and Glass material variants of iPhone 16 Pro case to cart

**Flow:**

1. Open https://casekaro.com/
2. Click **Mobile Covers**
3. Scroll to **Phone cases by model**
4. Search **Apple** in phone model search
5. Validate Apple models visible; other brands not visible
6. Search and select exactly **iPhone 16 Pro**
7. Click **Choose Options** on first product
8. Add Hard, Soft, and Glass to cart
9. Open cart and validate 3 items
10. Print material, price, and link for each item

---

## 7. Validations and Assertions

| # | Validation |
|---|------------|
| 1 | Homepage loaded (URL + logo) |
| 2 | Mobile Covers page opened |
| 3 | Apple/iPhone models visible after search |
| 4 | Other brands not visible (negative test) |
| 5 | Exact iPhone 16 Pro selected |
| 6 | Product page opened |
| 7 | All 3 materials added successfully |
| 8 | Cart contains 3 items |
| 9 | Material, price, and link printed to console |

---

## 8. Negative Testing

After searching **Apple** in the phone model search box, the framework asserts that competing brand labels are **not** visible in the filtered results:

- Samsung  
- OnePlus  
- Vivo  
- Oppo  
- Realme  

This confirms the search filter narrows results to Apple-related models only.

---

## 9. Challenges Faced

| Challenge | Impact |
|-----------|--------|
| Dynamic dropdown suggestions | Timing and locator stability |
| Similar model names (iPhone 16 Pro vs Pro Max) | Risk of wrong selection |
| Dynamic cart updates | Async UI after add-to-cart |
| Variant selection (Hard/Soft/Glass) | Multiple radio/label patterns |
| Page loading and AJAX | Requires Playwright auto-wait |

---

## 10. Solution Approach

| Approach | Benefit |
|----------|---------|
| Exact match locator for iPhone 16 Pro | Avoids Pro Max mis-click |
| Playwright auto-waits | No brittle Thread.sleep |
| JUnit assertions (no try-catch) | Clear failure reporting |
| Page Object Model | Maintainable, reusable code |
| Cucumber BDD | Readable scenario for reviewers |

---

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

---

## 12. Conclusion

The required CaseKaro flow was automated successfully using Java, Playwright, and Cucumber with a professional framework structure. Assertions cover navigation, search filtering, exact product selection, multi-variant cart addition, and detailed console reporting—demonstrating practical QA automation skills suitable for intern assignment submission and technical interviews.

---

## 13. Future Enhancements

- Screenshot capture on failure  
- CI/CD integration (GitHub Actions)  
- Cross-browser testing (Firefox, WebKit)  
- Parallel scenario execution  
- Allure report integration  
- Environment-specific config (staging vs production)  
- API + UI hybrid validation  

---

*End of Report — Sujal Arora*
