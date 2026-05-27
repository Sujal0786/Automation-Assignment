package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import utils.AssertionUtils;
import utils.WaitUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Page Object for the Mobile Covers page.
 */
public class MobileCoversPage extends BasePage {

    private static final String PHONE_MODEL_SECTION = "Phone cases by model";
    private static final String MODEL_SEARCH_INPUT = "#modelSearch";
    private static final String SEARCH_RESULTS = "#searchResults";
    private static final List<String> OTHER_BRANDS = Arrays.asList(
            "Samsung", "OnePlus", "Vivo", "Oppo", "Realme");

    private final Locator sectionHeading;
    private final Locator alternativeSectionHeading;
    private final Locator searchInput;
    private final Locator dropdownContainer;

    public MobileCoversPage(Page page) {
        super(page);
        this.sectionHeading = page.getByText(PHONE_MODEL_SECTION,
                new Page.GetByTextOptions().setExact(false)).first();
        this.alternativeSectionHeading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName(PHONE_MODEL_SECTION)).first();
        this.searchInput = page.locator(MODEL_SEARCH_INPUT);
        this.dropdownContainer = page.locator(SEARCH_RESULTS);
    }

    public void scrollToPhoneCasesByModel() {
        Locator heading = sectionHeading.count() > 0 ? sectionHeading : alternativeSectionHeading;
        heading.scrollIntoViewIfNeeded();
        heading.waitFor();
        searchInput.scrollIntoViewIfNeeded();
        AssertionUtils.assertConditionTrue(heading.isVisible(), 
                "'" + PHONE_MODEL_SECTION + "' section is not visible");
    }

    public void searchBrand(String brand) {
        scrollToPhoneCasesByModel();
        WaitUtils.waitForElementVisible(searchInput);
        click(searchInput);
        fill(searchInput, brand);
        WaitUtils.waitForElementVisible(dropdownContainer);
    }

    public void validateBrandModelsVisible(String brand) {
        String resultsText = dropdownContainer.innerText();

        String expectedText = brand;
        if (brand.equalsIgnoreCase("Apple")) {
            expectedText = "iPhone";
        }

        if (resultsText.contains("No models found")) {
            fill(searchInput, expectedText);
            String finalExpectedText = expectedText;
            page.waitForCondition(() -> dropdownContainer.locator("a")
                    .filter(new Locator.FilterOptions().setHasText(finalExpectedText)).count() > 0);

            Locator brandLinks = dropdownContainer.locator("a")
                    .filter(new Locator.FilterOptions().setHasText(expectedText));
            AssertionUtils.assertConditionTrue(brandLinks.count() > 0,
                    brand + "/" + expectedText + " models should be available in search");

            fill(searchInput, brand);
            WaitUtils.waitForElementVisible(dropdownContainer);
            return;
        }

        Locator brandLinks = dropdownContainer.locator("a")
                .filter(new Locator.FilterOptions().setHasText(expectedText));
        AssertionUtils.assertConditionTrue(brandLinks.count() > 0,
                "No " + brand + "/" + expectedText + " models visible after search");
    }

    public void validateOtherBrandsNotVisible(String searchedBrand) {
        String resultsText = dropdownContainer.innerText();
        for (String brand : OTHER_BRANDS) {
            if (!brand.equalsIgnoreCase(searchedBrand)) {
                AssertionUtils.assertConditionFalse(resultsText.contains(brand),
                        "Brand '" + brand + "' should not appear in search results after " + searchedBrand + " search");
            }
        }
    }

    public void searchAndSelectModel(String modelName) {
        WaitUtils.waitForElementVisible(searchInput);
        searchInput.scrollIntoViewIfNeeded();
        click(searchInput);
        fill(searchInput, modelName);

        AssertionUtils.assertEqualsWithMessage(modelName, searchInput.inputValue(), 
                "Search box value mismatch");

        WaitUtils.waitForElementVisible(dropdownContainer);
        page.waitForCondition(() -> dropdownContainer.locator("a").count() >= 1);

        Locator modelLink = findExactModelLink(modelName);
        AssertionUtils.assertConditionTrue(modelLink != null, 
                "Exact '" + modelName + "' must appear in dropdown");

        click(modelLink);
        String urlPattern = modelName.toLowerCase().replace(" ", "-");
        page.waitForURL("**/" + urlPattern + "**");
        AssertionUtils.assertPageUrlContains(getUrl(), urlPattern, 
                modelName + " collection page was not opened");
    }

    private Locator findExactModelLink(String modelName) {
        Locator links = dropdownContainer.locator("a");
        for (int i = 0; i < links.count(); i++) {
            Locator link = links.nth(i);
            if (modelName.equalsIgnoreCase(link.innerText().trim())) {
                return link;
            }
        }
        return null;
    }
}
