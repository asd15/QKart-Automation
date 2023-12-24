package QKART_SANITY_LOGIN.Module1;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jsoup.select.Evaluator.Id;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Home {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app";

    public Home(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToHome() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    public Boolean PerformLogout() throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            // Find and click on the Logout Button
            WebElement logout_button = driver.findElement(By.className("MuiButton-text"));
            logout_button.click();

            // Wait for Logout to Complete
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Register']")));

            return true;
        } catch (Exception e) {
            // Error while logout
            return false;
        }
    }

    /*
     * Returns Boolean if searching for the given product name occurs without any
     * errors
     */
    public Boolean searchForProduct(String product) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            
            WebElement searchInput = driver.findElement(By.name("search"));
            searchInput.clear();
            searchInput.sendKeys(product);
            Thread.sleep(3000);
            wait.until(ExpectedConditions.or(
                ExpectedConditions.textToBePresentInElementLocated(By.className("css-yg30e6"), product),
                ExpectedConditions.textToBePresentInElementLocated(By.className("css-yg30e6"), product.toUpperCase()),
                ExpectedConditions.textToBePresentInElementLocated(By.xpath("//div[contains(@class, 'loading')]//h4"), "No products found")
            ));
            
            return true;
        } catch (Exception e) {
            System.out.println("Error while searching for a product: " + e.getMessage());
            return false;
        }
    }

    /*
     * Returns Array of Web Elements that are search results and return the same
     */
    public List<WebElement> getSearchResults() {
        List<WebElement> searchResults = new ArrayList<WebElement>();
        try {
            searchResults = driver.findElements(By.xpath("//div[contains(@class, 'css-sycj1h')]"));
            return searchResults;
        } catch (Exception e) {
            System.out.println("There were no search results: " + e.getMessage());
            return searchResults;

        }
    }

    /*
     * Returns Boolean based on if the "No products found" text is displayed
     */
    public Boolean isNoResultFound() {
        Boolean status = false;
        try {
            
            WebElement noProducts = driver.findElement(By.xpath("//h4[text()=' No products found ']"));
            if(noProducts.isDisplayed()){
                status = true;
            }
            return status;
        } catch (Exception e) {
            return status;
        }
    }

    /*
     * Return Boolean if add product to cart is successful
     */
    public Boolean addProductToCart(String productName) {
        try {

            List<WebElement> productsList = driver.findElements(By.xpath("//p[contains(text(), '"+productName+"')]//ancestor::div[3]"));
            
            
            if(!productsList.isEmpty()){
                for(WebElement product : productsList){
                    WebElement addtoCartBtn = product.findElement(By.className("css-54wre3"));
                    addtoCartBtn.click();
                }
                return true;
            }

            System.out.println("Unable to find the given product");
            return false;

        } catch (Exception e) {
            System.out.println("Exception while performing add to cart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting the status of clicking on the checkout button
     */
    public Boolean clickCheckout() {
        Boolean status = false;
        try {
            WebElement checkoutBtn = driver.findElement(By.className("css-177pwqq"));
            checkoutBtn.click();
            status = true;
            return status;
        } catch (Exception e) {
            System.out.println("Exception while clicking on Checkout: " + e.getMessage());
            return status;
        }
    }

    /*
     * Return Boolean denoting the status of change quantity of product in cart
     * operation
     */
    public Boolean changeProductQuantityinCart(String productName, int quantity) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            
            if(quantity < 0){
                System.out.println("Quantity cannot be less than 0");
                return false;
            }

            WebElement itemInCart = driver.findElement(By.xpath("//div[contains(text(), '"+productName+"')]//ancestor::div[3]"));
            int numberOfItemsInCart = Integer.parseInt(itemInCart.findElement(By.className("css-olyig7")).getText());
            WebElement removeBtn = itemInCart.findElement(By.xpath("//*[contains(@data-testid, 'RemoveOutlinedIcon')]//parent::button"));
            WebElement addBtn = itemInCart.findElement(By.xpath("//*[contains(@data-testid, 'AddOutlinedIcon')]//parent::button"));
            int actualItemQuantity = numberOfItemsInCart;
            int expectedItemQuantity = numberOfItemsInCart;
            
            if(numberOfItemsInCart > quantity){
                int numOfTimesToClick = numberOfItemsInCart - quantity;
                while(numOfTimesToClick > 0){
                    actualItemQuantity = Integer.parseInt(itemInCart.findElement(By.className("css-olyig7")).getText());

                    if(actualItemQuantity != expectedItemQuantity){
                        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("css-olyig7"), String.valueOf(expectedItemQuantity)));
                    }
                    
                    removeBtn.click();
                    numOfTimesToClick--;
                    expectedItemQuantity--;
                }
            }
            else if(numberOfItemsInCart < quantity){
                int numOfTimesToClick = quantity - numberOfItemsInCart;
                while(numOfTimesToClick > 0){
                    actualItemQuantity = Integer.parseInt(itemInCart.findElement(By.className("css-olyig7")).getText());

                    if(actualItemQuantity != expectedItemQuantity){
                        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("css-olyig7"), String.valueOf(expectedItemQuantity)));
                    }

                    addBtn.click();
                    numOfTimesToClick--;
                    expectedItemQuantity++;
                }
            }

            return true;
        } catch (Exception e) {
            // if (quantity == 0)
            //     return true;
            System.out.println("exception occurred when updating cart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the cart contains items as expected
     */
    public Boolean verifyCartContents(List<String> expectedCartContents) {
        try {

            boolean status = false;
            List<WebElement> actualCartContents = driver.findElements(By.xpath("//div[contains(@class, 'css-1gjj37g')]/div[1]"));
            

            for(int i=0; i<actualCartContents.size(); i++){
                if(actualCartContents.get(i).getText().strip().toLowerCase().equals(expectedCartContents.get(i).strip().toLowerCase())){
                    status = true;
                }
                else{
                    System.out.println(expectedCartContents.get(i) +" not found");
                    return false;
                }
            }

            return status;

        } catch (Exception e) {
            System.out.println("Exception while verifying cart contents: " + e.getMessage());
            return false;
        }
    }
}
