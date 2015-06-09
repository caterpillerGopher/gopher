package com.echostar.gopher.selenium;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

/**
 * This class handles the Selenium web driver functions.
 * 
 * @author Shekhar Bhardwaj
 * 
 */
public interface GopherDriver {

	/**
	 * This method will be used to pause the script until the given element to
	 * be displayed.
	 * 
	 * @param maxTries     the maximum number of waits, each duration
	 * 						sleep("Sleep.GopherDriverAPI.waitForElementToBeVisible");
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void waitForElementToBeVisible(int maxTries, String[] locator) throws Exception;

	/**
	 * This method will be used to display the specified message when script in
	 * pause state.
	 * 
	 * @param maxTries	the maximum number of tries, each of duration
	 * 					sleep("Sleep.GopherDriverAPI.waitForAuction")
	 * @param messageToBeDisplayed
	 *            the message to be written to stdout on timeout
	 * @throws Exception
	 *             on error
	 *             @author shekhar.bhardwaj
	 */
	public void waitForAuction(int maxTries, String messageToBeDisplayed) throws Exception;

	/**
	 * This method will be used to capture the screen shot of an application.
	 * 
	 * @param picture_name     name for image
	 * @throws Exception       on error
	 * @author shekhar.bhardwaj
	 */
	public void selfie(String picture_name) throws Exception;

	/**
	 * Close and quit the browser.
	 * 
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void stopDriver() throws Exception;

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
		String[] locator, boolean checkFlag) throws Exception;

	/**
	 * Click on the given UI element i.e button/text/...
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goClickUIElement(WebElement parent, String[] locator) throws Exception;

	/**
	 * This method will be used to enter the text in the given text area.
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @param text     text to be entered
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goSendText(WebElement parent, String[] locator, String text) throws Exception;

	/**
	 * Select/click the given link.
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goClickOnLink(WebElement parent, String[] locator) throws Exception;

	/**
	 * This method will be used to select/click the given partial link.
	 * 
	 * @param parent   tbd
	 * @param locator  array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goClickOnPartialLink(WebElement parent, String[] locator) throws Exception;

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
	public WebElement getWebElement(WebElement parent, String[] locator) throws Exception;

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
	public List<WebElement> getWebElements(WebElement parent, String[] locator) throws Exception;

	/**
	 * This method returns true/false based on the UI element diaplyed status.
	 * 
	 * @param parent       tbd
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @return             true (displayed) or false
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public boolean isDisplayed(WebElement parent, String[] locator) throws Exception;

	/**
	 * Is the UI element enabled?
	 * 
	 * @param parent       tbd
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @return             true (Enabled) or false
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public boolean isEnabled(WebElement parent, String[] locator) throws Exception;

	/**
	 * This method used to verify the text in the webpage.
	 * 
	 * @param textToBeVerified the text to be verified
	 * @return                 true (present) or false
     * @throws Exception        on error
     * @author shekhar.bhardwaj
	 */
	public boolean isTextPresent(String textToBeVerified) throws Exception;

	/**
	 * Perform mouse over event on the given UI element.
	 * 
	 * @param parent       tbd
	 * @param locator      array returned by {@link #getLocator getLocator}
	 * @throws Exception   on error
	 * @author shekhar.bhardwaj
	 */
	public void goPerformMouseOver(WebElement parent, String[] locator) throws Exception;

	/**
	 * This method is used to perform mouse over event on the given UI element.
	 * 
	 * @param element  the web element to be moused over
	 * @author shekhar.bhardwaj
	 */
	public void goBuildMouseOverJavascriptExecutor(WebElement element) throws Exception;

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
		String[] locator, String itemTobe_selected) throws Exception;

	/**
	 * Get the requested url , ie http://www.dishanywhere.com
	 * 
	 * @param url			the URL
	 * @param browserName	the browser name
	 * @throws Exception	on any error
	 * @author shekhar.bhardwaj
	 */
	public void navigate2testsite(String url, String browserName) throws Exception;

	/***
	 * This Method gets the text in string format from GUI
	 * 
	 * @param parent		the parent
	 * @param locator		the locator
	 * @return				web element text or null
     * @throws Exception   on error
     * @author shekhar.bhardwaj
	 */

	public String goGetText(WebElement parent, String[] locator) throws Exception;

	/***
	 * This function compares actual and expected values
	 * 
	 * @param actual_text		the text we found
	 * @param expected_text		the text we expected
	 * @return boolean			true if equal
     * @throws Exception        on any error
     * @author shekhar.bhardwaj
	 */
	public boolean goCompare(String expected_text, String actual_text);

	/***
	 * !! STOP !! Under construction kindly use driver to directly send keys
	 * 
	 * @param parent		the WebElement parent
	 * @param key			the keys to send
	 * @param locator		the locator
	 * @throws Exception	on any error
	 * @author shekhar.bhardwaj
	 */
	public void goSendKeys(WebElement parent, String key, String[] locator) throws Exception;

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
	public String goGetAttribute(WebElement parent, String attribute, String[] locator) throws Exception;

	/***
	 * Get the title text of current page.
	 * 
	 * @return			the title or null
	 * @author shekhar.bhardwaj
	 */
	public String goGetTitle() throws Exception;

	/**
	 * Go back to the previous URL.
     * @throws Exception    on any error
     * @author shekhar.bhardwaj
	 */
	public void goBackJavaScriptExecuter() throws Exception;

	/***
	 * This method fetches HTTP Status code of applied Web URL.
	 * 
	 * @param weburl		the url
	 * @return				int value of status code
	 * @throws Exception	on error
	 * @author shekhar.bhardwaj
	 */
	public int getHTTPResponseStatusCode(String weburl) throws Exception;
 
	/**
     * Switch case for HTTP status code.
     * @param	status		the status
     * @author shekhar.bhardwaj
     */
	public void statusSwitch(int status);

	/**
	 * Navigate to the given URL.
	 * @param url			the URL
	 * @return				int HTTP Status
	 * @throws Exception	on error
	 * @author shekhar.bhardwaj
	 */
	public int goGetURL(String url) throws Exception;
	
	/**
	 * Get the current URL
	 * @return	the current URL
	 * @author shekhar.bhardwaj
	 */
	public String goGetCurrentURL();
	
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
    public boolean isDisplayedWithThreeLocators(String[] locator1,String[] locator2, String id) throws Exception;

	/**
	 * @param browserName	'firefox', 'chrome', 'iexplore' or 'safari'
	 * @param browserName	the browser name
	 * @return				the browser version
	 * @author shekhar.bhardwaj
	 */
	public String getBrowserVersion(String browserName);

	/***
     * This function has loop for finding duplicate
     * @param String[]  the array of asset names
     * @return boolean
     * @author shekhar.bhardwaj
     */
    public boolean goCheckDuplicated(String[] sValueTemp);
    
    /***
     * This function fails if the asset has duplicate values assertFalse(condition, "message")
     * @paramString[] assetNames
     * @return void
     * @author shekhar.bhardwaj
     */
    public void isAssetDuplicate(String[] assetNames);

    /**
     * Sleep for the time defined by the value of the given property.
     * @param propertyName  the key to the property
     * @author shekhar.bhardwaj
     */
    public void sleep (String propertyName);
     
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
    public String getTextWithThreeLocators(String[] locator1,String[] locator2, String id) throws Exception;
    
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
    public void goClickWithThreeLocators(String[] locator1,String[] locator2, String id) throws Exception;
    
    /***
     * function to navigate back to former URL
     * @throws InterruptedException
     * @author shekhar.bhardwaj
     */
    public void gopherNavigateBack() throws InterruptedException;
    
    /**
     * Get the RemoteWebDriver used by this class.
     * @return  the driver
     * @author shekhar.bhardwaj
     */
    public RemoteWebDriver getRemoteWebDriver ();

    /***
     * Press enter or return key on a XPATH and only on a XPATH.
     * @param String[] locator
     * @author shekhar.bhardwaj
     */
    public void goPressReturn(String[] locator);
    
    /***
     * Clear the text area
     * @param parent
     * @param locator
     * @throws Exception
     * @author shekhar.bhardwaj
     */
    public void goClearText(WebElement parent, String[] locator) throws Exception;

}