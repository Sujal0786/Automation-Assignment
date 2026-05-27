package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import utils.AssertionUtils;
import utils.ConfigReader;
import utils.WaitUtils;

/**
 * Page Object for CaseKaro Home Page.
 */
public class HomePage extends BasePage {

    private final Locator logoLink;
    private final Locator mobileCoversLink;
    private final Locator alternativeMobileCoversLink;
    private final Locator modelSearchInput;

    public HomePage(Page page) {
        super(page);
        this.logoLink = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Casekaro").setExact(true)).first();
        this.mobileCoversLink = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Mobile Covers")).first();
        this.alternativeMobileCoversLink = page.locator("a").filter(
                new Locator.FilterOptions().setHasText("Mobile Covers")).first();
        this.modelSearchInput = page.locator("#modelSearch");
    }

    public void openWebsite() {
        navigate(ConfigReader.getUrl());
        verifyHomepageLoaded();
    }

    public void verifyHomepageLoaded() {
        AssertionUtils.assertPageUrlContains(getUrl(), "casekaro", "CaseKaro homepage did not load");
        WaitUtils.waitForElementVisible(logoLink);
        AssertionUtils.assertConditionTrue(logoLink.isVisible(), "CaseKaro logo/link is not visible on homepage");
    }

    public void clickMobileCovers() {
        if (mobileCoversLink.count() > 0) {
            click(mobileCoversLink);
        } else {
            click(alternativeMobileCoversLink);
        }
        WaitUtils.waitForPageLoadState(page, LoadState.DOMCONTENTLOADED);
        verifyMobileCoversNavigation();
    }

    public void verifyMobileCoversNavigation() {
        String currentUrl = getUrl();
        boolean onMobilePage = currentUrl.contains("phone-cases-by-model")
                || currentUrl.contains("mobile")
                || currentUrl.contains("back-cover")
                || modelSearchInput.isVisible();
        AssertionUtils.assertConditionTrue(onMobilePage, 
                "Mobile Covers page was not opened successfully. URL: " + currentUrl);
    }
}
