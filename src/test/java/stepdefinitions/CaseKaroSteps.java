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
import pages.CartPage;
import pages.HomePage;
import pages.MobileCoversPage;
import pages.ProductPage;

import java.util.Arrays;
import java.util.List;

public class CaseKaroSteps extends BaseTest {

    private HomePage homePage;
    private MobileCoversPage mobileCoversPage;
    private ProductPage productPage;
    private CartPage cartPage;
    private String currentSearchedBrand;

    @Before
    public void beforeScenario() {
        setUp();
        homePage = new HomePage(getPage());
        mobileCoversPage = new MobileCoversPage(getPage());
        productPage = new ProductPage(getPage());
        cartPage = new CartPage(getPage());
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

    @And("user opens the cart")
    public void userOpensTheCart() {
        cartPage.openCart();
    }

    @Then("cart should contain all {string} material variants")
    public void cartShouldContainAllMaterialVariants(String expectedCountStr) {
        int expectedCount = Integer.parseInt(expectedCountStr);
        cartPage.validateCartItemCount(expectedCount);
        cartPage.validateMaterialsPresentInCart(Arrays.asList("Hard", "Soft", "Glass"));
        System.out.println("Cart validation passed successfully on the Cart Page.");
    }

    @And("user prints material price and product link in console")
    public void userPrintsMaterialPriceAndProductLinkInConsole() {
        cartPage.printCartItemDetails();
    }
}