package com.echostar.gopher.selenium;

import com.echostar.gopher.testng.ErrorUtil;
import com.echostar.gopher.util.Config;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the Selenium web driver functions.
 * 
 * @author Shekhar Bhardwaj
 * 
 */
public class GopherDriverImpl implements GopherDriver {

    RemoteWebDriver driver = null;

    private static Map<String, String> elementIdentifierMap;

	private Logger log;

	static {
		elementIdentifierMap = new HashMap<>();
		elementIdentifierMap.put("byName", "NAME"); //$NON-NLS-1$ //$NON-NLS-2$
		elementIdentifierMap.put("byID", "ID"); //$NON-NLS-1$ //$NON-NLS-2$
		elementIdentifierMap.put("byXpath", "XPATH"); //$NON-NLS-1$ //$NON-NLS-2$
		elementIdentifierMap.put("byLinktext", "LINK"); //$NON-NLS-1$ //$NON-NLS-2$
		elementIdentifierMap.put("byPLinktext", "PLINK"); //$NON-NLS-1$ //$NON-NLS-2$
		elementIdentifierMap.put("byClassname", "CLASSNAME"); //$NON-NLS-1$ //$NON-NLS-2$
		elementIdentifierMap.put("byTagname", "TAGNAME"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * TBD
	 * 
	 * @param browserName		'firefox', 'chrome', 'iexplore' or 'safari'
	 * @param OSName			'WINDOWS' or 'WIN8'
	 * @param nodeIP			hub ip
	 * @param nodePort			hub port
	 * @return					the WebDriver or null
	 * @author shekhar.bhardwaj
	 */
	public GopherDriverImpl (String browserName, String OSName,
		String nodeIP, String nodePort) throws Exception {
		this (browserName, OSName, nodeIP, nodePort, true);
	}

	/**
	 * TBD
	 * 
	 * @param browserName		'firefox', 'chrome', 'iexplore' or 'safari'
	 * @param OSName			'WINDOWS' or 'WIN8'
	 * @param nodeIP			hub ip
	 * @param nodePort			hub port
	 * @param ieUseNativeEvents	if the browser is 'ie' use native events or not
	 * @return					the WebDriver or null
	 * @author shekhar.bhardwaj
	 */
	public GopherDriverImpl (String browserName, String OSName,
		String nodeIP, String nodePort, boolean ieUseNativeEvents) throws Exception {

	    Assert.assertNotNull(browserName, "The argument String browserName is null."); //$NON-NLS-1$
        Assert.assertNotNull(OSName, "The argument String OSName is null."); //$NON-NLS-1$
        Assert.assertNotNull(nodePort, "The argument String nodePort is null."); //$NON-NLS-1$
        Assert.assertNotNull(ieUseNativeEvents, "The argument String ieUseNativeEvents is null."); //$NON-NLS-1$

	    RemoteWebDriver driver = null;
		DesiredCapabilities cap = null;

        log = Logger.getLogger (getClass().getName());

        try {
			log.info("Initializing browser capability."); //$NON-NLS-1$
			if (browserName.equals("firefox") && OSName.equals("MAC")) { //$NON-NLS-1$ //$NON-NLS-2$
				cap = DesiredCapabilities.firefox();
				cap.setBrowserName("firefox"); //$NON-NLS-1$
				cap.setPlatform(Platform.MAC);
			} else if (browserName.equals("chrome") && OSName.equals("MAC")) { //$NON-NLS-1$ //$NON-NLS-2$
				cap = DesiredCapabilities.chrome();
				cap.setBrowserName("chrome"); //$NON-NLS-1$
				cap.setPlatform(Platform.MAC);
			} else if (browserName.equals("chrome") //$NON-NLS-1$
					&& OSName.endsWith("WINDOWS")) { //$NON-NLS-1$
				cap = DesiredCapabilities.chrome();
				cap.setBrowserName("chrome"); //$NON-NLS-1$
				cap.setPlatform(Platform.WINDOWS);
			} else if (browserName.equals("iexplore")) { //$NON-NLS-1$
				cap = DesiredCapabilities.internetExplorer();
				cap.setBrowserName("iexplore"); //$NON-NLS-1$
				cap.setPlatform(Platform.WINDOWS);
				cap.setCapability("nativeEvents", ieUseNativeEvents); //$NON-NLS-1$
			} else if (browserName.equals("firefox") //$NON-NLS-1$
					&& OSName.equals("WINDOWS")) { //$NON-NLS-1$
				cap = DesiredCapabilities.firefox();
				cap.setBrowserName("firefox"); //$NON-NLS-1$
				cap.setPlatform(Platform.WINDOWS);
			} else if (browserName.equals("safari")) { //$NON-NLS-1$
				cap = DesiredCapabilities.safari();
				cap.setBrowserName("safari"); //$NON-NLS-1$
				cap.setPlatform(Platform.MAC);
			}

			if (browserName.equals("firefox") && OSName.equals("WIN8")) { //$NON-NLS-1$ //$NON-NLS-2$
				cap = DesiredCapabilities.firefox();
				cap.setBrowserName("firefox"); //$NON-NLS-1$
				cap.setPlatform(Platform.WIN8);
			} else if (browserName.equals("chrome") && OSName.equals("WIN8")) { //$NON-NLS-1$ //$NON-NLS-2$
				cap = DesiredCapabilities.firefox();
				cap.setBrowserName("chrome"); //$NON-NLS-1$
				cap.setPlatform(Platform.WIN8);
			} else if (browserName.equals("iexplore") && OSName.equals("WIN8")) { //$NON-NLS-1$ //$NON-NLS-2$
				cap = DesiredCapabilities.firefox();
				cap.setBrowserName("iexplore"); //$NON-NLS-1$
				cap.setPlatform(Platform.WIN8);
			}
			float node_port = Float.parseFloat(nodePort);
			int nodePort_ = (int) (node_port);
			driver = new RemoteWebDriver(new URL("http://" + nodeIP + ":"+ nodePort_ + "/wd/hub"), cap); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			this.driver=driver;
			log.info("Browser Name and Version : " + browserName+" "+getBrowserVersion(browserName)); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e){

            log.error("Something went wrong with browser initialization: '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
            log.error("Please check following before re-run:"); //$NON-NLS-1$
			log.error("1: Browser availability on node having platform \""+OSName+"\"."); //$NON-NLS-1$ //$NON-NLS-2$
			log.error("2: Node initialization with GRID."); //$NON-NLS-1$
			log.error("3: Node availability on network."); //$NON-NLS-1$
			log.error("4: Node IP and Port are free and correct."); //$NON-NLS-1$
			log.error("5: Browser version \""+browserName+"\"is compatible."); //$NON-NLS-1$ //$NON-NLS-2$

			if (driver != null ) {
		        driver.close();
		    }
            throw e;
		}

		try
		{
			Set<String> browserWindows = driver.getWindowHandles();
			if (browserWindows.size() != 1)
			{
				throw new Exception ("driver.getWindowHandles() !=1."); //$NON-NLS-1$
			}
		} catch (Exception e) {

            log.error("RemoteDriver#getWindowHandles() threw an exception: '"+ //$NON-NLS-1$
				e.getMessage()+"'."); //$NON-NLS-1$
			log.error("Browser is not opened."); //$NON-NLS-1$
            if (driver != null ) {
                driver.close();
            }
            throw e;
		}
	}

	/**
	 * This method will be used to make the script wait until page loads.
	 * 
	 * @param selenium
	 *            tbd
	 * @param timeout
	 *            time to wait for page load
	 * @throws Exception
	 *             on error
	 *             @author shekhar.bhardwaj
	 */
	/*
	public void waitForPageLoad(Selenium selenium, int timeout)
			throws Exception {
		long stTime = System.currentTimeMillis();
		try {
			selenium.waitForPageToLoad(Integer.toString(timeout));
			log.info("Waiting for Page to Load");
		} catch (Exception e) {
		}
		long eTime = System.currentTimeMillis();
		log.info("Page Load Time: " + (eTime - stTime));
	}
    */

	/**
	 * This method will be used to pause the script until the given element to
	 * be displayed.
	 * 
	 * @param maxTries     the maximum number of waits, each duration
	 * 						sleep("Sleep.GopherDriver.waitForElementToBeVisible");
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void waitForElementToBeVisible(int maxTries, String[] locator) throws Exception {

        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        try {
	        String elementName = locator[1];
	        String elementIdentifier = locator[0];
	        boolean flag = false;
	        int count = 0;
	        while (flag == false) {
	            sleep("Sleep.GopherDriver.waitForElementToBeVisible"); //$NON-NLS-1$
	            try {
	                // WebElement element = getWebElement(driver,null, locator);
	                if (isDisplayed(null, locator) == false) {
	                    // while(!driver.findElement(By.id(elementID)).isDisplayed())
	                    log.debug("Waiting for an element to be displayed"); //$NON-NLS-1$
	                    count = count + 1;
	                    if (count == maxTries) {
	                        log.info("Element with " //$NON-NLS-1$
								+ elementIdentifier + " " + elementName //$NON-NLS-1$
								+ " is not displayed, timeout"); //$NON-NLS-1$
	                        break;
	                    }
	                } else {
	                    flag = true;
	                }
	            } catch (Exception e) {
	                // Add the error to the list of errors.
	                ErrorUtil.addVerificationFailure(e);

	                log.error("Exception waiting for element to be visible: '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
	                count = count + 1;
	                if (count == maxTries) {
	                    log.error("Element with " //$NON-NLS-1$
							+ elementIdentifier + " " + elementName //$NON-NLS-1$
							+ " is not displayed, max tries exceeded."); //$NON-NLS-1$
	                    break;
	                }
	                
	                throw e;
	            }
	        }
	    } catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error ("Exception trying to wait for element to become visible : '"+ //$NON-NLS-1$
	                e.getMessage()+"'."); //$NON-NLS-1$
	        throw e;
	    }
	}

	/**
	 * This method will be used to display the specified message when script in
	 * pause state.
	 * 
	 * @param maxTries	the maximum number of tries, each of duration
	 * 					sleep("Sleep.GopherDriver.waitForAuction")
	 * @param messageToBeDisplayed
	 *            the message to be written to stdout on timeout
	 * @throws Exception
	 *             on error
	 *             @author shekhar.bhardwaj
	 */
	public void waitForAuction(int maxTries, String messageToBeDisplayed) throws Exception {

        Assert.assertNotNull(messageToBeDisplayed, "The argument String messageToBeDisplayed is null."); //$NON-NLS-1$

        try {
	        int count = 0;
	        while (true) {
	            sleep("Sleep.GopherDriver.waitForAuction"); //$NON-NLS-1$
	            count = count + 1;
	            log.debug("" + messageToBeDisplayed); //$NON-NLS-1$
	            if (count == maxTries) {
	                break;
	            }
	        }
	    } catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to wait for auction : '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
	        throw e;
	    }
	}

	/**
	 * This method will be used to capture the screen shot of an application.
	 * 
	 * @param picture_name     name for image
	 * @throws Exception       on error
	 * @author shekhar.bhardwaj
	 */
	public void selfie(String picture_name) throws Exception {

        Assert.assertNotNull(picture_name, "The argument String picture_name is null."); //$NON-NLS-1$

        try {
			// WebDriver augmentedDriver = new Augmenter().augment(driver);
			// File source =
			// ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);

			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			log.info("Capturing the screen shot..."); //$NON-NLS-1$
			// TBD - define property 'screenshotPath'.
			FileUtils.copyFile(scrFile,
					new File(Config.getProperty_S("screenshotPath").trim() //$NON-NLS-1$
							+ picture_name + ".jpg")); //$NON-NLS-1$
		} catch (Exception e) {

            log.error("Exception trying to capture screenshot: '"+ e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
			throw e;
		}
	}

	/**
	 * Close and quit the browser.
	 * 
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void stopDriver() throws Exception {
		if (driver == null) {
			return;
		}
        sleep("Sleep.GopherDriver.stopDriver"); //$NON-NLS-1$
		log.info("Closing the browser"); //$NON-NLS-1$
		try {
		    driver.close();
			driver.quit();

		} catch (Exception e) {

            log.error("Exception while closing the browser: '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
			throw e;
		}
	}

	/**
	 * This method will be used to check/uncheck the given check box.
	 * 
	 * @param parent
	 *            tbd
	 * @param locator
	 *            array returned by {@link #getLocator getLocator}
	 * @param checkFlag
	 *            checked or not
	 * @throws Exception
	 *             on error
	 *             
	 *             @author shekhar.bhardwaj
	 */
	public void goClickCheckbox(WebElement parent,
		String[] locator, boolean checkFlag) throws Exception {

	    //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
	    Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        try {
	        String elementName = (locator[1]).trim();
	        // String elementIdentifier=(locator[0]).trim();
			WebElement checkbox = getWebElement(parent, locator);
			if (checkbox != null && isEnabled(parent, locator) == true) {
				if (checkFlag == true) {
					if (!checkbox.isSelected()) {
						checkbox.click();
						log.info("The Check box with name " //$NON-NLS-1$
										+ elementName + " is checked."); //$NON-NLS-1$
					}
				} else {
					if (checkbox.isSelected()) {
						checkbox.click();
						log.info("The Check box with name " //$NON-NLS-1$
										+ elementName + " is unchecked."); //$NON-NLS-1$
					}
				}
			} else {
				log.error("Checkbox is not found."); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception while trying to check/uncheck checkbox: '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
			throw e;
		}
	}

	/**
	 * Click on the given UI element i.e button/text/...
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goClickUIElement(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String elementName = null;
		String elementIdentifier = null;

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (isEnabled(parent, locator) == true) {
				uiElement.click();
				log.info("The UI element with name " //$NON-NLS-1$
						+ elementName + " is clicked"); //$NON-NLS-1$
			} else {
				log.error("The UI element with name " //$NON-NLS-1$
						+ elementName + " is not found"); //$NON-NLS-1$
				// throw new
				// uiElementException("The ui element with name "+elementName+" is not found");
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception while trying to click on " //$NON-NLS-1$
				+ elementName+ ", elementIdentifier='"+ elementIdentifier+"' :'"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
	}

	/**
	 * This method will be used to enter the text in the given text area.
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @param text     text to be entered
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goSendText(WebElement parent, String[] locator, String text) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$
        Assert.assertNotNull(text, "The argument String text is null."); //$NON-NLS-1$

        String elementName = null;
		String elementIdentifier = null;
		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (isEnabled(parent, locator) == true) {
				uiElement.clear();
				uiElement.sendKeys(text);
				log.info("The text " + text //$NON-NLS-1$
						+ " for ui element with name " + elementName //$NON-NLS-1$
						+ " is entered"); //$NON-NLS-1$
			} else {
				log.info("The ui element with name " //$NON-NLS-1$
						+ elementName + " is not found"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception while trying to enter text in '" //$NON-NLS-1$
				+ elementName+ "', elementIdentifier='"+ elementIdentifier+"' :'"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
	}

	/**
	 * Select/click the given link.
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goClickOnLink(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String elementName = null;
		try {
	        elementName = (locator[1]).trim();
	        // String elementIdentifier=(locator[0]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (isEnabled(parent, locator) == true) {
				uiElement.click();
				log.info("The ui element with name " //$NON-NLS-1$
						+ elementName + " is clicked"); //$NON-NLS-1$
			} else {
				log.info("The ui element with name " //$NON-NLS-1$
						+ elementName + " is not found"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception while trying to click on '" //$NON-NLS-1$
				+ elementName + "' elementIdentifier LINK : '"+ //$NON-NLS-1$
			    e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
	}

	/**
	 * This method will be used to select/click the given partial link.
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goClickOnPartialLink(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String elementName = null;
		try {
	        elementName = (locator[1]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (isEnabled(parent, locator) == true) {
				uiElement.click();
				log.info("The ui element with name " //$NON-NLS-1$
						+ elementName + " is clicked"); //$NON-NLS-1$
			} else {
				log.error("The ui element with name " //$NON-NLS-1$
						+ elementName + " is not found"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception while trying to click on '" //$NON-NLS-1$
				+ elementName + "' elementIdentifier PLINK : '"+ //$NON-NLS-1$
			    e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
	}

	/**
	 * Find the WebElement based on the given UI element
	 * identifier i.e. ID/Xpath/name.
	 * 
	 * @param parent       tbd
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @return             the web element or null
     * @throws Exception    on error
     * @author shekhar.bhardwaj
	 */
	public WebElement getWebElement(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        WebElement uiElement = null;
		String elementName = null;
		String elementIdentifier = null;

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();

	        if ((elementIdentifierMap.get("byName")).equals(elementIdentifier)) { //$NON-NLS-1$
				if (parent == null) {
					uiElement = driver.findElement(By.name(elementName));
				} else {
					uiElement = parent.findElement(By.name(elementName));
				}
			} else if (elementIdentifierMap.get("byID").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElement = driver.findElement(By.id(elementName));
				} else {
					uiElement = parent.findElement(By.id(elementName));
				}
			} else if (elementIdentifierMap.get("byXpath").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElement = driver.findElement(By.xpath(elementName));
				} else {
					uiElement = parent.findElement(By.xpath(elementName));
				}
			} else if (elementIdentifierMap.get("byLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElement = driver.findElement(By.linkText(elementName));
				} else {
					uiElement = parent.findElement(By.linkText(elementName));
				}
			} else if (elementIdentifierMap.get("byPLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElement = driver.findElement(By
							.partialLinkText(elementName));
				} else {
					uiElement = parent.findElement(By
							.partialLinkText(elementName));
				}
			} else if (elementIdentifierMap.get("byClassname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					// uiElement =
					// driver.findElement(By.className(elementName));
					uiElement = driver.findElement(By.xpath(elementName));
				} else {
					uiElement = parent.findElement(By.xpath(elementName));
				}
			} else if (elementIdentifierMap.get("byTagname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElement = driver.findElement(By.tagName(elementName));
				} else {
					uiElement = parent.findElement(By.tagName(elementName));
				}
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to find the given element '" //$NON-NLS-1$
				+ elementName + "' Identifier='" + elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
            
            throw e;
		}

		return uiElement;

	}

	/**
	 * Get the WebElements array based on the given UI element
	 * identifier i.e. ID/Xpath/name.
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @return         the list of web elements or null
     * @throws Exception   on error
     * @author shekhar.bhardwaj
	 */
	public List<WebElement> getWebElements(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String elementName = null;
		String elementIdentifier = null;
		List<WebElement> uiElements = null;

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();
	        uiElements = new ArrayList<WebElement>();

	        if ((elementIdentifierMap.get("byName")).equals(elementIdentifier)) { //$NON-NLS-1$
				if (parent == null) {
					uiElements = driver.findElements(By.name(elementName));
				} else {
					uiElements = parent.findElements(By.name(elementName));
				}
			} else if (elementIdentifierMap.get("byID").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElements = driver.findElements(By.id(elementName));
				} else {
					uiElements = parent.findElements(By.id(elementName));
				}
			} else if (elementIdentifierMap.get("byXpath").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElements = driver.findElements(By.xpath(elementName));
				} else {
					uiElements = parent.findElements(By.xpath(elementName));
				}
			} else if (elementIdentifierMap.get("byLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElements = driver.findElements(By.linkText(elementName));
				} else {
					uiElements = parent.findElements(By.linkText(elementName));
				}
			} else if (elementIdentifierMap.get("byPLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElements = driver.findElements(By
							.partialLinkText(elementName));
				} else {
					uiElements = parent.findElements(By
							.partialLinkText(elementName));
				}
			} else if (elementIdentifierMap.get("byClassname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElements = driver.findElements(By.xpath(elementName));
				} else {
					uiElements = parent.findElements(By.xpath(elementName));
				}
			} else if (elementIdentifierMap.get("byTagname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiElements = driver.findElements(By.tagName(elementName));
				} else {
					uiElements = parent.findElements(By.tagName(elementName));
				}
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to find the given element '" //$NON-NLS-1$
				+ elementName + "' Identifier='" + elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
            
            throw e;
		}
		return uiElements;
	}

	/**
	 * This method returns true/false based on the UI element diaplyed status.
	 * 
	 * @param parent       tbd
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @return             true (displayed) or false
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public boolean isDisplayed(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String elementName = null;
		String elementIdentifier = null;
		boolean flag = false;
		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (uiElement.isDisplayed()) {
				flag = true;
				log.info("The UI element " + elementName //$NON-NLS-1$
						+ " is displayed"); //$NON-NLS-1$
			} else {
				log.error("The UI element " + elementName //$NON-NLS-1$
						+ " is not displayed"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to find the given element '" //$NON-NLS-1$
				+ elementName + "' Identifier='" + elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
		return flag;
	}

	/**
	 * Is the UI element enabled?
	 * 
	 * @param parent       tbd
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @return             true (Enabled) or false
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public boolean isEnabled(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String elementName = null;
		String elementIdentifier = null;
		boolean flag = false;

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (uiElement.isEnabled()) {
				flag = true;
				log.info("The UI element " + elementName //$NON-NLS-1$
						+ " is enabled"); //$NON-NLS-1$
			} else {
				log.error("The UI element " + elementName //$NON-NLS-1$
						+ " is not enabled"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to find the given element '" //$NON-NLS-1$
				+ elementName + "' Identifier='" + elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'"); //$NON-NLS-1$
			throw e;
		}
		return flag;
	}

	/**
	 * This method used to verify the text in the webpage.
	 * 
	 * @param textToBeVerified the text to be verified
	 * @return                 true (present) or false
     * @throws Exception        on error
     * @author shekhar.bhardwaj
	 */
	public boolean isTextPresent(String textToBeVerified) throws Exception {
 
	    Assert.assertNotNull(textToBeVerified, "The argument String textToBeVerified is null."); //$NON-NLS-1$

        boolean result = false;
		try {
			if (driver.findElement(By.xpath("//*[contains(.,'" //$NON-NLS-1$
					+ textToBeVerified + "')]")) != null) { //$NON-NLS-1$
				log.info("The text " + textToBeVerified //$NON-NLS-1$
						+ " is present on the page"); //$NON-NLS-1$
				result=true;
			} else {
				log.info("The text " + textToBeVerified //$NON-NLS-1$
						+ " is not present on the page"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception while searching for the text '" //$NON-NLS-1$
				+ textToBeVerified + "'  on the page : '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
			throw e;
		}
		return result;
	}

	/**
	 * Perform mouse over event on the given UI element.
	 * 
	 * @param parent       tbd
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goPerformMouseOver(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String elementName = null;
		String elementIdentifier = null;

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (isDisplayed(parent, locator) == true) {
				uiElement.sendKeys(Keys.TAB);
				Actions action = new Actions(driver);
				action.moveToElement(uiElement).perform();
	            sleep("Sleep.GopherDriver.goPerformMouseOver"); //$NON-NLS-1$
				log.info("The UI element with name " //$NON-NLS-1$
						+ elementName + " is mouse over"); //$NON-NLS-1$
			} else {
				log.info("The UI element with name " //$NON-NLS-1$
						+ elementName + " is not found"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception while trying to click on '" //$NON-NLS-1$
				+ elementName + "' elementIdentifier='"+ elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
	}

	/**
	 * This method is used to perform mouse over event on the given UI element.
	 * 
	 * @param element  the web element to be moused over
	 * @author shekhar.bhardwaj
	 */
	public void goBuildMouseOverJavascriptExecutor(WebElement element) throws Exception {

        Assert.assertNotNull(element, "The argument WebElement element is null."); //$NON-NLS-1$

        try {
			String mouseOverjavascript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover', true,  false); " //$NON-NLS-1$
					+ "arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}"; //$NON-NLS-1$
			JavascriptExecutor jse = driver;
			jse.executeScript(mouseOverjavascript, element);
            sleep("Sleep.GopherDriver.goBuildMouseOverJavascriptExecutor"); //$NON-NLS-1$
			log.info("The UI element with name " //$NON-NLS-1$
					+ element + " is mouse over"); //$NON-NLS-1$
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Error while doing mouse over on element : '" //$NON-NLS-1$
				+ e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
	}

	/**
	 * Select the given item from the drop down list.
	 * 
	 * @param parent               tbd
	 * @param locator              array returned by {@link #getLocator getLocator}
	 * @param itemTobe_selected    the item to be selected
	 * @throws Exception           on error
	 * @author shekhar.bhardwaj
	 */
	public void goSelectDropdown(WebElement parent,
		String[] locator, String itemTobe_selected) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$
        Assert.assertNotNull(itemTobe_selected, "The argument String itemTobe_selected is null."); //$NON-NLS-1$

        String elementName = null;
		String elementIdentifier = null;

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (isDisplayed(parent, locator) == true) {
				Select dropdown = new Select(uiElement);
				//dropdown.deselectAll();
				dropdown.selectByVisibleText(itemTobe_selected);
	            sleep("Sleep.GopherDriver.goSelectDropdown"); //$NON-NLS-1$
				log.info("The item " + itemTobe_selected //$NON-NLS-1$
						+ "in UI element with name " + elementName //$NON-NLS-1$
						+ " is selected"); //$NON-NLS-1$
			} else {
				log.error("The UI element with name " //$NON-NLS-1$
						+ elementName + " and item " + itemTobe_selected //$NON-NLS-1$
						+ " is not found"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to select dropdown '" //$NON-NLS-1$
				+ itemTobe_selected + "' on '" + elementName //$NON-NLS-1$
				+ "' elementIdentifier='" + elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
				e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
	}

	/**
	 * Get the requested url , ie http://www.dishanywhere.com
	 * 
	 * @param url			the URL
	 * @param browserName	the browser name
	 * @throws Exception	on any error
	 * @author shekhar.bhardwaj
	 */
	public void navigate2testsite(String url, String browserName) throws Exception {

        Assert.assertNotNull(url, "The argument String url is null."); //$NON-NLS-1$
        Assert.assertNotNull(browserName, "The argument String browserName is null."); //$NON-NLS-1$

        try {
            sleep("Sleep.GopherDriver.navigate2testsite_1"); //$NON-NLS-1$
	        Dimension targetSize;
	        if (!(browserName.equals("safari"))) { //$NON-NLS-1$
	            // Added the following code to maximize the browser window
	            Point targetPosition = new Point(0, 0);
	            driver.manage().window().setPosition(targetPosition);
	            targetSize = new Dimension(1920, 1080);
	            driver.manage().window().setSize(targetSize);
	        }
	        driver.manage().window().maximize();

	        driver.get(url);
	        // driver.manage().window().maximize(); This works only for firefox so
	        // added the above code

	        String currentURL = goGetCurrentURL();
	        log.info(""+currentURL); //$NON-NLS-1$


	        // To handle the qa.dishanywhere.com's maintenance pop up
	        if (!browserName.equals("safari")) { //$NON-NLS-1$
	            log.info("Implicitly waiting for :" + browserName); //$NON-NLS-1$
	            log.info("*************************************************************************"); //$NON-NLS-1$
	            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	        } else {
	            log.info("Thread.sleep for :" + browserName); //$NON-NLS-1$
	            log.info("*************************************************************************"); //$NON-NLS-1$
	            sleep("Sleep.GopherDriver.navigate2testsite_2"); //$NON-NLS-1$
	        }
	        //DAnyUtil.qaDANYpopUP(driver, url);
	        int testSiteStatusCode = getHTTPResponseStatusCode(url);
	        log.info("HTTP Status code for "+ url +": " + testSiteStatusCode); //$NON-NLS-1$ //$NON-NLS-2$
	        statusSwitch(testSiteStatusCode);
	        log.info("*************************************************************************"); //$NON-NLS-1$
	    } catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to navigate to test site : '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
	        throw e;
	    }
	}

	/***
	 * This Method gets the text in string format from GUI
	 * 
	 * @param parent		the parent
	 * @param locator		the locator
	 * @return				web element text or null
     * @throws Exception   on error
     * @author shekhar.bhardwaj
	 */

	public String goGetText(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String uiText = null;
		String elementName = (locator[1]).trim();
		String elementIdentifier = (locator[0]).trim();

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();

	        if ((elementIdentifierMap.get("byName")).equals(elementIdentifier)) { //$NON-NLS-1$
				if (parent == null) {
					uiText = driver.findElement(By.name(elementName)).getText();
				} else {
					uiText = parent.findElement(By.name(elementName)).getText();
				}
			} else if (elementIdentifierMap.get("byID").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.id(elementName)).getText();
				} else {
					uiText = parent.findElement(By.id(elementName)).getText();
				}
			} else if (elementIdentifierMap.get("byXpath").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.xpath(elementName))
							.getText();
				} else {
					uiText = parent.findElement(By.xpath(elementName))
							.getText();
				}
			} else if (elementIdentifierMap.get("byLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.linkText(elementName))
							.getText();
				} else {
					uiText = parent.findElement(By.linkText(elementName))
							.getText();
				}
			} else if (elementIdentifierMap.get("byPLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver
							.findElement(By.partialLinkText(elementName))
							.getText();
				} else {
					uiText = parent
							.findElement(By.partialLinkText(elementName))
							.getText();
				}
			} else if (elementIdentifierMap.get("byClassname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.className(elementName))
							.getText();
				} else {
					uiText = parent.findElement(By.className(elementName))
							.getText();
				}
			} else if (elementIdentifierMap.get("byTagname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.tagName(elementName))
							.getText();
				} else {
					uiText = parent.findElement(By.tagName(elementName))
							.getText();
				}
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to find the element '" //$NON-NLS-1$
				+ elementName + "' Identifier='" + elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
            
            throw e;
		}

		return uiText;
	}

	/***
	 * This function compares actual and expected values
	 * 
	 * @param actual_text		the text we found
	 * @param expected_text		the text we expected
	 * @return boolean			true if equal
     * @throws Exception        on any error
     * @author shekhar.bhardwaj
	 */
	public boolean goCompare(String expected_text, String actual_text) {

        Assert.assertNotNull(expected_text, "The argument String expected_text is null."); //$NON-NLS-1$
        Assert.assertNotNull(actual_text, "The argument String actual_text is null."); //$NON-NLS-1$

        String expected_textTrimmed = null;
		String actual_textTrimmed = null;
		boolean equal = false;

		try {
	        expected_textTrimmed = expected_text.trim();
	        actual_textTrimmed = actual_text.trim();
			log.info("Comparing, expected '" + expected_text + "' & actual '"+ actual_text+"'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			Assert.assertEquals(actual_textTrimmed, expected_textTrimmed);
			equal=true;
			log.info("Comparision is "+equal+"."); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Throwable t) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(t);

            log.error("Comparison failed. Expected '" + expected_text+ "' but found '" + actual_text+"'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		return equal;
	}

	/***
	 * !! STOP !! Under construction kindly use driver to directly send keys
	 * 
	 * @param parent		the WebElement parent
	 * @param key			the keys to send
	 * @param locator		the locator
	 * @throws Exception	on any error
	 * @author shekhar.bhardwaj
	 */
	public void goSendKeys(WebElement parent, String key, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(key, "The argument String key is null."); //$NON-NLS-1$
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String elementName = null;
		String elementIdentifier = null;

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();

	        if ((elementIdentifierMap.get("byName")).equals(elementIdentifier)) { //$NON-NLS-1$
				if (parent == null) {
					driver.findElement(By.name(elementName)).sendKeys(key);
				} else {
					parent.findElement(By.tagName(elementName)).sendKeys(key);
				}
			} else if (elementIdentifierMap.get("byID").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					driver.findElement(By.id(elementName)).sendKeys(key);
				} else {
					parent.findElement(By.tagName(elementName)).sendKeys(key);
				}
			} else if (elementIdentifierMap.get("byXpath").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					driver.findElement(By.xpath(elementName)).sendKeys(key);
				} else {
					parent.findElement(By.tagName(elementName)).sendKeys(key);
				}
			} else if (elementIdentifierMap.get("byLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					driver.findElement(By.linkText(elementName)).sendKeys(key);
				} else {
					parent.findElement(By.tagName(elementName)).sendKeys(key);
				}
			} else if (elementIdentifierMap.get("byPLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					driver.findElement(By.partialLinkText(elementName)).sendKeys(key);
				} else {
					parent.findElement(By.tagName(elementName)).sendKeys(key);
				}
			} else if (elementIdentifierMap.get("byClassname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					driver.findElement(By.className(elementName)).sendKeys(key);
				} else {
					parent.findElement(By.tagName(elementName)).sendKeys(key);
				}
			} else if (elementIdentifierMap.get("byTagname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					driver.findElement(By.tagName(elementName)).sendKeys(key);
				} else {
					parent.findElement(By.tagName(elementName)).sendKeys(key);
				}
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to find the given element '" //$NON-NLS-1$
				+ elementName + "' Identifier='" + elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}

	}

	/**
	 * !! STOP !! Under construction kindly use driver to directly get the
	 * attributes
	 * 
	 * @param parent		the parent WebElement
	 * @param attribute		the attribute name
	 * @param locator		the locator
	 * @return				the attribute value
     * @throws Exception   on error
     * @author shekhar.bhardwaj
	 */
	public String goGetAttribute(WebElement parent, String attribute, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(attribute, "The argument String attribute is null."); //$NON-NLS-1$
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$

        String uiText = null;
		String elementName = null;
		String elementIdentifier = null;

		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();

	        if ((elementIdentifierMap.get("byName")).equals(elementIdentifier)) { //$NON-NLS-1$
				if (parent == null) {
					uiText = driver.findElement(By.name(elementName))
							.getAttribute(attribute);
				} else {
					uiText = parent.findElement(By.name(elementName))
							.getAttribute(attribute);
				}
			} else if (elementIdentifierMap.get("byID").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.name(elementName))
							.getAttribute(attribute);
				} else {
					uiText = parent.findElement(By.name(elementName))
							.getAttribute(attribute);
				}
			} else if (elementIdentifierMap.get("byXpath").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.name(elementName))
							.getAttribute(attribute);
				} else {
					uiText = parent.findElement(By.name(elementName))
							.getAttribute(attribute);
				}
			} else if (elementIdentifierMap.get("byLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.name(elementName))
							.getAttribute(attribute);
				} else {
					uiText = parent.findElement(By.name(elementName))
							.getAttribute(attribute);
				}
			} else if (elementIdentifierMap.get("byPLinktext").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.name(elementName))
							.getAttribute(attribute);
				} else {
					uiText = parent.findElement(By.name(elementName))
							.getAttribute(attribute);
				}
			} else if (elementIdentifierMap.get("byClassname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.name(elementName))
							.getAttribute(attribute);
				} else {
					uiText = parent.findElement(By.name(elementName))
							.getAttribute(attribute);
				}
			} else if (elementIdentifierMap.get("byTagname").equals( //$NON-NLS-1$
					elementIdentifier)) {
				if (parent == null) {
					uiText = driver.findElement(By.name(elementName))
							.getAttribute(attribute);
				} else {
					uiText = parent.findElement(By.name(elementName))
							.getAttribute(attribute);
				}
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to find the given element '" //$NON-NLS-1$
			    + elementName + "' Identifier='" + elementIdentifier+"' : '"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
            
            throw e;
		}

		return uiText;

	}

	/***
	 * Get the title text of current page.
	 * 
	 * @return			the title or null
	 * @author shekhar.bhardwaj
	 */
	public String goGetTitle() throws Exception {

	    int maxTries = Config.getPropertyAsInt_S("Sleep.GopherDriver.goGetTitle.maxTries"); //$NON-NLS-1$
	    int waitTime = Config.getPropertyAsInt_S("Sleep.GopherDriver.goGetTitle.waitTime"); //$NON-NLS-1$

		String pageTitle = null;
		try {
			log.info("Looking for current page title."); //$NON-NLS-1$
            Exception ex = null;
			for (int i=0; i < maxTries; i++) {
			    try {
			        pageTitle = driver.getTitle();
			        Thread.sleep(waitTime);
		            Assert.assertNotNull(pageTitle, "The pageTitle is null."); //$NON-NLS-1$
			        break;
			    } catch (Exception e) {
			        ex=e;
			    }
			}
			if (ex != null) {
			    throw ex;
			}
			log.info("Current page title found"); //$NON-NLS-1$
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error(
				"Exception finding current page title:\n"+ //$NON-NLS-1$
				e.getMessage()+"."); //$NON-NLS-1$
            
            throw e;
		}
		return pageTitle;
	}

	/**
	 * Go back to the previous URL.
     * @throws Exception    on any error
     * @author shekhar.bhardwaj
	 */
	public void goBackJavaScriptExecuter() throws Exception {
		try {
			log.info("Navigating back to previous page <<<:"); //$NON-NLS-1$
			driver.executeScript("window.history.go(-1)"); //$NON-NLS-1$
            sleep("Sleep.GopherDriver.goBackJavaScriptExecuter"); //$NON-NLS-1$
			log.info("Navigated back to || :-" //$NON-NLS-1$
					+ goGetTitle());
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception trying to go back : '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
			throw e;
		}
	}

	/***
	 * This method fetches HTTP Status code of applied Web URL.
	 * 
	 * @param weburl		the url
	 * @return				int value of status code
	 * @throws Exception	on error
	 * @author shekhar.bhardwaj
	 */
	public int getHTTPResponseStatusCode(String weburl) throws Exception {

        Assert.assertNotNull(weburl, "The argument String weburl is null."); //$NON-NLS-1$

        int code = 0;
	    try {
	        URL url = new URL(weburl);
	        HttpURLConnection http = (HttpURLConnection)url.openConnection();
            Assert.assertNotNull(http, "The HttpURLConnection http is null."); //$NON-NLS-1$
	        code = http.getResponseCode();
            Assert.assertNotNull(code, "The Http response is null."); //$NON-NLS-1$
	    } catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error ("Exception trying to get HTTP status code : '"+ //$NON-NLS-1$
	                e.getMessage()+"'."); //$NON-NLS-1$
	        throw e;
	    }
	    return code;
    }
 
	/**
     * Switch case for HTTP status code.
     * @param	status		the status
     * @author shekhar.bhardwaj
     */
	public void statusSwitch(int status) {	      

	      switch(status)
	      {
	         case 100 :
	            log.info("status100 ::Continue");  //$NON-NLS-1$
	            break;
	         case 101 :
	        	 log.info("status100 ::Continue");  //$NON-NLS-1$
	        	 break;
	         case 200:
	        	 log.info("status::200 OK – Request was successful");  //$NON-NLS-1$
	            break;
	         case 201 :
	        	 log.info("status::201 Created – Request was successful and something new was created based on that request.");  //$NON-NLS-1$
	        	 break;
	         case 202 :
	        	 log.info("status::202 Accepted – Request was received successfully, but may not be acted on immediately.");  //$NON-NLS-1$
	            break;
	         case 203 :
	        	 log.info("status::203 Non-authoritative Information – Request was successful, but information sent to the client about the response comes from a 3rd party server.");  //$NON-NLS-1$
	            break;
	         case 204:
	        	 log.info("status::204 No Content – Request was successful, but no data is sent back.");  //$NON-NLS-1$
	            break;
	         case 205:
	        	 log.info("status::205 Reset Content – Request from the server to reset the information sent, such as form data.");  //$NON-NLS-1$
	            break;
	         case 206:
	        	 log.error("status::206 Partial Content – Response to a successful request for only part of a document.");  //$NON-NLS-1$
	            break;
	         case 401 :
	        	 log.error("status::HTTP error 401 (unauthorized)");  //$NON-NLS-1$
	            break;
	         case 400 :
	        	 log.error("status:: HTTP error 400 (bad request)");  //$NON-NLS-1$
	            break;
	         case 403 :
	        	 log.error("status::HTTP error 403 (forbidden)");  //$NON-NLS-1$
	            break;
	         case 404 :
	        	 log.error("status::HTTP error 404 (not found)");  //$NON-NLS-1$
	            break;
	         case 500 :
	        	 log.error("status:: HTTP error 500 (internal server error)");  //$NON-NLS-1$
	            break;
	         default :
	        	 log.info("status::"+status);  //$NON-NLS-1$
	      }
	      log.info("please check if the status returned is expected.");  //$NON-NLS-1$
	}

	/**
	 * Navigate to the given URL.
	 * @param url			the URL
	 * @return				int HTTP Status
	 * @throws Exception	on error
	 * @author shekhar.bhardwaj
	 */
	public int goGetURL(String url) throws Exception {

        Assert.assertNotNull(url, "The argument String url is null."); //$NON-NLS-1$

        try {
	        driver.get(url);
	        // driver.manage().window().maximize(); This works only for firefox so
	        // added the above code
	        String currentURL = driver.getCurrentUrl();
            Assert.assertNotNull(currentURL, "The currentURL is null."); //$NON-NLS-1$
	        log.info(""+currentURL); //$NON-NLS-1$
	        int url_http_status=getHTTPResponseStatusCode(url);
	        log.info("HTTP status of requested URL is :"+url_http_status); //$NON-NLS-1$
	        statusSwitch(url_http_status);
	        return url_http_status;
	    }
	    catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error ("Exception trying to get URL : '"+e.getMessage()+"'."); //$NON-NLS-1$ //$NON-NLS-2$
	        throw e;
	    }
	}
	
	/**
	 * Get the current URL
	 * @return	the current URL
	 * @author shekhar.bhardwaj
	 */
	public String goGetCurrentURL()
	{
		String currentURL = driver.getCurrentUrl();
        Assert.assertNotNull(currentURL, "The currentURL is null."); //$NON-NLS-1$
		return currentURL;
	}
	
    /***
     * Is the element displayed with three separate pieces always use with XPATHs only.
     * @param parent
     * @param locator1
     * @param locator2
     * @param id
     * @return boolean
     * @throws Exception
     * @author shekhar.bhardwaj
     */
    public boolean isDisplayedWithThreeLocators(String[] locator1,String[] locator2, String id) throws Exception {

        Assert.assertNotNull(id, "The argument String id is null."); //$NON-NLS-1$
        Assert.assertNotNull(locator1, "The argument String[] locator1 is null."); //$NON-NLS-1$
        Assert.assertNotNull(locator2, "The argument String[] locator2 is null."); //$NON-NLS-1$

        String elementName = (locator1[1]).trim();
        //String elementIdentifier = (locator1[0]).trim();
        String elementName1 = (locator2[1]).trim();
        //String elementIdentifier1 = (locator2[0]).trim();
        
        boolean flag = false;
        try {
            flag = driver.findElement(By.xpath(elementName+id+elementName1)).isDisplayed();
            if (flag) {
                flag = true;
                log.info("The UI element " + elementName //$NON-NLS-1$
                        +id+elementName1+ " is displayed"); //$NON-NLS-1$
            } else {
                log.debug("The UI element " + elementName //$NON-NLS-1$
                        +id+elementName1+ " is not displayed"); //$NON-NLS-1$
            }
        } catch (Exception e) {
            log.warn("Exception occoured while finding the given element " //$NON-NLS-1$
                    + elementName+id+elementName1);
        }
        return flag;
    }

	/**
	 * @param browserName	'firefox', 'chrome', 'iexplore' or 'safari'
	 * @param browserName	the browser name
	 * @return				the browser version
	 * @author shekhar.bhardwaj
	 */
	public String getBrowserVersion(String browserName){

	    Assert.assertNotNull(browserName, "The argument String browserName is null."); //$NON-NLS-1$

	    String browserVersion = "None"; //$NON-NLS-1$
	    WebDriverBackedSelenium selenium = new WebDriverBackedSelenium(driver,"http://www.google.com"); //$NON-NLS-1$
	    if(browserName.equals("firefox")){ //$NON-NLS-1$
	        browserVersion=selenium.getEval("navigator.userAgent.substring(navigator.userAgent.indexOf('Firefox')+8);"); //$NON-NLS-1$
	    }else if(browserName.equals("iexplore")){ //$NON-NLS-1$
	        browserVersion=selenium.getEval("navigator.userAgent.substring(navigator.userAgent.indexOf('MSIE')+5,navigator.userAgent.indexOf('.',navigator.userAgent.indexOf('MSIE')))"); //$NON-NLS-1$
	    }else if(browserName.equals("chrome")){ //$NON-NLS-1$
	        browserVersion=selenium.getEval("navigator.userAgent.substring(navigator.userAgent.indexOf('Chrome')+7,navigator.userAgent.indexOf('Safari'));"); //$NON-NLS-1$
	    }else if(browserName.equals("safari")){ //$NON-NLS-1$
	        browserVersion=selenium.getEval("navigator.userAgent.substring(navigator.userAgent.indexOf('Chrome')+7,navigator.userAgent.indexOf('Safari'));"); //$NON-NLS-1$
	        browserVersion=browserVersion.substring(browserVersion.length()-4,browserVersion.length());
	    }
	    return browserVersion;		
	}

	/***
     * This function has loop for finding duplicate
     * @param String[]  the array of asset names
     * @return boolean
     * @author shekhar.bhardwaj
     */
    public boolean goCheckDuplicated(String[] sValueTemp)
    {
        Assert.assertNotNull(sValueTemp, "The argument String[] sValueTemp is null."); //$NON-NLS-1$

        Set<String> sValueSet = new HashSet<String>();
        for(String tempValueSet : sValueTemp)
        {
            if (sValueSet.contains(tempValueSet))
                return true;
            else
                if(!tempValueSet.equals("")) //$NON-NLS-1$
                    sValueSet.add(tempValueSet);
        }
        return false;
    }
    
    /***
     * This function fails if the asset has duplicate values assertFalse(condition, "message")
     * @paramString[] assetNames
     * @return void
     * @author shekhar.bhardwaj
     */
    public void isAssetDuplicate(String[] assetNames)
    {
        Assert.assertNotNull(assetNames, "The argument String[] assetNames is null."); //$NON-NLS-1$
        boolean duplicate = false;
        duplicate = goCheckDuplicated(assetNames);
        
        if(duplicate){
            Assert.assertFalse(duplicate, "duplicate asset found, test case will fail"); //$NON-NLS-1$
            log.error("DATA FAILURE: Asset duplicated! \n"); //$NON-NLS-1$
            }else{
            
            log.debug("DATA : Asset not duplicated! \n"); //$NON-NLS-1$
        }
 
    }

    /**
     * Sleep for the time defined by the value of the given property.
     * @param propertyName  the key to the property
     * @author shekhar.bhardwaj
     */
    public void sleep (String propertyName) {
 
        Assert.assertNotNull(propertyName, "The argument String propertyName is null."); //$NON-NLS-1$

        String timeStr = Config.getProperty_S(propertyName);
    	// A default time in ms.
    	int time = 1000;
    	if (timeStr==null){
    		log.warn("Sleep time property with name '"+propertyName+"' not found.\n"+ //$NON-NLS-1$ //$NON-NLS-2$
    				"Using 1 sec."); //$NON-NLS-1$
    	} else {
    		try {
    			time=Integer.parseInt(timeStr);
    		} catch (Exception e) {
    			log.warn("Could not parse time string '"+timeStr+"' as an int.\n"+ //$NON-NLS-1$ //$NON-NLS-2$
    					"Using default 1 sec."); //$NON-NLS-1$
    		}
    	}
    	try {
        	Thread.sleep(time);    		
    	} catch (Exception e) {
    		log.error(e.getMessage());
    	}
    }
    
    
    /***
     * Get the text with three separate pieces always use with XPATHs only.
     * @param parent
     * @param locator1
     * @param locator2
     * @param id
     * @return boolean
     * @throws Exception
     * @author shekhar.bhardwaj
     */
    public String getTextWithThreeLocators(String[] locator1,String[] locator2, String id) throws Exception {

        Assert.assertNotNull(id, "The argument String id is null."); //$NON-NLS-1$
        Assert.assertNotNull(locator1, "The argument String[] locator1 is null."); //$NON-NLS-1$
        Assert.assertNotNull(locator2, "The argument String[] locator2 is null."); //$NON-NLS-1$

        String elementName = (locator1[1]).trim();
        //String elementIdentifier = (locator1[0]).trim();
        String elementName1 = (locator2[1]).trim();
        //String elementIdentifier1 = (locator2[0]).trim();
        
        String text = null;
        try {
        	text = driver.findElement(By.xpath(elementName+id+elementName1)).getText();
        	log.info("Found status -"+text); //$NON-NLS-1$
        } catch (Exception e) {
            log.warn("Exception occoured while finding the given element " //$NON-NLS-1$
                    + elementName+id+elementName1);
            throw e;
        }
        return text;
    }
    
    
    /***
     * Click with three separate pieces always use with XPATHs only.
     * @param parent
     * @param locator1
     * @param locator2
     * @param id
     * @return boolean
     * @throws Exception
     * @author shekhar.bhardwaj
     */
    public void goClickWithThreeLocators(String[] locator1,String[] locator2, String id) throws Exception {

        Assert.assertNotNull(id, "The argument String id is null."); //$NON-NLS-1$
        Assert.assertNotNull(locator1, "The argument String[] locator1 is null."); //$NON-NLS-1$
        Assert.assertNotNull(locator2, "The argument String[] locator2 is null."); //$NON-NLS-1$

        String elementName = (locator1[1]).trim();
        //String elementIdentifier = (locator1[0]).trim();
        String elementName1 = (locator2[1]).trim();
        //String elementIdentifier1 = (locator2[0]).trim();
        try {
        	boolean isdiaplyed = driver.findElement(By.xpath(elementName+id+elementName1)).isDisplayed();
        	if(isdiaplyed){
        	driver.findElement(By.xpath(elementName+id+elementName1)).click();
        	}else{
        	  log.info("No element found with given xpath: "+elementName+id+elementName1);	 //$NON-NLS-1$
        	}
        } catch (Exception e) {
            log.warn("Exception occoured while finding the given element " //$NON-NLS-1$
                    + elementName+id+elementName1);
            throw e;
        }
    }
    
    /***
     * function to navigate back to former URL
     * @throws InterruptedException
     * @author shekhar.bhardwaj
     */
    public void gopherNavigateBack() throws InterruptedException{
    	
    	String store = driver.getCurrentUrl();
    	Thread.sleep(5000);
    	try{
    	driver.navigate().back();
    	Thread.sleep(5000);
    	log.info("Navigating back<<"); //$NON-NLS-1$
    	String store1 = driver.getCurrentUrl();
    	if(!(store.equals(store1))){
    		log.debug("Successfully navigated back"); //$NON-NLS-1$
    	}else{
    		log.warn("problem with back navigation !!, test might fail, trying again with java script"); //$NON-NLS-1$
    		 goBackJavaScriptExecuter();
    	}
    	} catch(Exception e){
    		log.warn("problem with back navigation !!, test might fail"); //$NON-NLS-1$
    		log.error(e);
    	}
    }

    /**
     * Get the RemoteWebDriver used by this class.
     * @return  the driver
     * @author shekhar.bhardwaj
     */
    public RemoteWebDriver getRemoteWebDriver () {
        return driver;
    }

    /***
     * Press enter or return key on a XPATH and only on a XPATH.
     * @param String[] locator
     * @author shekhar.bhardwaj
     */
    public void goPressReturn(String[] locator){
    	String xpath = locator[1];
    	driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
    }
    
    /**
	 * This method will be used to enter the text in the given text area.
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @param text     text to be entered
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goClearText(WebElement parent, String[] locator) throws Exception {

        //Assert.assertNotNull(parent, "The argument WebElement parent is null.");
        Assert.assertNotNull(locator, "The argument String[] locator is null."); //$NON-NLS-1$
        String elementName = null;
		String elementIdentifier = null;
		try {
	        elementName = (locator[1]).trim();
	        elementIdentifier = (locator[0]).trim();
			WebElement uiElement = getWebElement(parent, locator);
			if (isEnabled(parent, locator) == true) {
				uiElement.clear();
			} else {
				log.info("The ui element with name " //$NON-NLS-1$
						+ elementName + " is not found"); //$NON-NLS-1$
			}
		} catch (Exception e) {
            // Add the error to the list of errors.
            ErrorUtil.addVerificationFailure(e);

            log.error("Exception while trying to cle text in '" //$NON-NLS-1$
				+ elementName+ "', elementIdentifier='"+ elementIdentifier+"' :'"+ //$NON-NLS-1$ //$NON-NLS-2$
			    e.getMessage()+"'."); //$NON-NLS-1$
			throw e;
		}
	}

}