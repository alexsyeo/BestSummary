package com.gigstudios.newssummary;

import android.content.Context;

import org.xml.sax.XMLReader;

import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class WordCounter {
    String article;
    Sentence[] sentences;

    Context context;

    public WordCounter(String a, Context context) {
        article = a;
        this.context = context;
    }

    public Sentence[] makeSentences() {

        //initializes sentence detector
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(MainActivity.getSentenceModel());
        String[] sentencesTemp = sentenceDetector.sentDetect(article);
        
        List<String> sentencesTempTemp = new ArrayList<String>();
    	for(String s:sentencesTemp){
    		sentencesTempTemp.add(s);
    	}
    	
    	for(int i=0;i<sentencesTempTemp.size();i++){
    		if(sentencesTempTemp.get(i).contains("\n")){
    			String[] temp = sentencesTempTemp.get(i).split("\n");
    			sentencesTempTemp.remove(i);
    			for (int j = temp.length-1; j >= 0; j--) {
    				sentencesTempTemp.add(i, temp[j]);
    				i++;
    			}
    		}
    	}

        sentences = new Sentence[sentencesTempTemp.size()];


        POSModel pos = MainActivity.getPOSModel();


        for (int i = 0; i < sentencesTemp.length; i++) {
            sentences[i] = new Sentence(sentencesTempTemp.get(i), pos);
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
