package emailsearch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/*
 * Scrapes a website for email addresses using a 
 * web driver. Should be used on websites with 
 * dynamically defined html.
 */
public class WebDriverEmailScraper{
	
	private URL rootUrl;
	private HashSet<String> linksSet;
	private Queue<String> toVisit;
	private HashSet<String> emailSet;
	private WebDriver driver;
	
	public WebDriverEmailScraper(String url){
		linksSet = new HashSet<String>();
		emailSet = new HashSet<String>();
		toVisit = new LinkedList<String>();
		driver = null;
		try {
			rootUrl = new URL(url);
		} catch (MalformedURLException e) {
            System.out.println("The url provided is malformed");
		}
	}
	
    /*
     * Only public method. Adds the root url's
     * information to the queue of urls to visit
     * and set of url's seen, so that crawling can begin.
     */
	public void startSearch(){
		System.out.println("Looking for emails on " + rootUrl.toExternalForm() + "...");
        String hostAndPath = formatUrlString(rootUrl.getHost() + rootUrl.getPath());
		toVisit.offer(hostAndPath);
		linksSet.add(hostAndPath);
		crawl();
	}
	
    /*
     * Extracts all hrefs from each WebElement in the list provided.
     * If an href is an email address, it gets added to the set of
     * dicovered email addresses. If a url has not been discovered before, and if it
     * has the same host as the root url, add it to the queue
     * of urls to visit.
     */
	private void updateQueue(List<WebElement> links){
		for(WebElement link : links){
			String url = link.getAttribute("href");
			if(url.contains("mailto:")){
				emailSet.add(url.replace("mailto:", ""));
                continue;
			}
			if(url.startsWith("/")){
				url = driver.getCurrentUrl() + url;
			}
            
			
			URL absUrl;
			try {
				absUrl = new URL(url);
				String hostAndPath = formatUrlString(absUrl.getHost() + absUrl.getPath());
				if(absUrl.getHost().equals(rootUrl.getHost()) && !linksSet.contains(hostAndPath)){
					toVisit.offer(hostAndPath);
					linksSet.add(hostAndPath);
				}
			} catch (MalformedURLException e) {
				
			}
		}
	}
	
    /*
     * Continuously connects to urls that need to be visited
     * in a new web driver window. Extracts hrefs from the current
     * web page and updates the queue of urls that need to be visited
     */
	private void crawl(){
		while(!toVisit.isEmpty()){
			String currentPage = toVisit.poll();
			driver = new FirefoxDriver();
			driver.get("https://" + currentPage);
            
			List<WebElement> elements = driver.findElements(By.cssSelector("a[href]"));
			updateQueue(elements);
            //driver.close();
		}
		printAllEmails();
	}
    
    /*
     * Helper method to make sure a url's that are added
     * to the queue or set are all formatted in the same way
     * Assumes url string that is passed in does not have a protocol prefix.
     */
    private String formatUrlString(String urlString){
        if(urlString.endsWith("/")){
            return urlString.substring(0, urlString.length()-1);
        }
        return urlString.replace("//", "/");
    }
	
    
    /*
     * Prints a list of discovered emails to standard out.
     */
    private void printAllEmails(){
        if(emailSet.size() == 0){
            System.out.println("No emails were found on " + rootUrl.toExternalForm());
            return;
        }
        for(String e : emailSet){
            System.out.println(e);
        }
    }

}
