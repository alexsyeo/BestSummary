package com.gigstudios.newssummary;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class Article {

    private Sentence[] sentences;
    private int numSentencesInSummary;
    private String text;
    private Sentence[] bestSentences;
    private String url;
    private String title;
    private Context context;
    private static double COS_WEIGHT = 2;

    public Article(String s, int numSentences, Context context, String t) {
        this.title = t;
        this.context = context;
        this.numSentencesInSummary = numSentences;
        WordCounter counter = new WordCounter(s, context);
        this.sentences = counter.makeSentences(this.title);
        this.text = s;

        for (int i = 0; i < sentences.length; i++) {
            sentences[i].scoreSentence(this);
        }

        //GENERAL IDEA: compares each sentence to the title of the article and changes the score based on the similarity to the title

        //the title of the article in the form of a Sentence
        Sentence articleTitle = counter.getTitle();

        //the universal list that will be used throughout the entire phase of comparisons
        List<Word> universalList = new ArrayList<Word>();

        //just for convenience
        List<Word> sub = articleTitle.getWords();

        //adds the words from the title to the universalList
        for (int i = 0; i < sub.size(); i++) {
            boolean contains = false;
            for (int j = 0; j < universalList.size(); j++) {
                if (sub.get(i).getWord().equals(universalList.get(j).getWord()))
                    contains = true;
            }
            if (!contains)
                universalList.add(sub.get(i));
        }

        //creates the array of values for the title that contains the instances of each word within the universal list
        double[] titleInstances = new double[universalList.size()];

        //goes through each word in the universalList and counts the amount of instances in the entire title

        //check to see if the -1 is okay
        for (int i = 0; i < universalList.size(); i++) {
            double count = 0;
            for (int j = 0; j < sub.size(); j++) {
                if ((universalList.get(i).getWord()).equals((sub.get(j)).getWord()))
                    count++;
            }
            titleInstances[i] = count;
        }



        //goes through the list of sentences in the article and compares each sentence to the article title


        //goes through the universalList
        for (int i = 0; i < sentences.length; i++) {
            double[] sentenceInstances = new double[universalList.size()];
            for (int j = 0; j < universalList.size(); j++) {
                //goes through each sentence
                double count = 0;
                //goes through each word in the sentence
                for (int k = 0; k < sentences[i].getWords().size(); k++) {
                    //checks to see if the word in the universalList is the same as the word in the sentence
                    if (universalList.get(j).getWord().equals(sentences[i].getWord(k).getWord())) {
                        count++;
                    }
                }
                sentenceInstances[j] = count;
            }
            //changes the points of the sentence based on the similarity between the sentence and the title
            double x = cosineSimilarity(titleInstances, sentenceInstances);
            System.out.println(x);
            sentences[i].setPoints(sentences[i].getPoints() * (x / COS_WEIGHT + (1 - 1 / COS_WEIGHT)));
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
        String summary = "";
        for (int i = 0; i < numSentencesInSummary; i++) {
            summary += bestSentences[i] + " ";
        }

        return summary;
    }

    //compares two different arrays of values
    //will be used to compare each sentence to the title of the article
    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        System.out.print("VectorA:\t");
        for (int i = 0; i < vectorA.length; i++) {
            System.out.print(vectorA[i] + ", ");
        }
        System.out.print("VectorB:\t");
        for (int i = 0; i < vectorB.length; i++) {
            System.out.print(vectorB[i] + ", ");
        }
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        if ((Math.sqrt(normA) * Math.sqrt(normB)) == 0)
            return 0;
        else
            return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
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

    public int getCharLength() {
        int length = 0;
        for (Sentence sentence : sentences) {
            length += sentence.toString().length();
        }
        return length;
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

    public String printSummary() {
        String ret = "";
        //print best sentences
        ret += ("---------------------------------------\n");
        ret += ("URL:\t" + url + "\n");
        ret += ("Title:\t" + title + "\n");
        for (int i = 0; i < bestSentences.length; i++) {
            ret += (bestSentences[i] + "\n");
        }
        return ret;
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
