import java.util.*;

<<<<<<< HEAD
=======

//testing this out
>>>>>>> ac81e4bcc961ef521f148322e23b61afdbef9979
public class Animal {
	private int numLegs;
	private int weight;
	private int id;
	private boolean hasOwner = false;
	private String name;
	
	public int numberOfAnimals = 0;
	
	
	
	static Scanner kb = new Scanner(System.in);
	
	public Animal() {
		numberOfAnimals++;
	}
	
	public static void main(String[] args) {
		Dog dog1 = new Dog();
		dog1.setName("Baxter");
		System.out.println(dog1.checkForOwner());
	}

	
	
	
	
	public boolean isHasOwner() {
		return hasOwner;
	}

	public void setHasOwner(boolean hasOwner) {
		this.hasOwner = hasOwner;
	}

	public int getNumLegs() {
		return numLegs;
	}

	public void setNumLegs(int numLegs) {
		this.numLegs = numLegs;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String checkForOwner() {
		System.out.println("Does " + this.getName() + " have an owner? Please type 'yes' or 'no'.");
		if (kb.hasNextLine()) {
			if (kb.nextLine().toLowerCase().equals("yes"))
				this.setHasOwner(true);
		}
		
		if (this.isHasOwner())
			return this.getName() + " has an owner!";
		return this.getName() + " does not have an owner!";
	}
	
}
