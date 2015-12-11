import org.jsoup.helper.Validate;
import emailsearch.EmailScraper;
import emailsearch.WebDriverEmailScraper;

/*
 *
 *
 * Start a search with an EmailScraper or a WebDriverEmailScraper
 * depending on command line input. If only a url is provided, the EmailScraper
 * is started by default. If "wd" is provided as the second argument, a
 * search is started with the WebDriverEmailScraper.
 */
public class EmailSearchEngine {
    
    public static void main(String[] args){
        boolean wd = false;
        Validate.isTrue(args.length != 0, "Usage: Supply url to scrape");
        
        String url = args[0];
        if(!url.startsWith("https://") && !url.startsWith("http://")){
            url = "http://" + url;
        }
        
        if(args.length > 1){
            Validate.isTrue(args[1].equals("wd"), "Usage: Second argument is not recognized. To start scraping with a web driver use 'wd' as the second argument.");
            wd = true;
        }
        
        if(!url.startsWith("https://") && !url.startsWith("http://")){
            url = "https://" + url;
        }
        
        if(wd){
            WebDriverEmailScraper wdScraper = new WebDriverEmailScraper(url);
            wdScraper.startSearch();
        }
        else{
            EmailScraper scraper = new EmailScraper(url);
            scraper.startSearch();
        }
        
        
    }
}
