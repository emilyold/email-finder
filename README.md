# email-finder
Crawler to find all email addresses on a website

This is a maven command line application. In order to run the application, you must have maven installed according the instructions described here: https://maven.apache.org/install.html. 

Once maven is configured correctly, navigate to the EmailFinder directory and compile the entire project with this command: mvn compile

There are two applications which can be run depending on the format of the website you are hoping to search...

(1) EmailScraper -- run with the command: mvn exec:java -Dexec.mainClass="EmailScraper" -Dexec.args="<url to search>"
If you prefer not to see the verbose maven output use the -q option after mvn in the command.

If the website you wish to search has statically defined html, the EmailScraper should be used. This application utilizes the JSoup (jsoup.org) library to connect to and parse pages of html. For a url to be included in the crawl, it must be explicity defined as an href attribute under an <a> tag in the html. Both absolute and relative urls will be processed.

(2) WebDriverEmailScraper -- run with the command: mvn exec:java -Dexec.mainClass="WebDriverEmailScraper" -Dexec.args="<url to search>"
If you prefer not to see the verbose maven output use the -q option after mvn in the command.

If the website you wish to search has dynamically defined html (like Jana's for example), the WebDriverEmailScraper should be used. This application utilizes the selenium library to connect to and parse web pages. I used because it supports JavaScript processing. Unfortunately, in order to extract as much data as possible from each url that is crawled, a new WebDriver is created for each url.

OUTPUT:
The output of both applications is the same. If there is ever a problem loading the content of a url that is discovered while crawling, a message will be printed to the console telling the user which url has not been processed. Once the application finishes crawling all of the discovered urls within the original url's domain, either a list of the emails that were discovered, or a message telling the user that no emails were found, will be printed to the console. Both of the applications exclusively look at urls with domains that exactly match the original url's domain. So, for example, a url like "blog.jana.com" will not be considered in a search for "jana.com".
