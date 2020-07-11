package screenRobotFF;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;


public class WebBrowser {

    private static WebDriver driver;

    public static void autoScreenshot(int screenCount) {

        //Метод для Скриншота
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
        //Даем странице прогрузиться
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
        //Настройка профиля браузера "firefox.exe -p , Profile Robot"
        System.setProperty("webdriver.gecko.driver", "src/main/resources/Geckodriver/geckodriver.exe");
        ProfilesIni profile = new ProfilesIni();
        FirefoxOptions opt = new FirefoxOptions();
        FirefoxOptions robot = opt.setProfile(profile.getProfile("Robot"));
        driver = new FirefoxDriver(robot);
        driver.manage().window().maximize();
    }

    public static void ugcSitesPlayerClicker(){

        try {
            WebElement player = driver.findElement(By.className("ytp-progress-bar-padding"));
            Actions actions = new Actions(driver);
            actions.click(player).build().perform();
        } catch (NoSuchElementException e){
        }
    }

    public static void main(String[] args) throws IOException {

        browserSetUp();

        //Открываем файл на запись результатов resultTable.csv (разделетиль "," )
        FileWriter writer = new FileWriter("src\\main\\resources\\resultTable.csv");
        writer.write("URL" + "," + "REDIRECT STATUS" + "," + "SCREENSHOT" + "\n");

        //Читаем файл input.txt
        FileReader reader = new FileReader("src\\main\\resources\\input.txt");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String url;
        int screenCount = 1;

        while ((url = bufferedReader.readLine()) != null) {

            driver.get(url);
            ugcSitesPlayerClicker();
            waitForLoad(driver, 4000);

            String pageSource = driver.getPageSource();

            if (!(url.equals(driver.getCurrentUrl().trim()))) {
                writer.write(url + "," + driver.getCurrentUrl().trim() + "," + "Rbt_" + screenCount + "\n");
            }
            if (pageSource.contains("https://www.cloudflare.com/5xx-error-landing?utm_source=iuam")) {
                waitForLoad(driver, 6000);
                writer.write(url + "," + "Cloudflare DDoS Protection" + "," + "Rbt_" + screenCount + "\n");
            } else {

                writer.write(url + "," + "OK" + "," + "Rbt_" + screenCount + "\n");
            }
            autoScreenshot(screenCount);
            screenCount++;
        }
        writer.close();
        reader.close();
        driver.quit();
    }
}
