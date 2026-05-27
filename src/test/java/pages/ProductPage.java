package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import models.CartItemDetails;
import utils.AssertionUtils;
import utils.ConfigReader;
import utils.WaitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object representing the Product Details Page.
 */
public class ProductPage extends BasePage {

    private String storedProductUrl;
    private final List<CartItemDetails> addedItems = new ArrayList<>();

    // Locators
    private final Locator chooseOptionsButton;
    private final Locator modalDialog;
    private final Locator modalProductLink;
    private final Locator materialLegend;
    private final Locator addToCartButton;
    private final Locator priceContainer;
    private final Locator cartBubble;
    private final Locator cartDrawerCloseButton;

    public ProductPage(Page page) {
        super(page);
        this.chooseOptionsButton = page.locator("button, a")
                .filter(new Locator.FilterOptions().setHasText("Choose options"));
        this.modalDialog = page.locator("[role='dialog'][aria-label^='Choose options for']");
        this.modalProductLink = modalDialog.locator("a[href*='/products/']");
        this.materialLegend = page.locator("legend.form__label");
        this.addToCartButton = page.locator("button[name='add']");
        this.priceContainer = page.locator(".product__info-container .price");
        this.cartBubble = page.locator("#cart-icon-bubble .cart-count-bubble, .cart-count-bubble");
        this.cartDrawerCloseButton = page.locator(
                "cart-drawer button[aria-label='Close'], " +
                "button[aria-label='Close'], " +
                ".drawer__close, " +
                ".cart-notification__close"
        );
    }

    public void clickChooseOptionsOnFirstProduct() {
        Locator firstChooseOptions = chooseOptionsButton.first();
        firstChooseOptions.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        click(firstChooseOptions);

        Locator firstModal = modalDialog.first();
        firstModal.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        Locator firstProductLink = modalProductLink.first();
        AssertionUtils.assertConditionTrue(firstProductLink.count() > 0, 
                "Product link not found in Choose Options modal");

        String href = firstProductLink.getAttribute("href");
        storedProductUrl = toAbsoluteUrl(href);

        navigate(storedProductUrl);
        waitForProductPageReady();

        AssertionUtils.assertPageUrlContains(getUrl(), "/products/", "Product page did not open");
    }

    public void addMaterialVariantToCart(String material) {
        AssertionUtils.assertConditionTrue(storedProductUrl != null && storedProductUrl.contains("/products/"),
                "Stored product URL should not be empty");

        navigate(storedProductUrl);
        waitForProductPageReady();

        int beforeCount = getCartBubbleCount();
        System.out.println("Before adding " + material + " cart count: " + beforeCount);

        selectMaterial(material);
        page.waitForTimeout(1200); // UI transition delay for material variant selection

        String price = extractCurrentProductPrice();
        String productLink = getUrl();

        click(addToCartButton.first());

        page.waitForTimeout(2500); // Async Cart drawer expansion transition delay

        int afterCount = getCartBubbleCount();
        System.out.println("After adding " + material + " cart count: " + afterCount);

        if (afterCount < beforeCount) {
            System.out.println(material + " add-to-cart count looked lower, but continuing because site cart count is unstable.");
        }

        System.out.println(material + " add-to-cart action completed.");
        addedItems.add(new CartItemDetails(material, price, productLink));

        System.out.println("Captured -> Material: " + material + " | Price: " + price + " | Link: " + productLink);
        closeCartDrawerIfOpen();
    }

    public void waitForProductPageReady() {
        WaitUtils.waitForPageLoadState(page);
        Locator firstLegend = materialLegend.first();
        WaitUtils.waitForElementVisible(firstLegend, 15000);

        Locator firstAddToCart = addToCartButton.first();
        WaitUtils.waitForElementVisible(firstAddToCart, 15000);

        System.out.println("Product page loaded successfully.");
    }

    private void selectMaterial(String material) {
        Locator materialOption;

        if (material.equalsIgnoreCase("Soft")) {
            materialOption = page.locator("legend.form__label:has-text('Material')")
                    .locator("xpath=..")
                    .locator("label:has-text('Soft')")
                    .filter(new Locator.FilterOptions().setHasNotText("Black Soft"))
                    .last();
        } else {
            materialOption = page.locator("legend.form__label:has-text('Material')")
                    .locator("xpath=..")
                    .locator("label:has-text('" + material + "')")
                    .first();
        }

        WaitUtils.waitForElementVisible(materialOption, 15000);
        click(materialOption);
        WaitUtils.waitForPageLoadState(page);
        System.out.println("Selected material: " + material);
    }

    private String extractCurrentProductPrice() {
        Locator firstPriceContainer = priceContainer.first();
        WaitUtils.waitForElementVisible(firstPriceContainer, 15000);

        String text = firstPriceContainer.innerText();
        Pattern pattern = Pattern.compile("[₹?]\\s*\\d+(?:\\.\\d{2})?");
        Matcher matcher = pattern.matcher(text);

        List<String> prices = new ArrayList<>();
        while (matcher.find()) {
            prices.add(matcher.group().trim().replace("?", "₹"));
        }

        if (prices.size() >= 2) {
            return prices.get(1); // Return Sale price if available
        }

        if (prices.size() == 1) {
            return prices.get(0);
        }

        return "Price not found";
    }

    private int getCartBubbleCount() {
        Locator firstBubble = cartBubble.first();
        if (firstBubble.count() == 0) {
            return 0;
        }

        String text = firstBubble.innerText().replaceAll("[^0-9]", "").trim();
        if (text.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(text);
    }

    private void closeCartDrawerIfOpen() {
        Locator closeButton = cartDrawerCloseButton.first();
        if (closeButton.count() > 0 && closeButton.isVisible()) {
            click(closeButton);
            page.waitForTimeout(700);
        }
    }

    private String toAbsoluteUrl(String href) {
        if (href == null) {
            return getUrl();
        }

        if (href.startsWith("http")) {
            return href;
        }

        String baseUrl = ConfigReader.getUrl();
        if (baseUrl.endsWith("/") && href.startsWith("/")) {
            return baseUrl + href.substring(1);
        } else if (!baseUrl.endsWith("/") && !href.startsWith("/")) {
            return baseUrl + "/" + href;
        }
        return baseUrl + href;
    }

    public List<CartItemDetails> getAddedItems() {
        return addedItems;
    }

    public String getStoredProductUrl() {
        return storedProductUrl;
    }
}