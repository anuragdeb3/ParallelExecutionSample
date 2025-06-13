package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseTest {
    protected WebDriver driver;

    public void setUp() {
        driver = new ChromeDriver(); // or WebDriverManager for setup
        driver.manage().window().maximize();
    }

    public void tearDown() {
        if (driver != null)
            driver.quit();
    }
}
