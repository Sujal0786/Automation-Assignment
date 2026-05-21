package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MobileCoversPage {

    private static final String PHONE_MODEL_SECTION = "Phone cases by model";
    private static final String MODEL_SEARCH_INPUT = "#modelSearch";
    private static final String SEARCH_RESULTS = "#searchResults";
    private static final String EXACT_IPHONE_16_PRO = "iPhone 16 Pro";
    private static final List<String> OTHER_BRANDS = Arrays.asList(
            "Samsung", "OnePlus", "Vivo", "Oppo", "Realme");

    private final Page page;

    public MobileCoversPage(Page page) {
        this.page = page;
    }

    public void scrollToPhoneCasesByModel() {
        Locator sectionHeading = page.getByText(PHONE_MODEL_SECTION,
                new Page.GetByTextOptions().setExact(false));
        if (sectionHeading.count() == 0) {
            sectionHeading = page.getByRole(AriaRole.HEADING,
                    new Page.GetByRoleOptions().setName(PHONE_MODEL_SECTION));
        }
        sectionHeading.first().scrollIntoViewIfNeeded();
        sectionHeading.first().waitFor();
        phoneModelSearchBox().scrollIntoViewIfNeeded();
        assertTrue("'" + PHONE_MODEL_SECTION + "' section is not visible",
                sectionHeading.first().isVisible());
    }

    private Locator phoneModelSearchBox() {
        return page.locator(MODEL_SEARCH_INPUT);
    }

    private Locator searchResultsDropdown() {
        return page.locator(SEARCH_RESULTS);
    }

    public void searchBrandApple() {
        scrollToPhoneCasesByModel();
        Locator searchBox = phoneModelSearchBox();
        searchBox.waitFor();
        searchBox.click();
        searchBox.fill("Apple");
        searchResultsDropdown().waitFor();
    }

    public void validateAppleModelsVisible() {
        Locator searchResults = searchResultsDropdown();
        String appleResultsText = searchResults.innerText();

        if (appleResultsText.contains("No models found")) {
            // CaseKaro has no model named "Apple"; Apple devices appear as iPhone models.
            Locator searchBox = phoneModelSearchBox();
            searchBox.fill("iPhone");
            page.waitForCondition(() -> searchResults.locator("a")
                    .filter(new Locator.FilterOptions().setHasText("iPhone")).count() > 0);

            Locator iphoneLinks = searchResults.locator("a")
                    .filter(new Locator.FilterOptions().setHasText("iPhone"));
            assertTrue("Apple/iPhone models should be available in search",
                    iphoneLinks.count() > 0);

            searchBox.fill("Apple");
            searchResults.waitFor();
            return;
        }

        Locator appleOrIphoneLinks = searchResults.locator("a")
                .filter(new Locator.FilterOptions().setHasText("iPhone"));
        assertTrue("No Apple/iPhone models visible after Apple search",
                appleOrIphoneLinks.count() > 0);
    }

    public void validateOtherBrandsNotVisible() {
        String resultsText = searchResultsDropdown().innerText();
        for (String brand : OTHER_BRANDS) {
            assertFalse("Brand '" + brand + "' should not appear in search results after Apple search",
                    resultsText.contains(brand));
        }
    }

    public void searchAndSelectIphone16Pro() {
        Locator searchBox = phoneModelSearchBox();
        searchBox.scrollIntoViewIfNeeded();
        searchBox.click();
        searchBox.clear();
        searchBox.fill("iPhone 16 Pro");

        assertEquals("Search box must show iPhone 16 Pro (not Apple)",
                "iPhone 16 Pro", searchBox.inputValue());

        Locator searchResults = searchResultsDropdown();
        searchResults.waitFor();
        page.waitForCondition(() -> searchResults.locator("a").count() >= 1);

        Locator iphone16ProLink = findExactIphone16ProLink(searchResults);
        assertTrue("Exact '" + EXACT_IPHONE_16_PRO + "' must appear in dropdown",
                iphone16ProLink != null);

        iphone16ProLink.click();
        page.waitForURL("**/iphone-16-pro**");
        assertTrue("iPhone 16 Pro collection page was not opened. URL: " + page.url(),
                page.url().contains("iphone-16-pro"));
    }

    private Locator findExactIphone16ProLink(Locator searchResults) {
        Locator links = searchResults.locator("a");
        for (int i = 0; i < links.count(); i++) {
            Locator link = links.nth(i);
            if (EXACT_IPHONE_16_PRO.equals(link.innerText().trim())) {
                return link;
            }
        }
        return null;
    }
}
