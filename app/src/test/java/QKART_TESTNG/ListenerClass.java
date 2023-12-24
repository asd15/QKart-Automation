package QKART_TESTNG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass implements ITestListener {

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

    /**
	 * use this method to create new excel file and set row header
	 * 
	 */
	public void createNewRunResult() {

		// Creating file object of existing excel file
		File fileName = new File("/home/crio-user/workspace/akashsdesai15-ME_QKART_QA/app/RunResults.xlsx");

		try {

			FileInputStream file = new FileInputStream(fileName);

			// Create Workbook instance
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create desired sheet in the workbook
            XSSFSheet sheet = workbook.createSheet("Result");

            // create header row
            XSSFRow headerRow = sheet.createRow(0);

            // set bold header
            XSSFFont font = workbook.createFont();
            font.setBold(true);

            XSSFCellStyle style = workbook.createCellStyle();
            style.setFont(font);

            
            headerRow.createCell(0).setCellValue("Test Case Name");
            headerRow.createCell(1).setCellValue("Parameters");
            headerRow.createCell(2).setCellValue("Status");
            
            for(int i=0; i<3; i++){
                headerRow.getCell(i).setCellStyle(style);
            }

			// Close input stream
			file.close();

			// Crating output stream and writing the updated workbook
			FileOutputStream os = new FileOutputStream(fileName);
			workbook.write(os);

			// Close the workbook and output stream
			workbook.close();
			os.close();

		} catch (Exception e) {
			System.err.println("Exception while updating an existing excel file.\n");
			e.printStackTrace();
		}
	}


    /**
	 * use this method to add row's into the existing excel
	 * 
	 * @param dataToWrite
	 */
	public void writeResultToExcel(String tcName, String parameters, String status) {


		// Creating file object of existing excel file
		File fileName = new File("/home/crio-user/workspace/akashsdesai15-ME_QKART_QA/app/RunResults.xlsx");

		try {

			FileInputStream file = new FileInputStream(fileName);

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheet("Result");

			// Getting the count of existing records
			int rowCount = sheet.getLastRowNum();


			// Creating new row from the next row count
			XSSFRow row = sheet.createRow(++rowCount);

            row.createCell(0).setCellValue((String) tcName);
            row.createCell(1).setCellValue((String) parameters);
            row.createCell(2).setCellValue((String) status);

			// Close input stream
			file.close();

			// Crating output stream and writing the updated workbook
			FileOutputStream os = new FileOutputStream(fileName);
			workbook.write(os);

			// Close the workbook and output stream
			workbook.close();
			os.close();

			System.out.println("Test case result uploaded successfully.\n");

		} catch (Exception e) {
			System.err.println("Exception while updating an existing excel file.\n");
			e.printStackTrace();
		}
	}
    
    public void onStart(ITestContext context) {
        System.out.println("onStart method started");
        createNewRunResult();
    }
    public void onFinish(ITestContext context) {
        System.out.println("onFinish method started");
    }

    public void onTestStart(ITestResult result) {
        System.out.println("New Test Started: " + result.getName());
        takeScreenshot(QKART_Tests.driver, "TestStart", result.getName());
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println("onTestSuccess Method: " + result.getName());
        takeScreenshot(QKART_Tests.driver, "TestSuccess", result.getName());
        writeResultToExcel(result.getName(), Arrays.toString(result.getParameters()), "PASS");
    }

    public void onTestFailure(ITestResult result) {
        System.out.println("onTestFailure Method: " + result.getName());
        takeScreenshot(QKART_Tests.driver, "TestFailed", result.getName());
        writeResultToExcel(result.getName(), Arrays.toString(result.getParameters()), "FAIL");
    }

    public void onTestSkipped(ITestResult result) {
        System.out.println("onTestSkipped Method: " + result.getName());
        writeResultToExcel(result.getName(), Arrays.toString(result.getParameters()), "SKIP");
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("onTestFailedButWithinSuccessPercentage: " + result.getName());
    }
}
