package tests;

import base.BaseTest;
import excel.ExcelUtil;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

public class AccountEditorTest extends BaseTest {

    private static final List<String> USERS = List.of("U001", "U002", "U003", "U004");
    private String userId;
    private List<Integer> assignedRows;

    @Factory(dataProvider = "userProvider")
    public AccountEditorTest(String userId, List<Integer> assignedRows) {
        this.userId = userId;
        this.assignedRows = assignedRows;
    }

    @DataProvider(name = "userProvider", parallel = true)
    public static Object[][] userProvider() throws Exception {
        Map<String, List<Integer>> userMap = ExcelUtil.getUserAssignments(USERS);
        Object[][] data = new Object[USERS.size()][2];
        for (int i = 0; i < USERS.size(); i++) {
            data[i][0] = USERS.get(i);
            data[i][1] = userMap.get(USERS.get(i));
        }
        return data;
    }

    @Test
    public void testEditAccounts() throws Exception {
        setUp();
        System.out.println("Logging in: " + userId);

        // Simulate Login (Replace with actual Selenium actions)
        login(userId);

        // Edit 250 rows assigned to the user
        for (int rowIndex : assignedRows.subList(0, Math.min(250, assignedRows.size()))) {
            // Simulate edit via Selenium
            editAccountInUI(rowIndex);
            ExcelUtil.markEdited(rowIndex, userId);
        }

        logout(userId);
        tearDown();
    }

    private void login(String userId) {
        driver.get("https://yourapp.com/login");
        // Selenium steps to log in
    }

    private void editAccountInUI(int rowIndex) {
        // Navigate to account detail page or list view
        // Perform edits using Selenium
        System.out.println("[" + userId + "] editing row: " + rowIndex);
    }

    private void logout(String userId) {
        // Perform logout
        System.out.println("[" + userId + "] logged out.");
    }
}
