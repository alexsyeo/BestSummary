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
        SentenceModel model = null;
        InputStream modelIn = null;

        try {
            modelIn = context.getResources().openRawResource(R.raw.en_sent);
            model = new SentenceModel(modelIn);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                //Error
            }
        }

        //initializes sentence detector
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        String[] sentencesTemp = sentenceDetector.sentDetect(article);

        sentences = new Sentence[sentencesTemp.length];


        POSModel pos = MainActivity.posModel;


        for (int i = 0; i < sentencesTemp.length; i++) {
            sentences[i] = new Sentence(sentencesTemp[i], pos);
            //I added the line of code below to set the location of each sentence in the article
            sentences[i].setIndexInArticle(i);
        }
        setWordNumbers();

        return sentences;
    }

    public String ReadFromfile(String fileName, Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
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
