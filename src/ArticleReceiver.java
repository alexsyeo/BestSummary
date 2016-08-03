package bestsummarydevelopment;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

public class ArticleReceiver {

    private ArrayList<Article> newsArticles = new ArrayList<Article>();
    private ArrayList<String> newsLinks = new ArrayList<String>();
    private SentenceGenAlg weightSetter;
    //added attribute


    public ArticleReceiver() {}
    //change constructor
    public ArticleReceiver(int numArticles, String link, SentenceGenAlg sg) throws BoilerpipeProcessingException, SAXException {
    	this.weightSetter = sg;
        if (numArticles != 0) {
            receiveNewsArticles(numArticles, link);
        } else {
            System.out.println("ERROR: numArticles request for " + link + " cannot equal 0.");
        }
    }

    private void receiveNewsArticles(int numArticles, String urlAddress) throws BoilerpipeProcessingException, SAXException {
        URL rssUrl = null;
        // if connected to Internet
        if (internetIsAvailable()) {
            try {
                // gather links
                rssUrl = new URL(urlAddress);
                BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
                String fullRss = in.readLine();
                in.close();

                numArticles += 2;

                int linkTagStart = 0, linkTagEnd = 0, actualLink = 0;
                while (newsLinks.size() < numArticles) {
                    //find start and end of link tag
                    linkTagStart = fullRss.indexOf("<link>");
                    linkTagEnd = fullRss.indexOf("</link>");
                    newsLinks.add(fullRss.substring(linkTagStart + 6, linkTagEnd));
                    fullRss = fullRss.substring(linkTagEnd + 5);
                }

                //remove 2 news links
                if (!newsLinks.isEmpty()) {
                    newsLinks.remove(0);
                    newsLinks.remove(0);
                } else {
                    System.out.println("ERROR: No Found Articles. Check If You Have Wifi.");
                }

                //find actual website links inside google news links
                int startPos = 0;
                for (int i = 0; i < newsLinks.size(); i++) {
                    startPos = newsLinks.get(i).indexOf("url=") + 4;
                    newsLinks.set(i, newsLinks.get(i).substring(startPos));
                }

                // gather articles from "section" tag of article using Jsoup
                for (String newsLink : newsLinks) {
                	gatherArticles(newsLink);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: No internet connection established.");
            return;
        }
    }

    public boolean gatherArticles(String newsLink) throws IOException, BoilerpipeProcessingException, SAXException {
    //Jsoup:
        Document doc = Jsoup.connect(newsLink).timeout(5000).get();
        Elements element = doc.select("p");
        String articleA = element.text();
    //Boilerpipe:
        final HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(newsLink));
		final TextDocument docB = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
		String articleB = CommonExtractors.ARTICLE_EXTRACTOR.getText(docB);
		String title = docB.getTitle();
	//Merging two versions:
		String article;
		if (isNYT(newsLink))
			article = mergeNYT(articleA);
		else
			article = merge(articleA, articleB);
		//edited below
		newsArticles.add(new Article(article, Main.getSummarySentences(), this.weightSetter.getNext()));
        Article a = newsArticles.get(newsArticles.size()-1);
        a.setTitle(title);
        a.setUrl(newsLink);
		
        //Test Stuff:
        /*System.out.println("A:\n" + articleA + "\n\n");
        System.out.println("B:\n" + articleB + "\n\n");
        System.out.println(newsLink + "\n" + title + "\n\n" + article+ "\n-----------------\n\n\n\n");*/
		return true;
		
		
        //newsArticles.add(new Article(article, Main.SUMMARY_SENTENCES));
        
        //Article a = newsArticles.get(newsArticles.size()-1);
        //a.setTitle(title);
        //a.setUrl(newsLink);
    }
    private String mergeNYT(String articleA) {
    	int charCount = 150;
    	//Dash thing:
    	boolean dashed = false;
		int index = 0; 
		for (int i = 0; i < charCount; i++) {
			if (articleA.charAt(i) == '—') {
				index = i;
				articleA = articleA.substring(index+2);
				dashed = true;
				break;
			}
		}
    	if (!dashed) {
    		//Removing ads:
        	String bad1 = "Advertisement Advertisement ";
    		if (articleA.length() >= bad1.length() && articleA.substring(0,bad1.length()).equals(bad1))
    			articleA = articleA.substring(bad1.length());
    		//Removing authors/date:
    		String letters = "abcdefghijklmnopqrstuvwxyz";
    		index = 0;
    		int count = 0;
    		for (int i = 0; i < charCount; i++) {
    			boolean isCapital = articleA.charAt(i) == ' ';
    			if (!isCapital) {
    				boolean isLetter = false;
        			for (int j = 0; j < letters.length(); j++) {
        				if (Character.toLowerCase(articleA.charAt(i)) == letters.charAt(j))
        					isLetter = true;
        			}
        			if (isLetter)
        				isCapital = Character.toLowerCase(articleA.charAt(i)) != articleA.charAt(i);
    			}
    			if (isCapital) {
    				count++;
    				if (count > 8 /*Change this var*/)
    					index = i;
    			}
    			if (!isCapital)
    				count = 0;
    		}
    		if (index != 0) {
    			for (int i = index; i < articleA.length(); i++) {
    				boolean isLetter1 = false;
    				boolean isLetter2 = false;
    				for (int j = 0; j < letters.length(); j++) {
    					if (Character.toLowerCase(articleA.charAt(i)) == letters.charAt(j))
    						isLetter1 = true;
    					if (Character.toLowerCase(articleA.charAt(i+1)) == letters.charAt(j))
    						isLetter2 = true;
    				}
    				if ((isLetter1 && isLetter2) && (Character.toLowerCase(articleA.charAt(i)) != articleA.charAt(i) && Character.toLowerCase(articleA.charAt(i+1)) == articleA.charAt(i+1))) {
    					articleA = articleA.substring(i);
    					break;
    				}
    					
    			}
    		}
    	}
    	/*
		//Removing authors:
		if (articleA.substring(0,2).equals("By")) {
			index = 0;
			for (int i = 0; i < articleA.length(); i++) {
				if (articleA.charAt(i) == '.') {
					index = i;
					break;
				}
			}
			articleA = articleA.substring(index+2);
		}
		//Removing date:
		String letters = "abcdefghijklmnopqrstuvwxyz";
		index = 0;
		for (int i = 0; i < articleA.length(); i++) {
			boolean isLetter = false;
			for (int j = 0; j < letters.length(); j++) {
				if (letters.charAt(j) == Character.toLowerCase((articleA.charAt(i)))) {
					isLetter = true;
					break;
				}
			}
			if (isLetter) {
				articleA = articleA.substring(index);
				break;
			}
			else
				index++;
		}
		//removing end:
		String bad2 = " See More » Go to Home Page »";
		if (articleA.length() >= bad2.length() && articleA.substring(articleA.length()-bad2.length(),articleA.length()).equals(bad2))
			articleA = articleA.substring(0, articleA.length()-bad2.length());
		String endPhrase = " Find out what you need to know about the 2016 presidential race today, and get politics news updates via Facebook, Twitter and the First Draft newsletter.";
		*/
		return articleA;
	}

	public boolean isNYT(String newsLink) {
		String url = "http://www.nytimes.com";
		return (newsLink.length() >= url.length() && newsLink.substring(0, url.length()).equals(url));
	}

	private String merge(String articleA, String articleB) {
    	int maxCount = 0;
		int maxIndex = 0;
		//runs through possible starting indexes of articleB
		for (int i = 0; i < articleB.length(); i++) {
			//counts how many characters are equal
			int count = 0;
			int max = articleA.length();
			if (articleB.length()-i < max)
				max = articleB.length()-i;
			for (int j = 0; j < max; j++) {
				if (articleA.charAt(j) == articleB.charAt(i+j)) {
					count++;
					if (count > maxCount) {
						maxCount = count;
						maxIndex = i;
					}
				}
				else {
					break;
				}
			}
		}
		return articleB.substring(maxIndex);
	}
    private String removeJunk(String article) {
    	
    	return null;
    }

	public ArrayList<Article> getArticles() {
        return newsArticles;
    }

    public Article getArticle(int i) {
        if (newsArticles.size() <= i) {
            return null;
        } else {
            return newsArticles.get(i);
        }
    }

    private static boolean internetIsAvailable() {
        try {
            final URL url = new URL("http://www.cnn.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}
