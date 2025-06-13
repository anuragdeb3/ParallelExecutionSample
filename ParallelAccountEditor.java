import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ParallelAccountEditor {

    private static final String FILE_PATH = "your_file.xlsx";
    private static Workbook workbook;
    private static Sheet userSheet;
    private static Sheet accountSheet;

    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(FILE_PATH);
        workbook = new XSSFWorkbook(fis);
        userSheet = workbook.getSheet("Users");
        accountSheet = workbook.getSheet("Accounts");

        List<String> userIds = getUserIds();
        int totalAccounts = accountSheet.getLastRowNum();

        // Distribute rows
        Map<String, List<Integer>> userAssignments = divideRows(userIds, totalAccounts);

        // Create threads
        List<Thread> threads = new ArrayList<>();
        for (String userId : userIds) {
            List<Integer> rows = userAssignments.get(userId);
            Thread t = new Thread(() -> editAccounts(userId, rows));
            threads.add(t);
            t.start();
        }

        // Wait for all to finish
        for (Thread t : threads) {
            t.join();
        }

        // Save and close
        fis.close();
        FileOutputStream fos = new FileOutputStream(FILE_PATH);
        workbook.write(fos);
        fos.close();
        workbook.close();

        System.out.println("All users have completed editing.");
    }

    private static List<String> getUserIds() {
        List<String> userIds = new ArrayList<>();
        for (int i = 1; i <= userSheet.getLastRowNum(); i++) {
            Row row = userSheet.getRow(i);
            userIds.add(row.getCell(0).getStringCellValue());
        }
        return userIds;
    }

    private static Map<String, List<Integer>> divideRows(List<String> userIds, int totalRows) {
        Map<String, List<Integer>> assignment = new HashMap<>();
        int numUsers = userIds.size();
        for (int i = 0; i < userIds.size(); i++) {
            assignment.put(userIds.get(i), new ArrayList<>());
        }

        for (int i = 1; i <= totalRows; i++) { // Row 0 is header
            String assignedUser = userIds.get((i - 1) % numUsers);
            assignment.get(assignedUser).add(i);
        }

        return assignment;
    }

    private static void editAccounts(String userId, List<Integer> rowIndexes) {
        for (Integer rowNum : rowIndexes) {
            Row row = accountSheet.getRow(rowNum);
            if (row != null) {
                Cell nameCell = row.getCell(1); // AccountName
                Cell editorCell = row.getCell(3); // Editor
                if (editorCell == null) editorCell = row.createCell(3);

                // Simulate edit
                synchronized (row) {
                    String oldName = nameCell.getStringCellValue();
                    nameCell.setCellValue(oldName + " [Edited]");
                    editorCell.setCellValue(userId);
                }

                System.out.println("User " + userId + " edited row " + rowNum);
            }
        }
    }
}
