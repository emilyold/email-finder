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
	
	URL rootUrl;
	private HashSet<String> linksSet;
	private  Queue<String> toVisit;
	private HashSet<String> emailSet;
	private WebDriver driver;
	
	public WebDriverEmailScraper(String url){
		linksSet = new HashSet<String>();
		emailSet = new HashSet<String>();
		toVisit = new LinkedList<String>();
		driver = new FirefoxDriver();
		driver.get(url);
		try {
			rootUrl = new URL(driver.getCurrentUrl());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void startSearch(){
		//List<WebElement> elements = driver.findElements(By.xpath("//*[@href]"));
		System.out.println("Looking for emails on " + rootUrl.toExternalForm() + "...");
		toVisit.offer(rootUrl.getHost() + rootUrl.getPath());
		linksSet.add(rootUrl.getHost() + rootUrl.getPath());
		//updateQueue(elements);
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
				String hostAndPath = absUrl.getHost() + absUrl.getPath();
				if(hostAndPath.charAt(hostAndPath.length()-1) == '/') 
					hostAndPath = hostAndPath.substring(0, hostAndPath.length()-1);
				hostAndPath = hostAndPath.replace("//", "/");
				if(absUrl.getHost().equals(rootUrl.getHost()) && !linksSet.contains(hostAndPath)){
					System.out.println("adding " + hostAndPath);
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
			System.out.println("opening " + currentPage);
			driver = new FirefoxDriver();
			driver.get("http://" + currentPage);
			List<WebElement> elements = driver.findElements(By.xpath("//*[@href]"));
			//test!
			driver.close();
			updateQueue(elements);
		}
		printAllEmails();
	}
	
	private void printAllEmails(){
		for(String e : emailSet){
			System.out.println(e);
		}
	}
	
	public static void main(String[] args){
//		Validate.isTrue(args.length == 1, "usage: supply url to scrape");
//		String url = args[0];
//		if(!url.startsWith("https://") && !url.startsWith("http://")){
//			url = "http://" + url;
//		}
		WebDriverEmailScraper ef = new WebDriverEmailScraper("http://jana.com");
		ef.startSearch();
		//ef.test();
	}
}
