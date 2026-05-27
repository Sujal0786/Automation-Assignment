package base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import utils.ConfigReader;

/**
 * Manages Playwright browser lifecycle dynamically using config.properties.
 */
public class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext context;
    protected static Page page;

    public void setUp() {
        System.out.println("INITIALIZING PLAYWRIGHT FACTORY...");
        playwright = Playwright.create();
        
        String browserName = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();
        int timeout = ConfigReader.getTimeout();
        int width = ConfigReader.getWidth();
        int height = ConfigReader.getHeight();

        System.out.println("LAUNCHING BROWSER: " + browserName + " (Headless: " + headless + ")");
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);

        switch (browserName) {
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
                 case "edge":
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                .setChannel("msedge")
                .setHeadless(headless)
        );
        break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            case "chromium":
            default:
                browser = playwright.chromium().launch(launchOptions);
                break;
                
        }

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(width, height));
        page = context.newPage();
        
        page.setDefaultTimeout(timeout);
        page.setDefaultNavigationTimeout(timeout);
    }

    public void tearDown() {
        System.out.println("TEARING DOWN BROWSER CONTEXT...");
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    public static Page getPage() {
        return page;
    }
}
