package screenRobotFF;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WebBrowser {

    public static void autoScreenshot() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd hh mm ss a");
        Calendar now = Calendar.getInstance();
        String path = "src/main/resources";
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        try {
            ImageIO.write(screenShot, "JPG", new File(path, formatter.format(now.getTime()) + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void browserSetUp() {

    }

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {

        System.setProperty("webdriver.gecko.driver", "C:/Users/dmitry.b/Documents/Geckodriver/geckodriver.exe");

        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile testprofile = profile.getProfile("Robot");
        FirefoxOptions opt = new FirefoxOptions();
        opt.setProfile(testprofile);
        FirefoxDriver driver = new FirefoxDriver(opt);
        
        driver.get("http://ya.ru");
        driver.findElement(By.tagName("body")).sendKeys(Keys.CONTROL + "t");
        autoScreenshot();
        driver.get("https://www.youtube.com/watch?v=N8Rr7rVf1RA");
        autoScreenshot();
        driver.close();

    }
}
