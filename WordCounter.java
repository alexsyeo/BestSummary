package bestsummarydevelopment;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WordCounter {
    String article;
    Sentence[] sentences;

    public WordCounter(String a) {
        article = a;
    }
    public Sentence[] makeSentences() {
    	SentenceModel model = null;
    	InputStream modelIn = null;
    	try {
    		//we should put all of our file paths in comments here
    		//Alex: "/Users/alex/BestSummary/bestsummarydevelopment/en-sent.bin"
    		//William:
    		//Sean:
    		//Jared:
    		
    	  modelIn = new FileInputStream("/Users/alex/BestSummary/bestsummarydevelopment/en-sent.bin");
    	  model = new SentenceModel(modelIn);
    	}
    	catch (IOException e) {
    	  e.printStackTrace();
    	}
    	finally {
    	  if (modelIn != null) {
    	    try {
    	      modelIn.close();
    	    }
    	    catch (IOException e) {
    	    }
    	  }
    	}
    	
    	//initializes sentence detector
    	SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

    	String[] sentencesTemp = sentenceDetector.sentDetect(article);
    	
    	sentences = new Sentence[sentencesTemp.length];
    	for (int i = 0; i < sentencesTemp.length; i++) {
    		sentences[i] = new Sentence(sentencesTemp[i]);
    		//I added the line of code below to set the location of each sentence in the article
    		sentences[i].setIndexInArticle(i);
    	}
    	setWordNumbers();
    	return sentences;
    }
    
    public void setWordNumbers() {
    	//Runs through all of the sentences
    	for (int i = 0; i < sentences.length; i++) {
    		//Runs through all of the words in the current sentence
    		for (int j = 0; j < sentences[i].getNumWords(); j++) {
    			//Sets a variable to count the instances of the current word
    			int count = 0;
    			//Runs through all of the sentences
    			for (int k = 0; k < sentences.length; k++) {
    	    		//Runs through all of the words in the current sentence
    				for (int l = 0; l < sentences[k].getNumWords(); l++) {
    					//Tests if the first word is equal to the second word
    					if (sentences[i].getWord(j).isEqualTo(sentences[k].getWord(l)))
    						count++; //If the words are equal, increase the count variable
    				}
    			}
    			//Set the instance variable for the word equal to the count variable
    			sentences[i].getWord(j).setInstances(count);
    		}
    	}
    }
}
