package com.example.service;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;

@Service
public class DonationWhatsAppWebService {

	private WebDriver driver;

	/**
	 * Public API used by DonationReceiptService
	 */
	public boolean sendMessageWithAttachment(String mobile, String message, File attachment) {

		try {
			
			getDriver();

			long start = System.currentTimeMillis();

			openChat(mobile, message);

			sendTextMessage(attachment);

			Thread.sleep(3000);

			/*
			 * new Actions(driver) .sendKeys(Keys.ENTER) .perform();
			 */

			System.out.println("Time : " + (System.currentTimeMillis() - start) + " ms");

			return true;

		} catch (Exception e) {

			e.printStackTrace();

			return false;

		}

	}
	
	public synchronized WebDriver getDriver() {

	    try {

	        if (driver != null) {

	            driver.getCurrentUrl();

	            return driver;

	        }

	    } catch (Exception e) {

	        System.out.println("Old session is closed.");

	        try {
	            driver.quit();
	        } catch (Exception ignored) {
	        }

	        driver = null;
	    }

	    WebDriverManager.chromedriver().setup();

	    ChromeOptions options = new ChromeOptions();

	    options.addArguments("--user-data-dir=C:\\HelpingHearts\\ChromeProfile");
	    options.addArguments("--disable-notifications");
	    //options.addArguments("--disable-popup-blocking");
	    //options.addArguments("--disable-infobars");
	    options.addArguments("--disable-extensions");

	    driver = new ChromeDriver(options);

	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

	    // Hide Chrome by moving it outside the screen
	    driver.manage().window().setSize(new Dimension(1400, 900));
	    driver.manage().window().setPosition(new Point(-3000, 0));

	    System.out.println("✅ Chrome Started (Hidden)");

	    return driver;
	}

	/**
	 * Opens WhatsApp chat using mobile number.
	 */
	private void openChat(String mobile, String message) throws Exception {

		String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);

		String url = "https://web.whatsapp.com/send?phone=91" + mobile + "&text=" + encoded;

		driver.get(url);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("footer")));

		Thread.sleep(2000);

	}

	/**
	 * Wait until chat window is completely loaded.
	 */

	private void sendTextMessage(File pdfFile) throws Exception {

		clickPlusButton();
		clickDocumentButton();
		uploadPdfUsingRobot(pdfFile);

		new Actions(driver).sendKeys(Keys.ENTER).perform();

		Thread.sleep(2000);

		System.out.println("Text Sent");

	}

	private void clickPlusButton() throws Exception {

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

		WebElement plusIcon = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span[data-testid='plus-rounded']")));

		System.out.println("Plus icon found");

		JavascriptExecutor js = (JavascriptExecutor) driver;

		// Highlight the icon
		js.executeScript("arguments[0].style.border='3px solid red';", plusIcon);

		// Click the clickable parent
		js.executeScript("arguments[0].parentElement.parentElement.click();", plusIcon);

		Thread.sleep(3000);

		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		Files.copy(screenshot.toPath(), Paths.get("C:/HelpingHearts/after-plus-click.png"),
				StandardCopyOption.REPLACE_EXISTING);

		System.out.println("Screenshot saved.");
	}

	private void clickDocumentButton() throws Exception {

		Thread.sleep(1000);

		new Actions(driver).sendKeys(Keys.ENTER).perform();

		System.out.println("Document Selected");

		Thread.sleep(1500);
	}

	private void uploadPdfUsingRobot(File pdfFile) throws Exception {

		Robot robot = new Robot();
		robot.setAutoDelay(200);

		// Copy only filename
		StringSelection ss = new StringSelection(pdfFile.getName());

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

		// Paste filename
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);

		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		Thread.sleep(100);

		// Open file
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);

		System.out.println("Selected : " + pdfFile.getName());

		Thread.sleep(4000);
	}

	

	

	/**
	 * Close browser.
	 */
	public synchronized void closeBrowser() {

		try {

			if (driver != null) {

				driver.quit();

			}

		} catch (Exception ignored) {
		}

		driver = null;
	}

	/**
	 * Check if browser is already running.
	 */
	public boolean isRunning() {

		try {

			if (driver == null) {
				return false;
			}

			driver.getTitle();

			return true;

		} catch (Exception ex) {

			return false;

		}

	}



}