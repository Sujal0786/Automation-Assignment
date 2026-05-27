package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import utils.WaitUtils;

import java.nio.file.Paths;

/**
 * Base Page class representing common interactions and functions for all Page Objects.
 */
public class BasePage {
    protected final Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public void navigate(String url) {
        System.out.println("NAVIGATING TO URL: " + url);
        page.navigate(url);
        WaitUtils.waitForPageLoadState(page, LoadState.DOMCONTENTLOADED);
    }

    public void click(Locator locator) {
        WaitUtils.waitForElementVisible(locator);
        locator.scrollIntoViewIfNeeded();
        locator.click(new Locator.ClickOptions().setForce(true));
    }

    public void fill(Locator locator, String value) {
        WaitUtils.waitForElementVisible(locator);
        locator.scrollIntoViewIfNeeded();
        locator.click();
        locator.clear();
        locator.fill(value);
    }

    public String getUrl() {
        return page.url();
    }

    public void takeScreenshot(String name) {
        String path = "screenshots/" + name + "_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(path))
                .setFullPage(true));
        System.out.println("SCREENSHOT SAVED AT: " + path);
    }
}
