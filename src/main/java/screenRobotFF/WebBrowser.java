package screenRobotFF;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


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

    public static void browserSetUp() {
        //Настройка профиля браузера "firefox.exe -p , Profile Robot"
        System.setProperty("webdriver.gecko.driver", "src/main/resources/Geckodriver/geckodriver.exe");

        //ProfilesIni profile = new ProfilesIni();
        FirefoxOptions opt = new FirefoxOptions();
        FirefoxProfile robotProfile = new FirefoxProfile();
        robotProfile.setPreference("network.proxy.type",1);
        robotProfile.setPreference("network.proxy.socks", "51.144.228.148");
        robotProfile.setPreference("network.proxy.socks_port", 1080);

        opt.setProfile(robotProfile);

        driver = new FirefoxDriver(opt);
        driver.manage().window().maximize();
    }

    public static void playerClickerUGC() {

        try {

            Actions actions = new Actions(driver);

            if (driver.getCurrentUrl().contains("ok.ru")) {

                System.out.println("ok.ru");
                Thread.sleep(2000);

                WebElement playerOKHoverElement = driver.findElement(By.className("vid-card_player"));
                actions.click(playerOKHoverElement).build().perform();
                WebElement playerOK = driver.findElement(By.className("html5-vpl_panel_progress-bar"));
                actions.moveToElement(playerOK, 0, 0).click().build().perform();
            }
            if (driver.getCurrentUrl().contains("vk.com")) {
                System.out.println("vk.com");
                Thread.sleep(2000);

                WebElement playerVKHover = driver.findElement(By.className("videoplayer_media"));
                actions.click(playerVKHover).build().perform();
                WebElement playerVK = driver.findElement(By.className("_bars_wrap"));
                actions.moveToElement(playerVK).click().build().perform();
                actions.click(playerVKHover).build().perform();
            }

            if (driver.getCurrentUrl().contains("youtube.com")) {

                WebElement playerYT = driver.findElement(By.className("ytp-progress-bar-padding"));
                actions.click(playerYT).build().perform();

            }

        } catch (NoSuchElementException | InterruptedException e) {
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
            playerClickerUGC();
            waitForLoad(driver, 4000);

            String pageSource = driver.getPageSource();

            if (!(url.equals(driver.getCurrentUrl().trim()))) {
                writer.write(url + "," + driver.getCurrentUrl().trim() + "," + "Rbt_" + screenCount + "\n");
            }
            if (pageSource.contains("https://www.cloudflare.com/5xx-error-landing?utm_source=iuam")) {
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
