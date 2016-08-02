package bestsummarydevelopment;
import java.util.ArrayList;
import java.util.List;


public class SentenceGenAlg {
	private List<SentenceGenome> population;
	
	private int popSize;
	private int chromosomeLength;
	
	private double totalFitness;
	private double bestFitness;
	private double averageFitness;
	private double worstFitness;
	
	private int fittestGenome;
	
	private double mutationRate = 0.2;
	private int numChildrenPerGeneration;
	private double crossoverRate = 0.7; //just a temporary value, works okay
	
	private int generation;
	
	public SentenceGenAlg(List<SentenceGenome> organisms, int num){
		this.population = organisms;
		this.popSize = organisms.size();
		this.numChildrenPerGeneration = num;
	}
	
	//private SentenceGenome GetChromoRoulette(){}

	private List<SentenceGenome> GrabNBest(int NBest){
		SentenceGenome[] bestGenomes = new SentenceGenome[NBest];
		
		for (int q=0; q<bestGenomes.length; q++) {
			bestGenomes[q] = new SentenceGenome(null);
		}
		
		for (int i = 0; i < bestGenomes.length; i++) {
			for (SentenceGenome sG : this.population) {
				if (sG.getFitness() > bestGenomes[i].getFitness()) {
					boolean isGood = true;
					for (int j = 0; j < bestGenomes.length; j++) {
						if (bestGenomes[j].equals(sG))
							isGood = false;
					}
					if (isGood) {
						bestGenomes[i] = sG;
					}
				}
			}
		}
		
		List<SentenceGenome> ret = new ArrayList<SentenceGenome>();
		for(int index=0;index<bestGenomes.length;index++){
			ret.add(bestGenomes[index]);
		}
		
		return ret;
	}
	
	public void Update(){
		List<SentenceGenome> parents = this.GrabNBest(2);
		List<SentenceGenome> children = parents.get(0).haveChildren(parents.get(1), this.numChildrenPerGeneration, this.mutationRate);
		this.population = children;
	}
	
	public List<SentenceGenome> getPop(){
		return this.population;
	}

	//private void CalculateBestWorstAvTot();

	//private void Reset();
	
	
	
	
	
}
