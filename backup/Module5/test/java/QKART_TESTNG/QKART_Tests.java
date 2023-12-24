package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(ListenerClass.class)
public class QKART_Tests {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

     @BeforeSuite (alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
        @Test(description = "Verify registration happens correctly", priority = 1, groups = {"Sanity_test"})
        @Parameters({"Username", "Password"})
        public void TestCase01(@Optional("testUser") String TC1_Username, @Optional("abc@123") String TC1_Password) throws InterruptedException {
        Boolean status;

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
         status = registration.registerUser(TC1_Username, TC1_Password, true);
        assertTrue(status, "Failed to register new user");
        
        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, TC1_Password);
        assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();
    }

    /*
     * Verify that an existing user is not allowed to re-register on QKart
     */
    @Test (description = "Verify re-registering an already registered user fails", priority = 2, groups = {"Sanity_test"})
    @Parameters({"Username", "Password"})
    public void TestCase02(@Optional("testUser") String TC2_Username, @Optional("abc@123") String TC2_Password) throws InterruptedException {
        Boolean status;

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(TC2_Username, TC2_Password, true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, TC2_Password, false);

        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
        assertFalse(status, "User registered and redirected to login page");
    }

    /*
     * Verify the functinality of the search text box
     */
    @Test (description = "Verify the functionality of search text box", priority = 3, groups = {"Sanity_test"})
    @Parameters({"YonexProduct", "InvalidProductNameToSearchFor"})
    public void TestCase03(String TC3_ProductNameToSearchFor, String TC3_InvalidProductNameToSearchFor) throws InterruptedException {
        boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct(TC3_ProductNameToSearchFor);
        assertTrue(status, "Unable to search for given product");

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        assertNotEquals(searchResults.size(), 0, "There were no results for the given search string");

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            assertTrue(elementText.toUpperCase().contains(TC3_ProductNameToSearchFor), " Test Results contains un-expected values: " + elementText);
        }


        // Search for product
        status = homePage.searchForProduct(TC3_InvalidProductNameToSearchFor);
        assertFalse(status, "Invalid keyword returned results");

        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        assertEquals(searchResults.size(), 0, "Expected: no results , actual: Results were available");
        assertTrue(homePage.isNoResultFound(), "Expected: no results , actual: Results were available");  
    }

    /*
     * Verify the presence of size chart and check if the size chart content is as
     * expected
     */
    @Test (description = "Verify the existence of size chart for certain items and validate contents of size chart", priority = 4, groups = {"Regression_Test"})
    @Parameters({"ShoesToSearchFor"})
    public void TestCase04(@Optional("Running Shoes") String TC4_ProductNameToSearchFor) throws InterruptedException {
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct(TC4_ProductNameToSearchFor);
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            assertTrue(result.verifySizeChartExists(), "Successfully validated presence of Size Chart Link");

            // Verify if size dropdown exists
            assertTrue(result.verifyExistenceofSizeDropdown(driver), "Validated presence of drop down failed");

            // Open the size chart
            assertTrue(result.openSizechart(), "Opening size chart failed");

            // Verify if the size chart contents matches the expected values
            assertTrue(result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver), "Failure while validating contents of Size Chart Link");
            
            status = result.closeSizeChart(driver);
        }
        
    }

    /*
     * Verify the complete flow of checking out and placing order for products is
     * working correctly
     */
    @Test (description = "Verify that a new user can add multiple products in to the cart and Checkout", priority = 5, groups = {"Sanity_test"})
    @Parameters({"Username", "Password", "ProductNameToSearchFor", "ProductNameToSearchFor2", "AddressDetails"})
    public void TestCase05(String TC5_Username, String TC5_Password, String TC5_ProductNameToSearchFor, String TC5_ProductNameToSearchFor2, String TC5_AddressDetails) throws InterruptedException {
        Boolean status;

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser(TC5_Username, TC5_Password, true);
        assertTrue(status, "Failed to register new user");

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, TC5_Password);
        assertTrue(status, "User Perform Login Failed");

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct(TC5_ProductNameToSearchFor);
        homePage.addProductToCart(TC5_ProductNameToSearchFor);
        status = homePage.searchForProduct(TC5_ProductNameToSearchFor2);
        homePage.addProductToCart(TC5_ProductNameToSearchFor2);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(TC5_AddressDetails);
        checkoutPage.selectAddress(TC5_AddressDetails);

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();
    }

    /*
     * Verify the quantity of items in cart can be updated
     */
    @Test (description = "Verify that the contents of the cart can be edited", priority = 6, groups = {"Regression_Test"})
    @Parameters({"Username", "Password", "WatchToSearchFor", "LampToSearchFor", "AddressDetails"})
    public void TestCase06(String TC6_Username, String TC6_Password, String TC6_ProductNameToSearchFor, String TC6_ProductNameToSearchFor2, String TC6_Address) throws InterruptedException {
        Boolean status;

        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser(TC6_Username, TC6_Password, true);
        assertTrue(status, "Failed to register new user");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, TC6_Password);
        assertTrue(status, "User Perform Login Failed");

        homePage.navigateToHome();
        status = homePage.searchForProduct(TC6_ProductNameToSearchFor);
        homePage.addProductToCart(TC6_ProductNameToSearchFor);

        status = homePage.searchForProduct(TC6_ProductNameToSearchFor2);
        homePage.addProductToCart(TC6_ProductNameToSearchFor2);

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearchFor, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearchFor2, 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearchFor, 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(TC6_Address);
        checkoutPage.selectAddress(TC6_Address);

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
        }

        status = driver.getCurrentUrl().endsWith("/thanks");

        homePage.navigateToHome();
        homePage.PerformLogout();
    }

    /*
     * Verify that the cart contents are persisted after logout
     */
    @Test (description = "Verify that the contents made to the cart are saved against the user's login details", priority = 7, groups = {"Regression_Test"})
    @Parameters({"Username", "Password", "ListOfProductsToAddToCart"})
    public void TestCase07(String username, String password, String listOfProducts) throws InterruptedException {
        Boolean status = false;
        List<String> expectedResult = Arrays.asList(listOfProducts.split(";"));

        Register registration = new Register(driver);
        Login login = new Login(driver);
        Home homePage = new Home(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser(username, password, true);
        assertTrue(status, "Failed to register new user");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, password);
        assertTrue(status, "User Perform Login Failed");

        homePage.navigateToHome();
        status = homePage.searchForProduct(expectedResult.get(0));
        homePage.addProductToCart(expectedResult.get(0));

        status = homePage.searchForProduct(expectedResult.get(1));
        homePage.addProductToCart(expectedResult.get(1));

        homePage.PerformLogout();

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, password);

        status = homePage.verifyCartContents(expectedResult);

        homePage.PerformLogout();
    }

    @Test (description = "Verify that insufficient balance error is thrown when the wallet balance is not enough", priority = 8, groups = {"Sanity_test"})
    @Parameters({"Username", "Password", "SofaToSearchFor", "Qty", "AddressDetails"})
    public void TestCase08(String username, String password, String productName, int quantity, String address) throws InterruptedException {
        Boolean status;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(username, password, true);
        assertTrue(status, "Failed to register new user");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, password);
        assertTrue(status, "User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(productName);
        homePage.addProductToCart(productName);

        homePage.changeProductQuantityinCart(productName, quantity);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();

    }

    @Test(dependsOnMethods = {"TestCase10"}, description = "Verify that a product added to a cart is available when a new tab is added", priority = 10, groups = {"Regression_Test"})
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

    }

    @Test (description = "Verify that privacy policy and about us links are working fine", priority = 9, groups = {"Regression_Test"})
    public void TestCase10() throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");
        
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);

        assertTrue(status, "Verifying parent page url didn't change on privacy policy link click failed");

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");

        assertTrue(status, "Verifying new tab opened has Privacy Policy page heading failed");

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");

        assertTrue(status, "Verifying new tab opened has Terms Of Service page heading failed");

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

    }

    @Test (description = "Verify that the contact us dialog works fine", priority = 11, groups = {"Regression_Test"})
    @Parameters({"ContactusUserName", "ContactUsEmail", "QueryContent"})
    public void TestCase11(String contactUsUsername, String contactUsEmail, String queryContent) throws InterruptedException {

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(contactUsUsername);
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(contactUsEmail);
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(queryContent);

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

    }

    @Test (description = "Ensure that the Advertisement Links on the QKART page are clickable", priority = 12, groups = {"Sanity_test"})
    @Parameters({"ProductNameToSearchFor", "AddressDetails"})
    public void TestCase12(String productName, String address) throws InterruptedException {
        Boolean status = false;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "User Perform Login Failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct(productName);
        homePage.addProductToCart(productName);
        homePage.changeProductQuantityinCart(productName, 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;

        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);

    }

    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
                message, status));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

