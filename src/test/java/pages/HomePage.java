package pages;

import base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import static org.junit.Assert.assertTrue;

public class HomePage {

    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public void openWebsite() {
        page.navigate(BaseTest.BASE_URL);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        verifyHomepageLoaded();
    }

    public void verifyHomepageLoaded() {
        assertTrue("CaseKaro homepage did not load",
                page.url().contains("casekaro.com"));
        Locator logo = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Casekaro").setExact(true));
        logo.first().waitFor();
        assertTrue("CaseKaro logo/link is not visible on homepage",
                logo.first().isVisible());
    }

    public void clickMobileCovers() {
        Locator mobileCoversLink = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Mobile Covers"));
        if (mobileCoversLink.count() == 0) {
            mobileCoversLink = page.locator("a").filter(
                    new Locator.FilterOptions().setHasText("Mobile Covers"));
        }
        mobileCoversLink.first().click();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        verifyMobileCoversNavigation();
    }

    public void verifyMobileCoversNavigation() {
        String currentUrl = page.url();
        boolean onMobilePage = currentUrl.contains("phone-cases-by-model")
                || currentUrl.contains("mobile")
                || currentUrl.contains("back-cover")
                || page.locator("#modelSearch").isVisible();
        assertTrue("Mobile Covers page was not opened successfully. URL: " + currentUrl,
                onMobilePage);
    }
}
