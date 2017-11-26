package MyWebCrawler;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.net.*;
import java.util.regex.*;
import java.util.LinkedList;

// The program is designed to visit up to 100 different valid URLs from a given URL.
// DFS is applied. The program checks the source code of each URL line by line.
// The program leaves the current URL and enters a new URL("crawl deeper") once it finds a valid URL link on the current URL.
// The program stops crawling deeper when the capacity is met or when a URL doesn't contain other URL links or when it fails to establish the connection

public class MyWebCrawler {

    public static String regexUrl="http://[A-Za-z0-9_&@#/%?=~|!.:;,]+[A-Za-z0-9_&@#/%?=~|!.:;,]";
    public static int totCnt=0;  // the total number of URLs visited
    public static int successCnt=0;  // the number of successful visits (no I/O exception occurs during openstream() )

    public static void main(String[] args){
        System.out.println("This program visits up to 100 different URLs from a given URL.");
        System.out.print("Please enter a URL address: ");
        LinkedList<String> urlList=new LinkedList<String>(); // record all the URLs already visited

        // keep asking for a valid URL address until get one
        while(true){
            String inputUrl=new Scanner(System.in).next();
        try {
            // the constructor throws MalformedURLException if no such url exists or the string is null
            // if the URL has valid format, start web crawling
            URL currUrl = new URL(inputUrl);
            urlList.add(inputUrl);
            crawlWeb(currUrl,urlList);
            break;
        }
        catch(MalformedURLException ex){
            System.out.println("Invalid URL address.");
            System.out.print("Please enter the URL address anew or enter a new one: ");
        }
        }

        // print result
        System.out.println('\n'+ "Webcrawling result: ");
        System.out.println("Total number of URLs visited: "+ totCnt + " Num of successful visits: "+ successCnt);
    }

    public static void crawlWeb(URL currUrl,LinkedList<String> urlList) {
        if (totCnt == 100) {
            System.out.println("Reach crawling capacity. End of crawling.");
            return;
        }
        totCnt++;
        // Opens a connection to currURL and returns an InputStream for reading from that connection
        try {
            System.out.println("The current URL is " + currUrl.toString());
            InputStream in = currUrl.openStream(); // establish connection, throw IOexception if the command fails
            successCnt = successCnt + 1;
            System.out.println("Total number of URLs visited: " + totCnt + " Num of successful visits: " + successCnt);
            Scanner browse = new Scanner(in); // read the source code of current URL
            String Content = "";
            Pattern patternUrl = Pattern.compile(regexUrl, Pattern.CANON_EQ);
            boolean noURL=true;  // mark whether the current URL contains other valid URLs
            while (browse.hasNext()){
                // examine the webpage line by line
                 Content = browse.nextLine();
                // search for url addresses in the html text line
                Matcher matchUrl = patternUrl.matcher(Content);
                // once found, check the validity of the URL addresses
                // if the URL address is valid and it hasn't been visited, the program crawls further by recursion
                while (matchUrl.find()){
                    String temp = matchUrl.group();
                    try {
                        URL newUrl = new URL(temp);
                        if (appendUrlList(urlList, temp)) {
                            noURL=false;
                            System.out.println("A new URL found in the current URL. Crawl deeper.");
                            crawlWeb(newUrl, urlList);
                        }
                    }
                    catch (MalformedURLException ex) {
                        continue;
                    }
                }
            }
            // the whole webpage is checked
            if(noURL)
                // end of the URL digging(DFS), return
                System.out.println("Current URL contains no URLs. Crawl backwards.");
            in.close();
        }
        catch (IOException ex) {
            System.out.println("I/O error");
            System.out.println("Total number of URLs visited: " + totCnt + " Num of successful visits: " + successCnt);
            return;
        }
    }

    private static boolean appendUrlList(LinkedList<String> urlList,String newUrl){
        if (!urlList.contains(newUrl)){
            urlList.add(newUrl);
            return true;
        }
        else
            return false;
    }
}
