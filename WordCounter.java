//package bestsummarydevelopment;

import java.util.ArrayList;

public class WordCounter {
    String article;
    Sentence[] sentences;

    public WordCounter(String a) {
        article = a;
    }
    public Sentence[] makeSentences() {
    	int mark = 0;
    	//Divides article into sentence length Strings and then feeds those strings into the Sentence constructor.
    	ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();
    	for (int i = 0; i < article.length(); i++) {
			if (article.charAt(i) == '.' || article.charAt(i) == '!' || article.charAt(i) == '?') {
				Sentence sentence = new Sentence(article.substring(mark,i + 1));
				sentenceList.add(sentence);
				mark = i + 2;
				//assuming there will always be spaces at the beginning of each sentence. we should change this later
			}
		}
    	sentences = new Sentence[sentenceList.size()];
    	for (int i = 0; i < sentenceList.size(); i++) {
    		sentences[i] = sentenceList.get(i);
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
    /*public Word[] countWords() {
    //Removing Punctuation:
        String cleanArticle = article;
        String OK = "abcdefghijklmnopqrstuvwxyz ";
        for (int i = 0; i < cleanArticle.length(); i++) {
            boolean isOK = false;
            for (int j = 0; j < OK.length(); j++) {
                if (Character.toLowerCase(cleanArticle.charAt(i)) == OK.charAt(j))
                    isOK = true;
                if (isOK)
                    break;
            }
            if (!isOK) {
                cleanArticle = cleanArticle.substring(0,i) + cleanArticle.substring(i+1);
                i--;
            }
        }
    //Creating String array where each word appears once
    //Creating a corresponding Integer array that counts how many times each word appears
        String[] words  = cleanArticle.split(" ");
        ArrayList<String> wordList = new ArrayList<String>();
        for (int i = 0; i < words.length; i++) {
            if (!words[i].equals(""))
                wordList.add(words[i]);
        }
        ArrayList<String> oneWordList = new ArrayList<String>();
        ArrayList<Integer> wordCountList = new ArrayList<Integer>();
        for (int i = 0; i < wordList.size(); i++) {
            oneWordList.add(wordList.get(i));
            wordCountList.add(1);
            for (int j = i+1; j < wordList.size(); j++) {
                if (wordList.get(i).equals(wordList.get(j))) {
                    wordCountList.set(wordCountList.size()-1, wordCountList.get(wordCountList.size()-1)+1);
                    wordList.remove(j);
                    j--;
                }
            }
        }
        String[] commonWords = new String[oneWordList.size()];
        int[] wordCount = new int[oneWordList.size()];
        for (int i = 0; i < commonWords.length; i++) {
            commonWords[i] = oneWordList.get(i);
            wordCount[i] = wordCountList.get(i);
        }
    //Reordering the array of words from most common to least common
        for (int i = 0; i < commonWords.length; i++) {
            for (int j = i+1; j < commonWords.length; j++) {
                if (wordCount[j] > wordCount[i]) {
                    int hold = wordCount[j];
                    wordCount[j] = wordCount[i];
                    wordCount[i] = hold;
                    String holder = commonWords[j];
                    commonWords[j] = commonWords[i];
                    commonWords[i] = holder;
                }
            }
        }
    //Return:
        Word[] ret = new Word[commonWords.length];
        for (int i = 0; i < commonWords.length; i++) {
            ret[i] = new Word(commonWords[i], wordCount[i]);
        }
        return ret;
    }*/
}
