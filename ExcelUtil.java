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

    public static List<User> getUsersWithPasswords() throws IOException {
    List<User> users = new ArrayList<>();
    FileInputStream fis = new FileInputStream(FILE_PATH);
    Workbook wb = new XSSFWorkbook(fis);
    Sheet sheet = wb.getSheet("Users");

    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        String userId = row.getCell(0).getStringCellValue();
        String password = row.getCell(1).getStringCellValue();
        users.add(new User(userId, password));
    }

    fis.close();
    return users;
}



    public static Map<String, List<Account>> getAssignedAccounts(List<String> userIds) throws IOException {
    Map<String, List<Account>> assignment = new HashMap<>();
    for (String user : userIds) {
        assignment.put(user, new ArrayList<>());
    }

    FileInputStream fis = new FileInputStream(FILE_PATH);
    Workbook workbook = new XSSFWorkbook(fis);
    Sheet sheet = workbook.getSheet("Accounts");
    int totalRows = sheet.getLastRowNum();

    for (int i = 1; i <= totalRows; i++) {
        Row row = sheet.getRow(i);
        if (row == null) continue;

        String accountId = getCellValue(row.getCell(0));
        String accountName = getCellValue(row.getCell(1));
        String mandateRef = getCellValue(row.getCell(2)); // Assuming column C = mandate ref

        String assignedUser = userIds.get((i - 1) % userIds.size());
        assignment.get(assignedUser).add(new Account(accountId, accountName, mandateRef, i));
    }

    fis.close();
    return assignment;
}


private static String getCellValue(Cell cell) {
    if (cell == null) return "";
    return switch (cell.getCellType()) {
        case STRING -> cell.getStringCellValue();
        case NUMERIC -> DateUtil.isCellDateFormatted(cell) ?
            cell.getDateCellValue().toString() :
            String.valueOf((long) cell.getNumericCellValue());
        case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
        default -> "";
    };
}

    

    public static Map<String, List<Map<String, String>>> getAssignedRowData(List<String> userIds) throws IOException {
    Map<String, List<Map<String, String>>> userToRows = new HashMap<>();
    for (String user : userIds) userToRows.put(user, new ArrayList<>());

    FileInputStream fis = new FileInputStream(FILE_PATH);
    Workbook workbook = new XSSFWorkbook(fis);
    Sheet sheet = workbook.getSheet("Accounts");

    Row headerRow = sheet.getRow(0);
    int totalRows = sheet.getLastRowNum();
    int totalCols = headerRow.getLastCellNum();

    for (int i = 1; i <= totalRows; i++) {
        Row row = sheet.getRow(i);
        if (row == null) continue;

        Map<String, String> rowData = new LinkedHashMap<>();
        for (int j = 0; j < totalCols; j++) {
            String key = headerRow.getCell(j).getStringCellValue().trim();
            String value = getCellValue(row.getCell(j));
            rowData.put(key, value);
        }

        rowData.put("__rowIndex", String.valueOf(i)); // helpful for marking edited
        String assignedUser = userIds.get((i - 1) % userIds.size());
        userToRows.get(assignedUser).add(rowData);
    }

    fis.close();
    return userToRows;
}


}
