# email-finder
Web crawler and scraper that finds all email addresses on a website

This is an appache maven command line application. In order to run the application, you must have maven installed according the instructions described here: https://maven.apache.org/install.html. 

Once maven is configured correctly, please navigate to the EmailFinder directory and compile the entire project with this command: mvn compile.

There are two ways the application can be run once it is compiled. Both can be run using the EmailSearchEngine class. 

(1) EmailScraper -- run with the command: mvn exec:java -Dexec.mainClass="EmailSearchEngine" -Dexec.args="[url to search]"
If you prefer not to see the verbose maven output use the -q option after mvn in the command.

If the website you wish to search has statically defined html, the EmailScraper should be used. This application utilizes the JSoup (www.jsoup.org) library to connect to and parse pages of html. For a url to be included in the crawl, it must be explicity defined as an href attribute under an <a> tag in the html. Both absolute and relative urls will be processed.

(2) WebDriverEmailScraper -- run with the command: mvn exec:java -Dexec.mainClass="EmailSearchEngine" -Dexec.args="[url to search] wd"
The second argument, 'wd', indicates that you wish to run the search engine using a web driver.
If you prefer not to see the verbose maven output use the -q option after mvn in the command.

If the website you wish to search has dynamically defined html (like Jana's for example), the WebDriverEmailScraper should be used. This application utilizes the selenium library (http://www.seleniumhq.org) to use a Firefox web driver to connect to and parse web pages. You must have Firefox installed on your computer in order for this application to run. I used because it supports JavaScript processing. While the EmailScraper searches all of the text found on the website for emails, the WebDriverEmailScraper only detects emails that are defined in as href attributes with the format "mailto: <email address>". Unfortunately, in order to extract as much data as possible from each url that is crawled, a new WebDriver is created for each url.

OUTPUT:
The output of both applications is the same. If there is ever a problem loading the content of a url that is discovered while crawling, a message will be printed to the console telling the user which url has not been processed. Once the application finishes crawling all of the discovered urls within the original url's domain, either a list of the emails that were discovered, or a message telling the user that no emails were found, will be printed to the console. Both of the applications exclusively look at urls with domains that exactly match the original url's domain. So, for example, a url like "blog.jana.com" will not be considered in a search for "jana.com".
