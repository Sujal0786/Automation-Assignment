package utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Reusable explicit wait utilities for Playwright locators and pages.
 */
public class WaitUtils {

    public static void waitForElementVisible(Locator locator, int timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
    }

    public static void waitForElementVisible(Locator locator) {
        waitForElementVisible(locator, ConfigReader.getTimeout());
    }

    public static void waitForElementHidden(Locator locator, int timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(timeoutMs));
    }

    public static void waitForElementHidden(Locator locator) {
        waitForElementHidden(locator, ConfigReader.getTimeout());
    }

    public static void waitForPageLoadState(Page page, LoadState state) {
        page.waitForLoadState(state);
    }

    public static void waitForPageLoadState(Page page) {
        waitForPageLoadState(page, LoadState.DOMCONTENTLOADED);
    }
}
