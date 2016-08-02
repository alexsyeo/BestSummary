import java.util.ArrayList;
import java.util.List;

public class SentenceGenome {
	double[] vecWeights;
	double fitness;
	
	public SentenceGenome(double[] weights, double fit){
		this.vecWeights = weights;
		this.fitness = fit;
	}
	
	public SentenceGenome(double[] weights){
		this.vecWeights = weights;
		this.fitness = -1;
	}
	
	public boolean compare(SentenceGenome lhs, SentenceGenome rhs){
		return lhs.fitness < rhs.fitness;
	}
	
	public List<SentenceGenome> haveChildren(SentenceGenome other, int numChildren, double mutationRate){
		List<SentenceGenome> ret = new ArrayList<SentenceGenome>();
		double[] weights = new double[this.vecWeights.length];
		for (int i=0; i<this.vecWeights.length; i++){
			weights[i] = 0.5 * (this.vecWeights[i] + other.getWeights()[i]);
		}
		
		for (int i=0; i<numChildren; i++){
			weights = weights.clone();
			SentenceGenome temp = new SentenceGenome(weights);
			temp.Mutate(mutationRate);
			ret.add(temp);
		}
		
		return ret;
	}

	private boolean Mutate(double mutationRate){
		for (int i = 0; i < this.vecWeights.length; i++){
			if(Math.random()<mutationRate){
				this.vecWeights[i] += (Math.random()-0.5)/10;
			}
		}
		return true;
	}
	
	public void setWeights(double[] weights){
		this.vecWeights = weights;
	}
	public double[] getWeights(){
		return this.vecWeights;
	}
	
	public void setFitness(double fit){
		this.fitness = fit;
	}
	public double getFitness(){
		return this.fitness;
	}
	
	public String toString(){
		String r = "[";
		for(int i=0;i<this.vecWeights.length;i++){
			r += this.vecWeights[i] + ", ";
		}
		r = r.substring(0,r.length()-2);
		r += "]";
		return r;
	}
	
}
