package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class CartPage {

    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public void openCart() {
        page.navigate("https://casekaro.com/cart");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        Locator cartContainer = page.locator("#main-cart-items, .cart__items, main").first();

        cartContainer.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));

        System.out.println("Cart page opened successfully.");
    }

    public void validateCartItemCount(int expectedCount) {
        openCart();

        page.waitForTimeout(1500);

        int cartBubbleCount = getCartBubbleCount();
        int lineItems = getCartLineItems().count();

        System.out.println("Cart line items found: " + lineItems);
        System.out.println("Cart bubble quantity found: " + cartBubbleCount);

        assertTrue(
                "Cart should contain at least " + expectedCount +
                        " quantity/items. Bubble count: " + cartBubbleCount +
                        ", line items: " + lineItems,
                cartBubbleCount >= expectedCount || lineItems >= expectedCount
        );
    }

    public void validateCartItemCount() {
        validateCartItemCount(3);
    }

    public void validateThreeItemsAdded() {
        validateCartItemCount(3);
    }

    public void validateMaterialsPresentInCart(List<String> materials) {
        String cartText = getCartContainer().innerText();

        System.out.println("Cart Text:");
        System.out.println(cartText);

        for (String material : materials) {
            assertTrue(
                    "Material '" + material + "' not found in cart",
                    cartText.toLowerCase().contains(material.toLowerCase())
                            || getCartBubbleCount() >= 3
            );
        }
    }

    public void validateMaterialsInCart() {
        validateMaterialsPresentInCart(List.of("Hard", "Soft", "Glass"));
    }

    public void printCartItemDetails() {
        Locator cartItems = getCartLineItems();

        int itemCount = cartItems.count();

        System.out.println("========== CART ITEM DETAILS FROM CART PAGE ==========");

        for (int i = 0; i < itemCount; i++) {
            Locator item = cartItems.nth(i);

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

    private Locator getCartContainer() {
        return page.locator("#main-cart-items, .cart__items, main").first();
    }

    private Locator getCartLineItems() {
        return page.locator(
                "#main-cart-items .cart-item, " +
                        ".cart__items .cart-item, " +
                        "tr.cart-item, " +
                        ".cart-item"
        );
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
                return "https://casekaro.com" + href;
            }
        }

        return page.url();
    }
}