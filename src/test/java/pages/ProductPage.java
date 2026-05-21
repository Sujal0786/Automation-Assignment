package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import models.CartItemDetails;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class ProductPage {

    private final Page page;
    private String storedProductUrl;
    private final List<CartItemDetails> addedItems = new ArrayList<>();

    public ProductPage(Page page) {
        this.page = page;
    }

    public void clickChooseOptionsOnFirstProduct() {
        Locator chooseOptions = page.locator("button, a")
                .filter(new Locator.FilterOptions().setHasText("Choose options"))
                .first();

        chooseOptions.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        chooseOptions.scrollIntoViewIfNeeded();
        chooseOptions.click();

        Locator modal = page.locator("[role='dialog'][aria-label^='Choose options for']").first();

        modal.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        Locator productLink = modal.locator("a[href*='/products/']").first();

        assertTrue("Product link not found in Choose Options modal", productLink.count() > 0);

        String href = productLink.getAttribute("href");
        storedProductUrl = toAbsoluteUrl(href);

        page.navigate(storedProductUrl);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        waitForProductPageReady();

        assertTrue("Product page did not open", page.url().contains("/products/"));
    }

    public void addHardMaterial() {
        addMaterialVariantToCart("Hard");
    }

    public void addSoftMaterial() {
        addMaterialVariantToCart("Soft");
    }

    public void addGlassMaterial() {
        addMaterialVariantToCart("Glass");
    }

   public void addMaterialVariantToCart(String material) {
    assertTrue("Stored product URL should not be empty",
            storedProductUrl != null && storedProductUrl.contains("/products/"));

    page.navigate(storedProductUrl);
    page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    waitForProductPageReady();

    int beforeCount = getCartBubbleCount();

    System.out.println("Before adding " + material + " cart count: " + beforeCount);

    selectMaterial(material);
    page.waitForTimeout(1200);

    String price = extractCurrentProductPrice();
    String productLink = page.url();

    clickAddToCart();

    page.waitForTimeout(2500);

    int afterCount = getCartBubbleCount();

    System.out.println("After adding " + material + " cart count: " + afterCount);

    assertTrue(
            material + " was not added as new cart item",
            afterCount > beforeCount
    );

    addedItems.add(new CartItemDetails(material, price, productLink));

    System.out.println("Captured -> Material: " + material + " | Price: " + price + " | Link: " + productLink);

    closeCartDrawerIfOpen();
}

    public void waitForProductPageReady() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        Locator materialLegend = page.locator("legend.form__label").first();

        materialLegend.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        Locator addToCartButton = page.locator("button[name='add']").first();

        addToCartButton.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

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

        materialOption.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        materialOption.scrollIntoViewIfNeeded();
        materialOption.click(new Locator.ClickOptions().setForce(true));

        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        System.out.println("Selected material: " + material);
    }

    private String extractCurrentProductPrice() {
    Locator priceContainer = page.locator(".product__info-container .price").first();

    priceContainer.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(15000));

    String text = priceContainer.innerText();

    Pattern pattern = Pattern.compile("[₹?]\\s*\\d+(?:\\.\\d{2})?");
    Matcher matcher = pattern.matcher(text);

    List<String> prices = new ArrayList<>();

    while (matcher.find()) {
        prices.add(matcher.group().trim().replace("?", "₹"));
    }

    if (prices.size() >= 2) {
        return prices.get(1); // sale price
    }

    if (prices.size() == 1) {
        return prices.get(0);
    }

    return "Price not found";
}
    private void clickAddToCart() {
    Locator addToCartButton = page.locator("button[name='add']").first();

    addToCartButton.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(15000));

    addToCartButton.scrollIntoViewIfNeeded();
    addToCartButton.click(new Locator.ClickOptions().setForce(true));
}
    private void waitForItemAddedSuccessfully() {
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        page.waitForTimeout(2000);

        boolean success =
                page.locator("cart-drawer.drawer.active, .cart-notification.active").count() > 0
                        || page.getByText("View cart").count() > 0
                        || page.getByText("Added to cart").count() > 0
                        || page.getByText("Item added to your cart").count() > 0
                        || getCartBubbleCount() >= addedItems.size() + 1;

        System.out.println("Cart bubble count after add: " + getCartBubbleCount());

        assertTrue("Item was not added to cart successfully", success);
    }

    private int getCartBubbleCount() {
        Locator cartBubble = page.locator("#cart-icon-bubble .cart-count-bubble, .cart-count-bubble").first();

        if (cartBubble.count() == 0) {
            return 0;
        }

        String text = cartBubble.innerText().replaceAll("[^0-9]", "").trim();

        if (text.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(text);
    }

    private int getCartLineItemCount() {
        return page.locator(
                "cart-drawer tr.cart-item, " +
                        "cart-drawer .cart-item, " +
                        "#main-cart-items .cart-item, " +
                        "tr.cart-item"
        ).count();
    }

    private void closeCartDrawerIfOpen() {
        Locator closeButton = page.locator(
                "cart-drawer button[aria-label='Close'], " +
                        "button[aria-label='Close'], " +
                        ".drawer__close, " +
                        ".cart-notification__close"
        ).first();

        if (closeButton.count() > 0 && closeButton.isVisible()) {
            closeButton.click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(700);
        }
    }

    private void takeMaterialScreenshot(String material) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("screenshots/before-select-" + material + ".png"))
                .setFullPage(true));
    }

    private String toAbsoluteUrl(String href) {
        if (href == null) {
            return page.url();
        }

        if (href.startsWith("http")) {
            return href;
        }

        return "https://casekaro.com" + href;
    }

    public List<CartItemDetails> getAddedItems() {
        return addedItems;
    }

    public String getStoredProductUrl() {
        return storedProductUrl;
    }
}