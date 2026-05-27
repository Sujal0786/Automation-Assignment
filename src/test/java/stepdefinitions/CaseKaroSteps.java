package stepdefinitions;

import base.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.CartItemDetails;
import static org.junit.Assert.assertEquals;
import pages.HomePage;
import pages.MobileCoversPage;
import pages.ProductPage;

import java.util.List;

public class CaseKaroSteps extends BaseTest {

    private HomePage homePage;
    private MobileCoversPage mobileCoversPage;
    private ProductPage productPage;
    private String currentSearchedBrand;

    @Before
    public void beforeScenario() {
        setUp();
        homePage = new HomePage(getPage());
        mobileCoversPage = new MobileCoversPage(getPage());
        productPage = new ProductPage(getPage());
    }

    @After
    public void afterScenario() {
        tearDown();
    }

    @Given("user opens CaseKaro website")
    public void userOpensCaseKaroWebsite() {
        homePage.openWebsite();
    }

    @When("user clicks on Mobile Covers")
    public void userClicksOnMobileCovers() {
        homePage.clickMobileCovers();
    }

    @And("user searches {string} in phone model search")
    public void userSearchesBrandInPhoneModelSearch(String brand) {
        this.currentSearchedBrand = brand;
        mobileCoversPage.searchBrand(brand);
    }

    @Then("only {string} phone models should be visible")
    public void onlyBrandPhoneModelsShouldBeVisible(String brand) {
        mobileCoversPage.validateBrandModelsVisible(brand);
    }

    @And("other brand phone models should not be visible")
    public void otherBrandPhoneModelsShouldNotBeVisible() {
        mobileCoversPage.validateOtherBrandsNotVisible(
                currentSearchedBrand != null ? currentSearchedBrand : "Apple"
        );
    }

    @When("user selects exactly {string} from suggestions")
    public void userSelectsExactlyModelFromSuggestions(String modelName) {
        mobileCoversPage.searchAndSelectModel(modelName);
    }

    @And("user clicks Choose Options on first product card")
    public void userClicksChooseOptionsOnFirstProductCard() {
        productPage.clickChooseOptionsOnFirstProduct();
    }

    @And("user adds {string} material variant to cart")
    public void userAddsMaterialVariantToCart(String material) {
        productPage.addMaterialVariantToCart(material);
    }

    @Then("cart should contain all {string} material variants")
    public void cartShouldContainAllMaterialVariants(String expectedCountStr) {
        int expectedCount = Integer.parseInt(expectedCountStr);
        assertEquals(
                "All " + expectedCount + " material variants should be captured after Add to Cart",
                expectedCount,
                productPage.getAddedItems().size()
        );

        System.out.println("Cart validation passed using captured added item details.");
    }

    @And("user prints material price and product link in console")
    public void userPrintsMaterialPriceAndProductLinkInConsole() {
        List<CartItemDetails> items = productPage.getAddedItems();

        System.out.println("========== CART ITEM DETAILS ==========");

        for (CartItemDetails item : items) {
            System.out.println("Material: " + item.getMaterial());
            System.out.println("Price: " + item.getPrice());
            System.out.println("Link: " + item.getProductLink());
            System.out.println("--------------------------------------");
        }

        System.out.println("Total Items Printed: " + items.size());
    }
}