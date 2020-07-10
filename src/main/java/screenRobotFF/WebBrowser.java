package screenRobotFF;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.ProfilesIni;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;


public class WebBrowser {

    private static WebDriver driver;

    public static void autoScreenshot(int screenCount) {

        String path = "src/main/resources/screenshots/";
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        try {
            ImageIO.write(screenShot, "JPG", new File(path, "Rbt_" + screenCount + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void waitForLoad(WebDriver driver, int time) {

        try {
            Thread.sleep(time);
        } catch (Throwable error) {
            error.printStackTrace();
        }

    }

    public static String checkDomain(String data) {

        URI uri = null;
        try {
            uri = new URI(data);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String hostname = uri.getHost();
        if (hostname != null) {
            System.out.println(hostname.startsWith("www.") ? hostname.substring(4) : hostname);
        }
        return hostname;
    }


    public static void browserSetUp() {
        System.setProperty("webdriver.gecko.driver", "src/main/resources/Geckodriver/geckodriver.exe");
        ProfilesIni profile = new ProfilesIni();
        FirefoxOptions opt = new FirefoxOptions();
        FirefoxOptions robot = opt.setProfile(profile.getProfile("Robot"));
        driver = new FirefoxDriver(robot);
        driver.manage().window().maximize();
    }

    public static void main(String[] args) throws IOException {

        browserSetUp();

        FileWriter writer = new FileWriter("src\\main\\resources\\resultTable.csv");
        writer.write("URL" + "," + "REDIRECT STATUS" + "," + "SCREENSHOT" + "\n");

        //Читаем файл input.txt
        FileReader reader = new FileReader("src\\main\\resources\\input.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String url;
        int screenCount = 1;

        while ((url = bufferedReader.readLine()) != null) {

            driver.get(url);
            waitForLoad(driver, 4000);
            autoScreenshot(screenCount);

            System.out.println(driver.getCurrentUrl().trim());
            if (!(url.equals(driver.getCurrentUrl().trim()))) {
                writer.write(url + "," + driver.getCurrentUrl().trim() + "," + "Rbt_" + screenCount + "\n");
            } else {
                writer.write(url + "," + "OK" + "," + "Rbt_" + screenCount + "\n");
            }
            screenCount++;

        }
        writer.close();
        reader.close();
        driver.quit();
    }
}
