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

    @And("user searches Apple in phone model search")
    public void userSearchesAppleInPhoneModelSearch() {
        mobileCoversPage.searchBrandApple();
    }

    @Then("only Apple phone models should be visible")
    public void onlyApplePhoneModelsShouldBeVisible() {
        mobileCoversPage.validateAppleModelsVisible();
    }

    @And("other brand phone models should not be visible")
    public void otherBrandPhoneModelsShouldNotBeVisible() {
        mobileCoversPage.validateOtherBrandsNotVisible();
    }

    @When("user selects exactly iPhone 16 Pro from suggestions")
    public void userSelectsExactlyIphone16ProFromSuggestions() {
        mobileCoversPage.searchAndSelectIphone16Pro();
    }

    @And("user clicks Choose Options on first product card")
    public void userClicksChooseOptionsOnFirstProductCard() {
        productPage.clickChooseOptionsOnFirstProduct();
    }

    @And("user adds Hard material variant to cart")
    public void userAddsHardMaterialVariantToCart() {
        productPage.addHardMaterial();
    }

    @And("user adds Soft material variant to cart")
    public void userAddsSoftMaterialVariantToCart() {
        productPage.addSoftMaterial();
    }

    @And("user adds Glass material variant to cart")
    public void userAddsGlassMaterialVariantToCart() {
        productPage.addGlassMaterial();
    }

    @Then("cart should contain all 3 material variants")
    public void cartShouldContainAll3MaterialVariants() {
       assertEquals(
        "All 3 material variants should be captured after Add to Cart",
        3,
        productPage.getAddedItems().size()
);
        

        System.out.println("Cart validation passed using captured added item details.");
    }

    @And("user prints material price and product link in console")
    public void userPrintsMaterialPriceAndProductLinkInConsole() {
        List<CartItemDetails> items = productPage.getAddedItems();

        assertEquals(
        "All 3 item details should be printed",
        3,
        items.size()
);

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