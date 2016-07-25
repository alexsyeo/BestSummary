import java.util.ArrayList;
//HI!
public class WordCounter {
    String article;
    public WordCounter(String a) {
        article = a;
    }
    public String countWords() {
    //Removing Punctuation:
        String cleanArticle = article;
        String OK = "abcdefghijklmnopqrstuvwxyz ";
        for (int i = 0; i < cleanArticle.length(); i++) {
            for (int j = 0; j < OK.length(); j++) {
                if (cleanArticle.charAt(i) == OK.charAt(j))
                    break;
            }
        }
    /*Creating String array where wach word appears once
    Creating a corresponding Integer array that counts how many times each word appears*/
        String[] words  = cleanArticle.split(" ");
        ArrayList<String> wordList = new ArrayList<String>();
        for (int i = 0; i < words.length; i++) {
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
        String ret = "";
        for (int i = 0; i < commonWords.length; i++) {
            ret += wordCount[i] + "\t" + commonWords[i] + "\n";
        }
        return ret;
    }
}
