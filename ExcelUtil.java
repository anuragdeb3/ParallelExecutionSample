package excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ExcelUtil {

    private static final String FILE_PATH = "resources/your_file.xlsx";
    private static Workbook workbook;
    private static Sheet accountSheet;

    public static Map<String, List<Integer>> getUserAssignments(List<String> users) throws IOException {
        FileInputStream fis = new FileInputStream(FILE_PATH);
        workbook = new XSSFWorkbook(fis);
        accountSheet = workbook.getSheet("Accounts");

        int totalRows = accountSheet.getLastRowNum(); // Skip header
        Map<String, List<Integer>> assignment = new HashMap<>();
        for (String user : users) assignment.put(user, new ArrayList<>());

        for (int i = 1; i <= totalRows; i++) {
            String user = users.get((i - 1) % users.size());
            assignment.get(user).add(i);
        }

        fis.close();
        return assignment;
    }

    public static void markEdited(int rowIndex, String userId) throws IOException {
        FileInputStream fis = new FileInputStream(FILE_PATH);
        workbook = new XSSFWorkbook(fis);
        accountSheet = workbook.getSheet("Accounts");
        Row row = accountSheet.getRow(rowIndex);
        Cell nameCell = row.getCell(1);
        nameCell.setCellValue(nameCell.getStringCellValue() + " [Edited]");

        Cell editorCell = row.getCell(3);
        if (editorCell == null) editorCell = row.createCell(3);
        editorCell.setCellValue(userId);

        fis.close();
        FileOutputStream fos = new FileOutputStream(FILE_PATH);
        workbook.write(fos);
        fos.close();
    }
}
