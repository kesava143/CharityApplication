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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.WhatsAppContactEntity;

import io.github.bonigarcia.wdm.WebDriverManager;

@Service
public class WhatsAppService {

	@Autowired
	private ProgressService progressService;

	private WebDriver driver;

	/**
	 * Returns existing Chrome instance or creates a new one.
	 */

	private final List<String> successNumbers = new CopyOnWriteArrayList<>();
	private final List<String> failedNumbers = new CopyOnWriteArrayList<>();

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
	    options.addArguments("--disable-popup-blocking");
	    options.addArguments("--disable-infobars");
	    options.addArguments("--disable-extensions");

	    driver = new ChromeDriver(options);

	    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

	    // Hide Chrome by moving it outside the screen
	    driver.manage().window().setSize(new Dimension(1400, 900));
	    driver.manage().window().setPosition(new Point(-3000, 0));

	    System.out.println("✅ Chrome Started (Hidden)");

	    return driver;
	}

	public List<String> getSuccessNumbers() {
		return successNumbers;
	}

	public List<String> getFailedNumbers() {
		return failedNumbers;
	}

	/**
	 * Opens WhatsApp Web.
	 */
	public void openWhatsapp() {

		getDriver().get("https://web.whatsapp.com");

		System.out.println("Waiting for WhatsApp login...");

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		System.out.println("WhatsApp Ready");
	}

	/**
	 * Sends a single WhatsApp message.
	 */
	public boolean sendMessage(String mobile, String message) {

		try {

			long start = System.currentTimeMillis();

			System.out.println("-----------------------------------");
			System.out.println("Sending to : " + mobile);

			String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);

			String url = "https://web.whatsapp.com/send?phone=91" + mobile + "&text=" + encoded;

			driver.get(url);

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

			// Wait until the message input box is available
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@contenteditable='true']")));

			// Small delay to ensure chat is ready
			Thread.sleep(1000);

			// Send the message
			new Actions(driver).sendKeys(Keys.ENTER).perform();

			// Wait a little for the message to be sent
			Thread.sleep(1500);

			System.out.println("✅ Sent Successfully");
			System.out.println("Time Taken : " + (System.currentTimeMillis() - start) + " ms");

			return true;

		} catch (TimeoutException e) {

			System.out.println("❌ Chat did not load for : " + mobile);
			return false;

		} catch (Exception e) {

			System.out.println("❌ Failed : " + mobile);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sends to multiple numbers.
	 */
	public void sendBulkMessages(String[] mobiles, String message) {

		successNumbers.clear();
		failedNumbers.clear();

		for (String mobile : mobiles) {

			boolean sent = sendMessage(mobile, message);

			if (sent) {

				successNumbers.add(mobile);

				progressService.markSuccess();

				System.out.println("SUCCESS : " + mobile);

			} else {

				failedNumbers.add(mobile);

				progressService.markFailed();

				System.out.println("FAILED  : " + mobile);

			}

			try {
				// Wait before next message to reduce the risk of spam detection
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}

		}

		System.out.println("=================================");
		System.out.println("Bulk Sending Completed");
		System.out.println("=================================");
	}

	/**
	 * Close browser.
	 */
	public void closeBrowser() {

		if (driver != null) {

			driver.quit();

			driver = null;

			System.out.println("Chrome Closed");
		}

	}

	// public int[] sendBulkMessages(List<String> mobiles, String message) {
	public int[] sendBulkMessages(List<WhatsAppContactEntity> contacts, String message) {

		// Clear previous results
		successNumbers.clear();
		failedNumbers.clear();

		progressService.start(contacts.size());

		openWhatsapp();

		int success = 0;
		int failed = 0;

		for (WhatsAppContactEntity contact : contacts) {

			progressService.setCurrentNumber(contact.getMobile());

			String personalizedMessage = personalizeMessage(message, contact);

			boolean sent = sendMessage(contact.getMobile(), personalizedMessage);

			if (sent) {

				success++;

				successNumbers.add(contact.getMobile());

				progressService.markSuccess();

				System.out.println("SUCCESS : " + contact.getMobile());

			} else {

				failed++;

				failedNumbers.add(contact.getMobile());

				progressService.markFailed();

				System.out.println("FAILED : " + contact.getMobile());

			}

			try {

				Thread.sleep(3000);

			} catch (InterruptedException e) {

				Thread.currentThread().interrupt();

			}

		}

		try {

			Thread.sleep(3000);

		} catch (InterruptedException e) {

			Thread.currentThread().interrupt();

		}

		return new int[] { success, failed };
	}

	private String personalizeMessage(String template, WhatsAppContactEntity contact) {

		String message = template;

		message = message.replace("{NAME}", contact.getName() == null ? "Sir / Madam" : contact.getName());

		message = message.replace("{PHONE}", contact.getMobile());

		return message;
	}

	public boolean sendMessageWithAttachment(String mobile, String message, File pdfFile) {

		try {

			driver = getDriver();

			if (!pdfFile.exists()) {
				System.out.println("PDF Not Found");
				return false;
			}

			long start = System.currentTimeMillis();

			openChat(mobile, message);

//			sendTextMessage();
			sendTextMessage(pdfFile);

//			clickPlusButton();
//
//			clickDocumentButton();

//			uploadPdf(pdfFile);
//			uploadPdfUsingRobot(pdfFile);

//			sendAttachment();
//
//			System.out.println("Certificate Sent Successfully");
			
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

	private void openChat(String mobile, String message) throws Exception {

		String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);

		String url = "https://web.whatsapp.com/send?phone=91" + mobile + "&text=" + encoded;

		driver.get(url);

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("footer")));

		Thread.sleep(2000);

	}

	private void sendTextMessage(File pdfFile) throws Exception {
		
		clickPlusButton();
		clickDocumentButton();
		uploadPdfUsingRobot(pdfFile);

		new Actions(driver).sendKeys(Keys.ENTER).perform();

		Thread.sleep(2000);

		System.out.println("Text Sent");

	}

	private void clickPlusButton() throws Exception {

	    WebDriverWait wait =
	            new WebDriverWait(driver, Duration.ofSeconds(20));

	    WebElement plusIcon = wait.until(
	            ExpectedConditions.visibilityOfElementLocated(
	                    By.cssSelector("span[data-testid='plus-rounded']")));

	    System.out.println("Plus icon found");

	    JavascriptExecutor js = (JavascriptExecutor) driver;

	    // Highlight the icon
	    js.executeScript(
	        "arguments[0].style.border='3px solid red';", plusIcon);

	    // Click the clickable parent
	    js.executeScript(
	        "arguments[0].parentElement.parentElement.click();",
	        plusIcon);

	    Thread.sleep(3000);

	    File screenshot =
	            ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

	    Files.copy(
	            screenshot.toPath(),
	            Paths.get("C:/HelpingHearts/after-plus-click.png"),
	            StandardCopyOption.REPLACE_EXISTING);

	    System.out.println("Screenshot saved.");
	}

	private void clickDocumentButton() throws Exception {

	    Thread.sleep(1000);

	    new Actions(driver)
	            .sendKeys(Keys.ENTER)
	            .perform();

	    System.out.println("Document Selected");

	    Thread.sleep(1500);
	}

	private void uploadPdf(File pdfFile) throws Exception {

	    List<WebElement> inputs =
	            driver.findElements(By.cssSelector("input[type='file']"));

	    System.out.println("Total file inputs : " + inputs.size());

	    if (inputs.isEmpty()) {

	        throw new RuntimeException("No file input found.");

	    }

	    WebElement input = inputs.get(0);

	    System.out.println("accept : " + input.getAttribute("accept"));

	    if (!pdfFile.exists()) {

	        throw new RuntimeException("PDF does not exist.");

	    }

	    input.sendKeys(pdfFile.getAbsolutePath());

	    System.out.println("Upload request sent.");

	    Thread.sleep(5000);
	}
	
	private void uploadPdfUsingRobot(File pdfFile) throws Exception {

	    Robot robot = new Robot();
	    robot.setAutoDelay(200);


	    // Copy only filename
	    StringSelection ss =
	            new StringSelection(pdfFile.getName());

	    Toolkit.getDefaultToolkit()
	            .getSystemClipboard()
	            .setContents(ss, null);

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

	private void sendAttachment() throws Exception {

		new Actions(driver).sendKeys(Keys.ENTER).perform();

		Thread.sleep(3000);

		System.out.println("Attachment Sent");

	}

}