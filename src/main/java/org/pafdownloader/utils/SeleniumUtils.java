package org.pafdownloader.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Nicholas Curl
 */
public class SeleniumUtils {

    /**
     * The instance of the logger
     */
    private static final Logger logger = LogManager.getLogger(SeleniumUtils.class);

    public static void waitUntilClickable(WebDriver driver, By by) {
        WebDriverWait driverWait = new WebDriverWait(driver, 30);
        driverWait.until(ExpectedConditions.elementToBeClickable(by)).click();
        waitForLoad(driver);
    }

    public static void waitForLoad(WebDriver driver) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> ((JavascriptExecutor) driver1).executeScript(
                "return document.readyState").equals("complete");
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(pageLoadCondition);
    }

    public static void waitUntilClickable(WebDriver driver, WebElement element) {
        WebDriverWait driverWait = new WebDriverWait(driver, 30);
        driverWait.until(ExpectedConditions.elementToBeClickable(element)).click();
        waitForLoad(driver);
    }

    public static void waitUntilVisible(WebDriver driver, By by) {
        WebDriverWait driverWait = new WebDriverWait(driver, 30);
        try {
            driverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        }
        catch (TimeoutException e) {
            logger.debug("Timeout", e);
        }
    }

    public static void waitUntilVisible(WebDriver driver, WebElement element) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 30);
        try {
            webDriverWait.until(ExpectedConditions.visibilityOf(element));
        }
        catch (TimeoutException e) {
            logger.debug("Timeout", e);
        }
    }
}
