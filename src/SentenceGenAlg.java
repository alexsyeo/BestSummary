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
	private int currentIndex = 0;
	
	public SentenceGenAlg(List<SentenceGenome> organisms, int num){
		this.population = organisms;
		this.popSize = organisms.size();
		this.numChildrenPerGeneration = num;
	}
	
	//private SentenceGenome GetChromoRoulette(){}

	//gets the 2 best genomes to be the parents for the next generation
	private List<SentenceGenome> GrabNBest(int NBest){
		SentenceGenome[] bestGenomes = new SentenceGenome[NBest];
		
		for (int q=0; q<bestGenomes.length; q++) {
			bestGenomes[q] = new SentenceGenome(null, -9999999);
		}
		//this is the algorithm
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
	
	//gets the next genome in the population; if there are no more genomes left it runs the "update" method which
	//creates the next generation.
	public SentenceGenome getNext(){
		if(this.currentIndex == this.popSize - 2){
			this.Update();
			this.currentIndex = 0;
		}
		this.currentIndex++;
		return this.population.get(currentIndex-1);
	}
	
	//gets the best 2 genomes out of the current population and creates then next generation from them
	public void Update(){
		List<SentenceGenome> parents = this.GrabNBest(2);
		List<SentenceGenome> children = parents.get(0).haveChildren(parents.get(1), this.numChildrenPerGeneration, this.mutationRate);
		this.population = children;
		this.popSize = children.size();
	}
	
	//getters, setters
	public List<SentenceGenome> getPop(){
		return this.population;
	}
	
	public int getPopSize(){
		return this.popSize;
	}

	//private void CalculateBestWorstAvTot();

	//private void Reset();
	
	
	
	
	
}
