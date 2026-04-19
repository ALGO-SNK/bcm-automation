package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ExcelUtils {

    private ExcelUtils() {
    }

    public static String getCellValue(String filePath, String sheetName, int rowNumber, int columnNumber) {
        ensureWorkbookExists(Path.of(filePath));
        DataFormatter formatter = new DataFormatter();

        try (InputStream inputStream = Files.newInputStream(Path.of(filePath));
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                return "";
            }
            Row row = sheet.getRow(rowNumber);
            if (row == null) {
                return "";
            }
            Cell cell = row.getCell(columnNumber);
            return cell == null ? "" : formatter.formatCellValue(cell);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read Excel file: " + filePath, e);
        }
    }

    public static Object[][] sheetToDataProvider(String filePath, String sheetName) {
        ensureWorkbookExists(Path.of(filePath));
        DataFormatter formatter = new DataFormatter();

        try (InputStream inputStream = Files.newInputStream(Path.of(filePath));
             Workbook workbook = WorkbookFactory.create(inputStream)) {
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

    private static void ensureWorkbookExists(Path filePath) {
        if (Files.exists(filePath)) {
            return;
        }
        try {
            Files.createDirectories(filePath.getParent());
            try (Workbook workbook = new XSSFWorkbook();
                 OutputStream outputStream = Files.newOutputStream(filePath)) {
                Sheet sheet = workbook.createSheet("LoginData");

                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("username");
                header.createCell(1).setCellValue("password");

                Row sample = sheet.createRow(1);
                sample.createCell(0).setCellValue("admin");
                sample.createCell(1).setCellValue("admin");

                workbook.write(outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create sample Excel file: " + filePath, e);
        }
    }

    public static List<String> getColumnValues(String filePath, String sheetName, int columnIndex) {
        ensureWorkbookExists(Path.of(filePath));
        DataFormatter formatter = new DataFormatter();
        List<String> values = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(Path.of(filePath));
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                return values;
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(columnIndex);
                values.add(cell == null ? "" : formatter.formatCellValue(cell));
            }
            return values;
        } catch (IOException e) {
            throw new RuntimeException("Unable to read column values from Excel: " + filePath, e);
        }
    }
}
