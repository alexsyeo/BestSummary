package bestsummarydevelopment;

import java.util.ArrayList;
import java.util.List;

public class SentenceGenome {
	double[] weights;
	double fitness;
	
	//constructors
	public SentenceGenome(double[] weights, double fit){
		this.weights = weights;
		this.fitness = fit;
	}
	
	public SentenceGenome(double[] weights){
		this.weights = weights;
		this.fitness = -1;
	}
	
	public boolean compare(SentenceGenome lhs, SentenceGenome rhs){
		return lhs.fitness < rhs.fitness;
	}
	
	//switches a random number of weights between two chromosomes/organisms
	public List<SentenceGenome> Crossover(SentenceGenome other){
		for(int i=0;i<(int)(Math.random()*this.weights.length);i++){
			double temp = this.weights[i];
			this.weights[i] = other.getWeights()[i];
			other.getWeights()[i] = temp;
		}
		List<SentenceGenome> ret = new ArrayList<SentenceGenome>();
		ret.add(this);
		ret.add(other);
		return ret;
	}
	
	//Takes two SentenceGenome objects and creates a specified number of children (numChildren)
	//by averaging the weights of the two parents. In this method, the genomes of the parents first 
	//undergo crossover before being averaged. In addition, when each child is created its genes
	//may be randomly changed due to mutation.
	public List<SentenceGenome> haveChildren(SentenceGenome other, int numChildren, double mutationRate){
		List<SentenceGenome> ret = new ArrayList<SentenceGenome>();
		//crossover
		List<SentenceGenome> genomesAfterCrossover = this.Crossover(other);
		SentenceGenome genome1 = genomesAfterCrossover.get(0);
		SentenceGenome genome2 = genomesAfterCrossover.get(1);
		double[] w = new double[genome1.getWeights().length];
		//averages parents' weights
		for (int i=0; i<genome1.getWeights().length; i++){
			w[i] = 0.5 * (genome1.getWeights()[i] + genome2.getWeights()[i]);
		}
		
		//mutates children randomly
		for (int i=0; i<numChildren; i++){
			w = w.clone();
			SentenceGenome temp = new SentenceGenome(w);
			temp.Mutate(mutationRate);
			ret.add(temp);
		}
		
		return ret;
	}
	
	//Increases or decreases each weight by a random amount, if a random variable is less than the mutation rate.
	private boolean Mutate(double mutationRate){
		for (int i = 0; i < this.weights.length; i++){
			if(Math.random()<mutationRate){
				//the maximum change that a weight can go through is +-0.5
				this.weights[i] += (Math.random()-0.5);
			}
		}
		return true;
	}
	
	//getters, setters, and toString() method
	public void setWeights(double[] weights){
		this.weights = weights;
	}
	public double[] getWeights(){
		return this.weights;
	}
	
	public void setFitness(double fit){
		this.fitness = fit;
	}
	public double getFitness(){
		return this.fitness;
	}
	
	public String toString(){
		String r = "[";
		for(int i=0;i<this.weights.length;i++){
			r += this.weights[i] + ", ";
		}
		r = r.substring(0,r.length()-2);
		r += "]";
		return r;
	}
	
}
