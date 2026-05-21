package models;

public class CartItemDetails {

    private final String material;
    private final String price;
    private final String productLink;

    public CartItemDetails(String material, String price, String productLink) {
        this.material = material;
        this.price = price;
        this.productLink = productLink;
    }

    public String getMaterial() {
        return material;
    }

    public String getPrice() {
        return price;
    }

    public String getProductLink() {
        return productLink;
    }
}
