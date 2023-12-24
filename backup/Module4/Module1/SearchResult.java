package QKART_SANITY_LOGIN.Module1;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.Log;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResult {
    WebElement parentElement;

    public SearchResult(WebElement SearchResultElement) {
        this.parentElement = SearchResultElement;
    }

    /*
     * Return title of the parentElement denoting the card content section of a
     * search result
     */
    public String getTitleofResult() {
        String titleOfSearchResult = "";
        titleOfSearchResult = parentElement.findElement(By.className("css-yg30e6")).getText();
        return titleOfSearchResult;
    }

    /*
     * Return Boolean denoting if the open size chart operation was successful
     */
    public Boolean openSizechart() {
        try {
            parentElement.findElement(By.className("css-1urhf6j")).click();
            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception while opening Size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the close size chart operation was successful
     */
    public Boolean closeSizeChart(WebDriver driver) {
        try {
            Actions action = new Actions(driver);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            // Clicking on "ESC" key closes the size chart modal
            action.sendKeys(Keys.ESCAPE);
            action.perform();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("css-uhb5lp")));
            return true;
        } catch (Exception e) {
            System.out.println("Exception while closing the size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean based on if the size chart exists
     */
    public Boolean verifySizeChartExists() {
        Boolean status = false;
        try {
            WebElement sizeChartBtn = parentElement.findElement(By.xpath("//button[text()='Size chart']"));
            if(sizeChartBtn.isDisplayed()){              
                if(sizeChartBtn.getText().matches("SIZE CHART")){
                    status = true;
                }
                else{
                    status = false;
                }
            }
            return status;
        } catch (Exception e) {
            return status;
        }
    }

    /*
     * Return Boolean if the table headers and body of the size chart matches the
     * expected values
     */
    public Boolean validateSizeChartContents(List<String> expectedTableHeaders, List<List<String>> expectedTableBody,
            WebDriver driver) {
        Boolean status = true;
        try {
            List<WebElement> actualTableHeaders = driver.findElements(By.className("css-1rg3xbn"));
            List<String> actualTableHeadersText = new ArrayList<String>();
            for(WebElement actualTableHeader : actualTableHeaders){
                String actualTableHeaderText = actualTableHeader.getText();
                actualTableHeadersText.add(actualTableHeaderText);  
            }

            status = expectedTableHeaders.equals(actualTableHeadersText);

            
            List<WebElement> actualTableRows = driver.findElements(By.className("css-171yt5d"));
            List<String> actualTableRowElemsText = new ArrayList<String>();
            List<String> expectedTableRowElemsText = new ArrayList<String>();

            for(WebElement actualTableRow : actualTableRows){
                List<WebElement> actualTableRowElems = actualTableRow.findElements(By.className("css-c6d53g"));
                for(WebElement actualTableRowElem : actualTableRowElems){
                    actualTableRowElemsText.add(actualTableRowElem.getText());
                }
            }

            for(List<String> expectedTableBodyRow : expectedTableBody){
                for(String expectedTableBodyCell : expectedTableBodyRow){
                    expectedTableRowElemsText.add(expectedTableBodyCell);
                }
            }

            status = expectedTableRowElemsText.equals(actualTableRowElemsText);


            return status;

        } catch (Exception e) {
            System.out.println("Error while validating chart contents");
            return false;
        }
    }

    /*
     * Return Boolean based on if the Size drop down exists
     */
    public Boolean verifyExistenceofSizeDropdown(WebDriver driver) {
        Boolean status = false;
        try {
            WebElement sizeDropdown = parentElement.findElement(By.className("css-1gtikml"));
            if(sizeDropdown.isDisplayed()){
                status = true;
            }
            else{
                status = false;
            }
            return status;
        } catch (Exception e) {
            return status;
        }
    }
}