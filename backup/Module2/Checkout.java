package QKART_SANITY_LOGIN.Module1;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    public Checkout(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /*
     * Return Boolean denoting the status of adding a new address
     */
    public Boolean addNewAddress(String addresString) {
        try {
            
            WebElement addNewAddressBtn = driver.findElement(By.className("css-rfvjbl"));
            addNewAddressBtn.click();
            Thread.sleep(1000);
            WebElement addressTextArea = driver.findElement(By.className("css-u36398"));
            addressTextArea.sendKeys(addresString);
            WebElement addBtn = driver.findElement(By.xpath("//button[contains(@class,'css-177pwqq') and text()='Add']"));
            addBtn.click();

            return true;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;

        }
    }

    /*
     * Return Boolean denoting the status of selecting an available address
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            
            List<WebElement> AddressList = driver.findElements(By.xpath("//p[text()='"+addressToSelect+"']"));

            if(!AddressList.isEmpty()){
                for(WebElement address : AddressList){

                    address.click();
    
                    return true;
                }
            }

            System.out.println("Unable to find the given address");
            return false;
        } catch (Exception e) {
            System.out.println("Exception Occurred while selecting the given address: " + e.getMessage());
            return false;
        }

    }

    /*
     * Return Boolean denoting the status of place order action
     */
    public Boolean placeOrder() {
        try {

            WebElement placeOrderBtn = driver.findElement(By.xpath("//button[contains(@class,'css-177pwqq') and text()='PLACE ORDER']"));
            placeOrderBtn.click();

            return true;

        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the insufficient balance message is displayed
     */
    public Boolean verifyInsufficientBalanceMessage() {
        try {
            WebElement insuffElementMessage = driver.findElement(By.xpath("//div[@id='notistack-snackbar']"));
            if(insuffElementMessage.getText().contains("You do not have enough balance in your wallet for this purchase")){
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    }
}
