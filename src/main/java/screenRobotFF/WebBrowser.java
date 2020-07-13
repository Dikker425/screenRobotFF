package screenRobotFF;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


public class WebBrowser {

    private static WebDriver driver;
    private static final String path = "src/main/resources/screenshots/";
    private static final String inputPATH = "src/main/resources/input.txt";
    private static final String resultTablePATH = "src/main/resources/resultTable.csv";

    public static void autoScreenshot(int screenCount) {

        //Метод для Скриншота

        File dir = new File(path);
        dir.mkdir();

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        try {
            ImageIO.write(screenShot, "JPG", new File(path, "WKr" + screenCount + ".jpg"));
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

        if (System.getProperty("os.name").equals("Linux")) {
            System.setProperty("webdriver.gecko.driver", "src/main/resources/Geckodriver/geckodriver");
        } else {
            System.setProperty("webdriver.gecko.driver", "src/main/resources/Geckodriver/geckodriver.exe");
        }

        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("Robot");
        FirefoxOptions opt = new FirefoxOptions();

        //Прокси
//        myprofile.setPreference("network.proxy.type", 1);
//        myprofile.setPreference("network.proxy.socks", "51.144.228.148");
//        myprofile.setPreference("network.proxy.socks_port", 1080);
        opt.setProfile(myprofile);

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

            if (driver.getCurrentUrl().contains("video.sibnet.ru")) {
                System.out.println("sibnet.ru");

                driver.findElement(By.id("video_html5_wrapper")).sendKeys("video_html5_wrapper" + Keys.SPACE);
                driver.findElement(By.id("video_html5_wrapper")).sendKeys("video_html5_wrapper" + Keys.ARROW_RIGHT);
                driver.findElement(By.id("video_html5_wrapper")).sendKeys("video_html5_wrapper" + Keys.ARROW_RIGHT);
                driver.findElement(By.id("video_html5_wrapper")).sendKeys("video_html5_wrapper" + Keys.ARROW_RIGHT);
                driver.findElement(By.id("video_html5_wrapper")).sendKeys("video_html5_wrapper" + Keys.SPACE);
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
        FileWriter writer = new FileWriter(resultTablePATH);
        writer.write("URL" + "," + "REDIRECT STATUS" + "," + "SCREENSHOT" + "\n");

        //Читаем файл input.txt
        FileReader reader = new FileReader(inputPATH);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String url;
        int screenCount = 1;

        while ((url = bufferedReader.readLine()) != null) {

            try {
                driver.get(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

            playerClickerUGC();
            waitForLoad(driver, 4000);

            String pageSource = driver.getPageSource(); // Для проверки кода на Cloudflare DDoS Protection

            if (!(url.equals(driver.getCurrentUrl().trim()))) {
                writer.write(url + "," + driver.getCurrentUrl().trim() + "," + "WKr_" + screenCount + "\n");
            }
            if (pageSource.contains("https://www.cloudflare.com/5xx-error-landing?utm_source=iuam")) {
                writer.write(url + "," + "Cloudflare DDoS Protection" + "," + "WKr_" + screenCount + "\n");
            } else {
                writer.write(url + "," + "NoRedirect" + "," + "WKr_" + screenCount + "\n");
            }
            autoScreenshot(screenCount);
            screenCount++;

        }
        writer.close();
        reader.close();
        driver.quit();
    }
}
