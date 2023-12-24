package QKART_TESTNG;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Table.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

public class DP {

    /**
	 * 
	 * use this method to read a particular Cell value
	 * 
	 * @param sheetName
	 * @param rowIndex
	 * @param colIndex
	 * @throws IOException
	 */
	public Object[][] getSheetData(String sheetName) throws IOException {
        File fileName = new File("/home/crio-user/workspace/akashsdesai15-ME_QKART_QA/app/src/test/resources/Dataset.xlsx");
        
        FileInputStream file = new FileInputStream(fileName);

        // Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        
        XSSFSheet sheet = workbook.getSheet(sheetName);

        int rowCount = 3;
		int colsCount = sheet.getRow(1).getLastCellNum();

        Object [][] arrayExcelData = new Object[rowCount][colsCount-1];

        for (int rowIndex=1; rowIndex <= rowCount; rowIndex++){
            for (int colIndex=1; colIndex < colsCount; colIndex++){
                
                XSSFRow row = sheet.getRow(rowIndex);
                
                XSSFCell cell = row.getCell(colIndex);

                if (cell.getCellType() == CellType.NUMERIC) {
                    arrayExcelData[rowIndex-1][colIndex-1] = (int)cell.getNumericCellValue();
                } else {
                    arrayExcelData[rowIndex-1][colIndex-1] = cell.getStringCellValue();
                }               
            }
        }

        file.close();
        workbook.close();

        return arrayExcelData;
    }


    @DataProvider (name = "data-provider")
    public Object[][] dpMethod (Method m) throws IOException{
        switch (m.getName()){
            case "TestCase01":
                return getSheetData("TestCase01");
            case "TestCase02":
                return getSheetData("TestCase02");
            case "TestCase03":
                return getSheetData("TestCase03");
            case "TestCase04":
                return getSheetData("TestCase04");
            case "TestCase05":
                return getSheetData("TestCase05");
            case "TestCase06":
                return getSheetData("TestCase06");
            case "TestCase07":
                return getSheetData("TestCase07");
            case "TestCase08":
                return getSheetData("TestCase08");
            case "TestCase11":
                return getSheetData("TestCase11");
            case "TestCase12":
                return getSheetData("TestCase12");
        }
        return null;
    }
}