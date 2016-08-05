package com.gigstudios.newssummary;

import android.content.Context;

public class Article {

    private Sentence[] sentences;
    private int numSentencesInSummary;
    private String text;
    private Sentence[] bestSentences;
    private String url;
    private String title;
    private Context context;

    public Article(String s, int numSentences, Context context) {
        this.context = context;
        this.numSentencesInSummary = numSentences;
        WordCounter counter = new WordCounter(s, context);
        sentences = counter.makeSentences();
        this.text = s;

        for (int i = 0; i < sentences.length; i++) {
            sentences[i].scoreSentence(this);
        }

        //find best sentences for the summary
        bestSentences = findBestSentences();
    }

    private Sentence[] findBestSentences() {
        Sentence[] top = new Sentence[numSentencesInSummary];

        // initialize top array with blank sentences
        for (int i = 0; i < top.length; i++) {
            top[i] = new Sentence("", null);
        }

        // get best sentences
        for (int i = 0; i < top.length; i++) {
            for (Sentence sentence : sentences) {
                if (sentence.getPoints() > top[i].getPoints()) {
                    boolean isGood = true;
                    for (int j = 0; j < top.length; j++) {
                        if (top[j].equals(sentence))
                            isGood = false;
                    }
                    if (isGood) {
                        top[i] = sentence;
                    }
                }
            }
        }

        // sort in order of appearance in article
        for (int i = 0; i < top.length; i++) {
            for (int j = i; j < top.length; j++) {
                if (top[j].getIndexInArticle() < top[i].getIndexInArticle()) {
                    Sentence hold = top[i];
                    top[i] = top[j];
                    top[j] = hold;
                }
            }
        }

        return top;

    }

    public String getSummary() {
		String ret = "";
		for (int i = 0; i < bestSentences.length; i++) {
            ret += (bestSentences[i] + " ");
		}
		return ret;
	}

    public Sentence getSentence(int i) {
        return this.sentences[i];
    }

    public Sentence[] getBestSentences() {
        return bestSentences;
    }

    public String getText() {
        return text;
    }

    public void setText(String s) {
        text = s;
    }

    public String toString() {
        return text;
    }

    public int getNumberOfSentences() {
        return sentences.length;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String printInfo() {
        String ret = "";
        //prints the info of each sentence
        for (int i = 0; i < bestSentences.length; i++) {
            ret += (bestSentences[i].getInfo() + "\n");
        }
        ret += "\n";
        return ret;
    }
}
