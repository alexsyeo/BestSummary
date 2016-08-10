
package com.gigstudios.newssummary;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class Sentence {

    private double points;
    private int numWords;
    private int indexInArticle;
    private List<Word> words;
    private String text;
    private static String[] badList = {"cnn", "caption", "photo", "images", "email", "espn", "facebook", "twitter", "pinterest", "whatsapp", "linkedin", "related"};
    private static String[] doubleBadList = {"read more", "see also", "learn more"};
    private static double NOUN_WEIGHT = 2;
    private static double PROPER_NOUN_WEIGHT = 4;
    private static double QUOTATION_WEIGHT = 0.5;
    private static double VERB_WEIGHT = 1.5;
    private static double PRESENT_VERB_WEIGHT = 0.25;
    private static double ADJECTIVE_WEIGHT = 1;
    private static double INDEX_WEIGHT = 2;
    private static double LENGTH_WEIGHT = 2;
    private static double BAD_WEIGHT = 0.01;
    private static double NUMBER_WEIGHT = 10;

    //constructor, creates a sentence that is split up by spaces
    public Sentence(String s, POSModel model) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '[') {
                for (int k = i; k < s.length(); k++) {
                    if (s.charAt(k) == ']') {
                        if (k+1 != s.length()) {
                            s = s.substring(0, i) + s.substring(k+1);
                        }
                        else
                            s = s.substring(0, i);
                    }
                }
            }
            else if (s.charAt(i) == '(') {
                for (int k = i; k < s.length(); k++) {
                    if (s.charAt(k) == ')') {
                        if (k+1 != s.length()) {
                            s = s.substring(0, i) + s.substring(k+1);
                        }
                        else
                            s = s.substring(0, i);
                    }
                }
            }

        }
        text = s;
        String OK = "abcdefghijklmnopqrstuvwxyz' ";
        for (int i = 0; i < s.length(); i++) {
            boolean isOK = false;
            for (int j = 0; j < OK.length(); j++) {
                if (Character.toLowerCase(s.charAt(i)) == OK.charAt(j))
                    isOK = true;
                if (isOK)
                    break;
            }
            if (!isOK) {
                s = s.substring(0, i) + s.substring(i + 1);
                i--;
            }
        }
        words = new ArrayList<Word>();
        if (model != null) {
            POSTaggerME tagger = new POSTaggerME(model);
            String[] temp = s.split(" ");
            //array of parts of speech corresponding to each word in the string
            String[] POS = tagger.tag(temp);
            for (int i = 0; i < temp.length; i++) {
                words.add(new Word(temp[i], POS[i]));
            }
            this.numWords = temp.length;
        }
    }

    //sets the score of the sentence
    public boolean scoreSentence(Article article) {
        if(numWords > 0) {
            double x;
            //Creates an initial point value based on the words in the sentences
            //Takes into account instances of each word and their part of speech
            this.points = instancePoints();
            //changes the score based on the location of the sentence within the article
            //INDEX_WEIGHT changes the possible interval of the multiplier
            x = (article.getNumberOfSentences() - this.indexInArticle) / article.getNumberOfSentences();
            this.points *= (x / INDEX_WEIGHT + (1 - 1 / INDEX_WEIGHT));
            //changes the score based on the length of the sentence
            //LENGTH_WEIGHT changes the possible interval of the multiplier
            x = 1 / numWords;

            //checks to see if there are present tense verbs in the sentence
            this.points *= (x / LENGTH_WEIGHT + (1 - 1 / LENGTH_WEIGHT));
            if (this.checkPresentTense())
                this.points *= PRESENT_VERB_WEIGHT;
            //checks to see if any of the bad cases are true for the particular sentence
            if (this.checkBadList() || this.checkBadWords() || this.checkDoubleBadList() || this.checkFirstWord() || !this.checkHasVerb() || this.checkQuotation())
                this.points *= BAD_WEIGHT;
            //checks to see if there are any numbers in the sentence
            if (this.checkNumber() != 0) {
                x = 1/this.checkNumber();
                this.points *= (x / NUMBER_WEIGHT + (1 - 1 / NUMBER_WEIGHT));
            }
        }else{
            //no words in sentence
            this.points = 0;
        }
        return true;
    }

    public double instancePoints() {
        double count = 0;
        for (int i = 0; i < words.size(); i++) {
            double temp = words.get(i).getInstances() * 100;
            String posTemp = words.get(i).getPartOfSpeech();

            //proper nouns
            if (posTemp.equals("NNP") || posTemp.equals("NNPS"))
                temp *= PROPER_NOUN_WEIGHT;
            //nouns
            if (posTemp.equals("NN") || posTemp.equals("NNS"))
                temp *= NOUN_WEIGHT;
            //other types of verbs
            if (posTemp.equals("VB") || posTemp.equals("VBD") || posTemp.equals("VBG") || posTemp.equals("VBN"))
                temp *= VERB_WEIGHT;
            //adjectives
            if (posTemp.equals("JJ") || posTemp.equals("JJR") || posTemp.equals("JJS"))
                temp *= ADJECTIVE_WEIGHT;
                //sets the word equal to zero if the word is a coordinating conjunction, subordinating conjunction, preposition, determiner, or adverb
            else if (posTemp.equals("CC") || posTemp.equals("IN") || posTemp.equals("DT") || posTemp.equals("RB"))
                temp = 0;

            count += temp;
        }
        if (this.containsString("\""))
            count *= QUOTATION_WEIGHT;
        return count;
    }

    //checks to see if there is a "bad" word in the sentence
    public boolean checkBadWords() {
        if (words.size() >= 6) {
            for (int i = 0; i < 6; i++) {
                String sub = words.get(i).getPartOfSpeech();
                //checks to see if the first six words in a sentence contain a pronoun or a personal pronoun
                if (sub.equals("WP") || (sub.length() > 2 && (sub.equals("WP$") || sub.equals("PRP"))))
                    return true;
            }
        }
        return false;
    }

    //checks to see if there is unnecessary info in the sentence
    public boolean checkBadList() {
        //goes through badList
        for (int i = 0; i < words.size(); i++) {
            for (int k = 0; k < badList.length; k++) {
                if (words.get(i).getWord().toLowerCase().equals(badList[k]))
                    return true;
            }
        }
        return false;
    }
    public boolean checkDoubleBadList() {
        //goes through doubleBadList
        for (int i = 0; i < doubleBadList.length; i++) {
            if (text.contains(doubleBadList[i]))
                return true;
        }
        return false;
    }
    //checks to see if the first word in the sentence is a conjunction
    public boolean checkFirstWord() {
        String sub = "";
        if(words.size() != 0) {
            if(words.get(0).getWord().length() != 0) {
                sub = words.get(0).getPartOfSpeech();
            }else{
                return false;
            }
        }
        return sub.equals("CC") || sub.equals("IN") || sub.equals("WRB") || sub.equals("RB") || sub.equals("PRP") || sub.equals("PRP$");
    }

    //checks to see if there is a present tense verb in the sentence
    public boolean checkPresentTense() {
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getPartOfSpeech().equals("VBP") || words.get(i).getPartOfSpeech().equals("VBZ"))
                return true;
        }
        return false;
    }

    //check to see if there is a verb in the sentence
    public boolean checkHasVerb() {
        String posTemp;
        for (int i = 0; i < words.size(); i++) {
            posTemp = words.get(i).getPartOfSpeech();
            if (posTemp.equals("VB") || posTemp.equals("VBD") || posTemp.equals("VBG") || posTemp.equals("VBN") || posTemp.equals("VBP") || posTemp.equals("VBZ"))
                return true;
        }
        return false;
    }

    //checks to see if there is only one quotation mark in the sentence
    public boolean checkQuotation() {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\"') {
                count++;
                //System.out.println(text.charAt(i) + text.charAt(i-1));
            }
        }
        if (count % 2 != 0)
            return true;
        return false;
    }

    public int checkNumber() {
        int count = 0;
        String number = "012345689";
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < number.length(); j++) {
                if (text.charAt(i) == number.charAt(j))
                    count++;
            }
        }
        return count;
    }


    public boolean containsString(String s) {
        return this.toString().contains(s);
    }

    //getters and setters for number of words in the sentence
    public int getNumWords() {
        return this.numWords;
    }

    public void setNumWords(int i) {
        this.numWords = i;
    }


    //getters and setters for specific Words
    public Word getWord(int index) {
        return words.get(index);
    }

    public boolean setWord(int index, Word element) {
        words.set(index, element);
        return true;
    }

    //getters and setters for the list of words
    public List<Word> getWords() {
        return words;
    }


    //getters and setters for points
    public double getPoints() {
        return this.points;
    }

    public void setPoints(double i) {
        this.points = i;
    }

    public void setIndexInArticle(int i) {
        this.indexInArticle = i;
    }

    public int getIndexInArticle() {
        return this.indexInArticle;
    }

    //to-string method
    public String toString() {
        return text;
    }

    public String getInfo() {
        return "Index:\t" + this.indexInArticle + "\tNumberOfWords:\t" + this.numWords + "\tPoints:\t" + this.points;
    }
    public String getWordInfo() {
        String ret = "";
        for (int i = 0; i < words.size(); i++) {
            ret += this.words.get(i).getWord() + "[" + this.words.get(i).getPartOfSpeech() + "] ";
        }
        return ret;
    }
}