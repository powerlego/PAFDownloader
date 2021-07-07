package org.pafdownloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.pafdownloader.containers.Employees;
import org.pafdownloader.scraping.login.LoginProcessing;
import org.pafdownloader.utils.SeleniumUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @author Nicholas Curl
 */
public class Main {

    /**
     * The instance of the logger
     */
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        Scanner scanner = new Scanner(System.in);
        Employees employees = new Employees();
        System.out.println("Please list the directory to save the PAFs");
        Path downloadDirPath = null;
        try {
            downloadDirPath = Paths.get(scanner.nextLine()).toFile().getCanonicalFile().toPath();
        }
        catch (IOException canonical) {
            logger.fatal("Unable to get canonical path", canonical);
            System.exit(1);
        }
        if (!downloadDirPath.toFile().exists()) {
            try {
                Files.createDirectories(downloadDirPath);
            }
            catch (IOException download) {
                logger.fatal("Unable to create download directory", download);
                System.exit(1);
            }
        }
        String download_dir = downloadDirPath.toString();
        ChromeOptions chromeOptions = new ChromeOptions();
        JSONObject settings = new JSONObject(
                "{\n" +
                "   \"recentDestinations\": [\n" +
                "       {\n" +
                "           \"id\": \"Save as PDF\",\n" +
                "           \"origin\": \"local\",\n" +
                "           \"account\": \"\",\n" +
                "       }\n" +
                "   ],\n" +
                "   \"selectedDestinationId\": \"Save as PDF\",\n" +
                "   \"version\": 2\n" +
                "}");
        JSONObject prefs = new JSONObject(
                "{\n" +
                "   \"plugins.plugins_list\":\n" +
                "       [\n" +
                "           {\n" +
                "               \"enabled\": False,\n" +
                "               \"name\": \"Chrome PDF Viewer\"\n" +
                "          }\n" +
                "       ],\n" +
                "   \"download.extensions_to_open\": \"applications/pdf\"\n" +
                "}")
                .put("printing.print_preview_sticky_settings.appState", settings)
                .put("download.default_directory", download_dir);
        chromeOptions.setExperimentalOption("prefs", prefs);
        String url = "https://www.paycomonline.net/v4/cl/cl-login.php";
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.get(url);
        driver.manage().window().maximize();
        LoginProcessing.login(driver, scanner);
        new Actions(driver).moveToElement(driver.findElement(By.id("TalentManagement"))).perform();
        SeleniumUtils.waitUntilVisible(driver, By.id("PersonnelActionForms"));
        new Actions(driver).moveToElement(driver.findElement(By.id("PersonnelActionForms"))).perform();
        SeleniumUtils.waitUntilClickable(driver, By.id("PersonnelActionFormDashboard"));
        SeleniumUtils.waitUntilClickable(driver, By.xpath("//*[@id=\"tabfinal-approved-tab\"]/a"));
        SeleniumUtils.waitUntilVisible(driver,
                                       By.xpath("/html/body/div[4]/div/form/div[1]/div[2]/div[1]/div/div[1]/input")
        );
        driver.findElement(By.xpath("/html/body/div[4]/div/form/div[1]/div[2]/div[1]/div/div[1]/input")).click();
        SeleniumUtils.waitUntilVisible(driver, By.xpath("/html/body/div[7]/ul/li[1]"));
        try {
            driver.findElement(By.xpath("/html/body/div[7]/ul/*[contains(text(), 'PAF Filter')]"));
        }
        catch (NoSuchElementException exception) {
            SeleniumUtils.waitUntilClickable(driver,
                                             By.xpath("/html/body/div[4]/div/form/div[1]/div[2]/span[1]/div/div/a")
            );
            SeleniumUtils.waitUntilClickable(driver,
                                             By.xpath(
                                                     "/html/body/div[4]/div/form/div[1]/div[2]/span[1]/div/div/ul/li[2]/a")
            );
            SeleniumUtils.waitForLoad(driver);
            SeleniumUtils.waitUntilClickable(driver, By.xpath("/html/body/div[4]/div/div/div[3]/ul/li[2]/a"));
            try {
                driver.findElement(By.xpath("//a[contains(text(), 'PAF Filter')]"));
            }
            catch (NoSuchElementException e) {
                logger.fatal("Unable to copy filter", e);
                System.exit(1);
            }
        }
        //driver.close();
    }
}
