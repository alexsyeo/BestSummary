public class DriverTest {
    public static void main (String[] args) {
        WordCounter count = new WordCounter("Hello Hello This is is a test");
        System.out.println(count.countWords());
    }
}