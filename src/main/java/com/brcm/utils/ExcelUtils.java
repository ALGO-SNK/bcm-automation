package com.brcm.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads values from an .xlsx file.
 *
 * Beginner usage:
 *   String value = ExcelUtils.getCellValue("src/test/resources/data.xlsx", "LoginData", 1, 0);
 *   Object[][] rows = ExcelUtils.sheetToDataProvider("...", "LoginData");
 *
 * Files must exist — if the path is wrong, a clear error is thrown
 * (it does NOT auto-create dummy data).
 */
public final class ExcelUtils {

    private ExcelUtils() {}

    public static String getCellValue(String filePath, String sheetName, int rowNumber, int columnNumber) {
        requireFileExists(filePath);
        DataFormatter formatter = new DataFormatter();
        try (InputStream in = Files.newInputStream(Path.of(filePath));
             Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) return "";
            Row row = sheet.getRow(rowNumber);
            if (row == null) return "";
            Cell cell = row.getCell(columnNumber);
            return cell == null ? "" : formatter.formatCellValue(cell);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Excel file: " + filePath, e);
        }
    }

    /** Reads rows 1..N as Object[][] for @DataProvider (row 0 is treated as header). */
    public static Object[][] sheetToDataProvider(String filePath, String sheetName) {
        requireFileExists(filePath);
        DataFormatter formatter = new DataFormatter();
        try (InputStream in = Files.newInputStream(Path.of(filePath));
             Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                return new Object[0][0];
            }
            int rowCount = sheet.getPhysicalNumberOfRows() - 1;
            int columnCount = sheet.getRow(0).getLastCellNum();
            Object[][] data = new Object[rowCount][columnCount];
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < columnCount; j++) {
                    Cell cell = row == null ? null : row.getCell(j);
                    data[i - 1][j] = cell == null ? "" : formatter.formatCellValue(cell);
                }
            }
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Unable to convert Excel to DataProvider: " + filePath, e);
        }
    }

    public static List<String> getColumnValues(String filePath, String sheetName, int columnIndex) {
        requireFileExists(filePath);
        DataFormatter formatter = new DataFormatter();
        List<String> values = new ArrayList<>();
        try (InputStream in = Files.newInputStream(Path.of(filePath));
             Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) return values;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell cell = row.getCell(columnIndex);
                values.add(cell == null ? "" : formatter.formatCellValue(cell));
            }
            return values;
        } catch (IOException e) {
            throw new RuntimeException("Unable to read column from Excel: " + filePath, e);
        }
    }

    private static void requireFileExists(String filePath) {
        if (!Files.exists(Path.of(filePath))) {
            throw new IllegalStateException("Excel file not found: " + filePath
                    + "  (create it in src/test/resources or fix the path)");
        }
    }
}
