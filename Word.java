public class Word {
    private String partOfSpeech;
    private String word;
    private int instances;
    
    public Word() {}
    
    public Word(String w, String pos) {
        this.word = w;
        this.instances = -1;
        this.partOfSpeech = pos;
    }
    
    public boolean equals(Word other){
    	return this.word.equals(other.word) && this.partOfSpeech.equals(other.partOfSpeech);
    }
    
    public String getPartOfSpeech() {
        return partOfSpeech;
    }
    public void setPartOfSpeech(String i) {
        this.partOfSpeech = i;
    }
    
    
    public String getWord() {
        return word;
    }
    public void setWord(String i) {
        this.word = i;
    }
    
    
    public int getInstances() {
        return instances;
    }
    public void setInstances(int i) {
        this.instances = i;
    }
    
    public String toString() {
        return word;
    }
}