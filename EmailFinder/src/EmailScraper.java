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
 * Scrapes a website for email addresses. Should be used
 * on websites with statically defined html.
 */
public class EmailScraper {
	URL rootUrl;
	private HashSet<String> linksSet;
	private  Queue<URL> toVisit;
	private HashSet<String> emailSet;
	
	public EmailScraper(String url){
		try {
			this.rootUrl = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println("The url provided is malformed.");
		}
		linksSet = new HashSet<String>();
		emailSet = new HashSet<String>();
		toVisit = new LinkedList<URL>();
	}
	
    /*
     * Only public method. Checks for emails on the
     * root web page, and begins crawling linked pages.
     */
	public void startSearch(){
		try{
			Document doc = Jsoup.connect(rootUrl.toExternalForm()).timeout(10000).get();
			checkForEmails(doc);
			linksSet.add(rootUrl.getHost() + rootUrl.getPath());
			System.out.println("Looking for emails on " + rootUrl.toExternalForm() + "...");
			updateQueue(doc);
		}
		catch(IOException ioe){
			System.out.println("Could not connect to " + rootUrl);
		}
		crawl();
	}
	
    /*
     * Extracts all hrefs from the provided Document.
     * If a url has not been discovered before, and if it 
     * has the same host as the root url, add it to the queue
     * of urls to visit.
     */
	public void updateQueue(Document doc){
		Elements links = doc.select("a[href]");
		for(Element link : links){
			try{
				URL absUrl = new URL(link.attr("abs:href"));
				
				String hostAndPath = absUrl.getHost() + absUrl.getPath();
				if((absUrl.toExternalForm().contains("http://" + rootUrl.getHost())
						|| absUrl.toExternalForm().contains("https://" + rootUrl.getHost())
						|| absUrl.toExternalForm().contains("https://www." + rootUrl.getHost())
						|| absUrl.toExternalForm().contains("http://www." + rootUrl.getHost()))
						&& !linksSet.contains(hostAndPath) && !linksSet.contains(hostAndPath + "/")){
					toVisit.offer(absUrl);
					linksSet.add(hostAndPath);
				}
			}
			catch(MalformedURLException e){
				//ignore malformed urls that are found as hrefs
			}
		}
	}
	
    /*
     * Continuously connects to urls that need to be visited,
     * checks if the web page contains emails,
     * and updates the queue of urls that need to be visited
     */
	public void crawl(){
		while(!toVisit.isEmpty()){
			URL currentPage = toVisit.poll();
			try{
				
				Document doc = Jsoup.connect(currentPage.toExternalForm()).timeout(10000).get();
					
				checkForEmails(doc);
					
				updateQueue(doc);
				
			}
			catch(UnsupportedMimeTypeException umte){
				//ignore urls with unsupported types (e.g. pdfs, mp3s, etc.)
			}
			catch(IOException ioe){
				System.out.println("Could not connect to " + currentPage.toExternalForm() +
						". Any emails on that page will not be found. Continuing to search.");
			}
			
		}
		printAllEmails();
	}
	
    /*
     * Searches for strings in the visible text of a web page
     * that have the format of an email address. Adds discovered emails
     * to the set of discovered emails.
     */
	private void checkForEmails(Document doc){
		Pattern pattern = Pattern.compile("[a-zA-Z0-9_.-]+@[a-zA-Z0-9-_.]+[a-zA-Z]{2,}+");
		
		Matcher matcher = pattern.matcher(doc.text());
		while(matcher.find()){
			String email = matcher.group();
			emailSet.add(email);
		}
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
//	public static void main(String[] args){
//		Validate.isTrue(args.length != 0, "usage: supply url to scrape");
//		String url = args[0];
//		if(!url.startsWith("https://") && !url.startsWith("http://")){
//			url = "http://" + url;
//		}
//		EmailScraper scraper = new EmailScraper(url);
//		scraper.startSearch();
//	}
}
