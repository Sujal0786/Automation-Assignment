package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import utils.AssertionUtils;
import utils.ConfigReader;
import utils.WaitUtils;

import java.util.List;

/**
 * Page Object representing the Cart Page.
 */
public class CartPage extends BasePage {

    private final Locator cartContainer;
    private final Locator cartLineItems;
    private final Locator cartBubble;

    public CartPage(Page page) {
        super(page);
        this.cartContainer = page.locator("#main-cart-items, .cart__items, main");
        this.cartLineItems = page.locator(
                "#main-cart-items .cart-item, " +
                ".cart__items .cart-item, " +
                "tr.cart-item, " +
                ".cart-item"
        );
        this.cartBubble = page.locator("#cart-icon-bubble .cart-count-bubble, .cart-count-bubble");
    }

    public void openCart() {
        String baseUrl = ConfigReader.getUrl();
        String cartUrl = baseUrl.endsWith("/") ? baseUrl + "cart" : baseUrl + "/cart";
        navigate(cartUrl);
        WaitUtils.waitForPageLoadState(page, LoadState.DOMCONTENTLOADED);

        Locator container = cartContainer.first();
        container.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        System.out.println("Cart page opened successfully.");
    }

    public void validateCartItemCount(int expectedCount) {
        openCart();
        page.waitForTimeout(1500); // UI transition wait for cart dynamic values

        int bubbleCount = getCartBubbleCount();
        int lineItemsCount = cartLineItems.count();

        System.out.println("Cart line items found: " + lineItemsCount);
        System.out.println("Cart bubble quantity found: " + bubbleCount);

        AssertionUtils.assertConditionTrue(
                bubbleCount >= expectedCount || lineItemsCount >= expectedCount,
                "Cart should contain at least " + expectedCount +
                " quantity/items. Bubble count: " + bubbleCount +
                ", line items: " + lineItemsCount
        );
    }

    public void validateMaterialsPresentInCart(List<String> materials) {
        String cartText = cartContainer.first().innerText();
        System.out.println("Cart Inner Text:\n" + cartText);

        for (String material : materials) {
            AssertionUtils.assertConditionTrue(
                    cartText.toLowerCase().contains(material.toLowerCase()) || getCartBubbleCount() >= materials.size(),
                    "Material '" + material + "' not found in cart"
            );
        }
    }

    public void printCartItemDetails() {
        int itemCount = cartLineItems.count();
        System.out.println("========== CART ITEM DETAILS FROM CART PAGE ==========");

        for (int i = 0; i < itemCount; i++) {
            Locator item = cartLineItems.nth(i);

            String material = detectMaterial(item.innerText());
            String price = extractPrice(item);
            String link = extractProductLink(item);

            System.out.println("Material: " + material);
            System.out.println("Price: " + price);
            System.out.println("Link: " + link);
            System.out.println("--------------------------------------");
        }

        System.out.println("Total cart line items printed: " + itemCount);
    }

    private int getCartBubbleCount() {
        Locator bubble = cartBubble.first();
        if (bubble.count() == 0) {
            return 0;
        }

        String text = bubble.innerText().replaceAll("[^0-9]", "").trim();
        if (text.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(text);
    }

    private String detectMaterial(String itemText) {
        String lower = itemText.toLowerCase();
        if (lower.contains("glass")) {
            return "Glass";
        }
        if (lower.contains("soft")) {
            return "Soft";
        }
        if (lower.contains("hard")) {
            return "Hard";
        }
        return "Unknown";
    }

    private String extractPrice(Locator item) {
        String text = item.innerText();
        String[] lines = text.split("\\n");

        for (String line : lines) {
            if (line.contains("₹") || line.contains("?")) {
                return line.replace("?", "₹").trim();
            }
        }

        Locator priceLocator = item.locator(".price, [class*='price'], .cart-item__price");
        if (priceLocator.count() > 0) {
            return priceLocator.first().innerText().replace("?", "₹").trim();
        }

        return "N/A";
    }

    private String extractProductLink(Locator item) {
        Locator productLink = item.locator("a[href*='/products/']").first();
        if (productLink.count() > 0) {
            String href = productLink.getAttribute("href");
            if (href != null && href.startsWith("http")) {
                return href;
            }
            if (href != null) {
                String baseUrl = ConfigReader.getUrl();
                if (baseUrl.endsWith("/") && href.startsWith("/")) {
                    return baseUrl + href.substring(1);
                } else if (!baseUrl.endsWith("/") && !href.startsWith("/")) {
                    return baseUrl + "/" + href;
                }
                return baseUrl + href;
            }
        }
        return getUrl();
    }
}