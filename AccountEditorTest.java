package tests;

import base.BaseTest;
import excel.ExcelUtil;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;

public class AccountEditorTest extends BaseTest {

    private final User user;
    private final List<Map<String, String>> assignedRows;

    @Factory(dataProvider = "userProvider")
    public AccountEditorTest(User user, List<Map<String, String>> assignedRows) {
        this.user = user;
        this.assignedRows = assignedRows;
    }

    @DataProvider(name = "userProvider", parallel = true)
    public static Object[][] userProvider() throws IOException {
        List<User> users = ExcelUtil.getUsersWithPasswords();
        Map<String, List<Map<String, String>>> assignment =
            ExcelUtil.getAssignedRowData(users.stream().map(u -> u.userId).toList());

        Object[][] data = new Object[users.size()][2];
        for (int i = 0; i < users.size(); i++) {
            data[i][0] = users.get(i);
            data[i][1] = assignment.get(users.get(i).userId);
        }
        return data;
    }

    @Test
    public void testEditAccounts() throws Exception {
        setUp();
        login(user.userId, user.password);

        for (Map<String, String> rowData : assignedRows.subList(0, Math.min(250, assignedRows.size()))) {
            editAccountInUI(rowData);
            int rowIndex = Integer.parseInt(rowData.get("__rowIndex"));
            ExcelUtil.markEdited(rowIndex, user.userId);
        }

        logout();
        tearDown();
    }

    private void login(String userId, String password) {
        driver.get("https://yourapp.com/login");
        driver.findElement(By.id("username")).sendKeys(userId);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("loginButton")).click();
    }

    private void logout() {
        driver.findElement(By.id("logoutButton")).click();
    }

    private void editAccountInUI(Map<String, String> row) {
        System.out.println("[" + user.userId + "] Editing: " + row.get("AccountID") + ", MandateRef=" + row.get("MandateRef"));
        // Use row.get("ColumnName") in your Selenium code
    }
}
