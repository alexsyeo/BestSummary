public class Word {
    String partOfSpeech;
    String word;
    int instances;
    
    public Word() {}
    public Word(String w, int i) {
        word = w;
        instances = i;
        partOfSpeech = "?";
    }
    public String getPartOfSpeech() {
        return partOfSpeech;
    }
    public String getWord() {
        return word;
    }
    public int getInstances() {
        return instances;
    }
    public String toString() {
        return word;
    }
}