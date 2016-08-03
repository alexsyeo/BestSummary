import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestMain {
	public static void main(String[] args){
		Scanner kb = new Scanner(System.in);
		
		double[] testGenome1 = {0.4, 0.1, 0.6, 0.5};
		double[] testGenome2 = {0.98, 0.72, 0.23, 0.44};
		double[] testGenome3 = {0.1, 0.73, 0.3, 0.88};
		double[] testGenome4 = {0.56, 0.463, 0.26, 0.455};
		
		SentenceGenome s1 = new SentenceGenome(testGenome1);
		SentenceGenome s2 = new SentenceGenome(testGenome2);
		SentenceGenome s3 = new SentenceGenome(testGenome3);
		SentenceGenome s4 = new SentenceGenome(testGenome4);
		
		
		List<SentenceGenome> genomes = new ArrayList<SentenceGenome>();
		
		
		genomes.add(s1);
		genomes.add(s2);
		genomes.add(s3);
		genomes.add(s4);
		
		SentenceGenAlg Algorithm = new SentenceGenAlg(genomes);
		
		for (int i=0; i<5; i++){
			for(SentenceGenome sG:Algorithm.getPop()){
				System.out.println(sG);
				sG.setFitness(Double.parseDouble(kb.nextLine()));
			}
			Algorithm.Update();
		}
		kb.close();
	}
}
