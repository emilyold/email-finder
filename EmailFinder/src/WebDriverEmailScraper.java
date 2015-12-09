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
	
	public void startSearch(){
		System.out.println("Looking for emails on " + rootUrl.toExternalForm() + "...");
        String hostAndPath = formatUrlString(rootUrl.getHost() + rootUrl.getPath());
		toVisit.offer(hostAndPath);
		linksSet.add(hostAndPath);
		findEmails();
	}
	
	private void updateQueue(List<WebElement> links){
		for(WebElement link : links){
			String url = link.getAttribute("href");
			if(url.contains("mailto:")){
				emailSet.add(url.replace("mailto:", ""));
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
	
	private void findEmails(){
		while(!toVisit.isEmpty()){
			String currentPage = toVisit.poll();
			driver = new FirefoxDriver();
			driver.get("https://" + currentPage);
			List<WebElement> elements = driver.findElements(By.xpath("//*[@href]"));
			updateQueue(elements);
            driver.close();
		}
		printAllEmails();
	}
    
    private String formatUrlString(String urlString){
        if(urlString.endsWith("/")){
            return urlString.substring(0, urlString.length()-1);
        }
        return urlString.replace("//", "/");
    }
	
	private void printAllEmails(){
		for(String e : emailSet){
			System.out.println(e);
		}
	}
	
	public static void main(String[] args){
		Validate.isTrue(args.length == 1, "usage: supply url to scrape");
		String url = args[0];
		if(!url.startsWith("https://") && !url.startsWith("http://")){
			url = "https://" + url;
		}
		WebDriverEmailScraper scraper = new WebDriverEmailScraper(url);
		scraper.startSearch();
	}
}
