import java.util.Arrays;
import java.util.Random;

class Chromosome {
    public char[] genes;
    public float fitness;
    public float proportionateFitness;
    public float cumulativeProbability;

    public Chromosome(int n){
        this.genes = new char[n];
        for(int i = 0; i < this.genes.length; i++){
            this.genes[i] = Math.random() > 0.5 ? '1' : '0';
        }
    }

    public void fitness(){
        int f = 0;
        for(int i = 0; i < genes.length; i++){
            if(genes[i] == '1')
                f++;
        }
        this.fitness = f;
    }

    public void proportionateFitness(float sumF){
        this.proportionateFitness = this.fitness / sumF;
    }

    public Chromosome clone(){
        Chromosome c = new Chromosome(genes.length);
        c.genes = genes;
        c.fitness = fitness;
        c.proportionateFitness = proportionateFitness;
        c.cumulativeProbability = cumulativeProbability;
        return c;
    }

    @Override
    public String toString() {
        return "Chromosome{" +
                "genes=" + Arrays.toString(genes) +
                ", fitness=" + fitness +
                ", proportionateFitness=" + proportionateFitness +
                ", cumulativeProbability=" + cumulativeProbability +
                '}';
    }
}


class Population {
    public float mutationRate;
    public float crossoverRate;
    public Chromosome[] population;
    public int generations;
    public String target;
    public boolean finished;
    public Chromosome solution;

    public Population(float mutationRate, float crossoverRate, String target, int maxPopulation) {
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.population = new Chromosome[maxPopulation];
        for (int i = 0; i < population.length; i++){
            this.population[i] = new Chromosome(target.length());
        }

        this.generations = 0;
        this.target = target;
        this.finished = false;
    }

    public void selectParents(){
        Chromosome[] matingPool = new Chromosome[population.length];

        // 6. Repeat steps 4-5 n times to create n candidates in the mating pool
        for(int i = 0; i < population.length; i++){
            matingPool[i] = rouletteWheel(population);
        }


        Chromosome[] matingPoolAfterCrossover = new Chromosome[population.length];

        // Crossover, we consider each individual
        for(int i = 0; i < matingPool.length; i++){
            double r = Math.random();
            if( r < crossoverRate){
                // Apply crossover
                matingPoolAfterCrossover[i] = crossover(matingPool[i], rouletteWheel(matingPool));
            }else {
                // Add it unaffected into the next population
                matingPoolAfterCrossover[i] = matingPool[i].clone();
            }
        }


        Chromosome[] newPopulation = new Chromosome[population.length];

        // Mutation, we consider each individual
        for(int i = 0; i < matingPoolAfterCrossover.length; i++){
            double r = Math.random();
            if( r < mutationRate){
                newPopulation[i] = mutate(matingPoolAfterCrossover[i]);
            }else{
                newPopulation[i] = matingPoolAfterCrossover[i].clone();
            }
        }

        calc();
        generations++;
        isFinished();
        System.out.println("=========================== GENERATION " + generations + " ======================");
        for(int i = 0; i < population.length; i++){
            System.out.println(population[i]);
        }
        System.out.println("total fitness: " + sumFitness(population) + "\n\n\n");
        population = newPopulation;
    }

    public void isFinished(){
        for(int i = 0; i < population.length; i++){
            if(Arrays.equals(population[i].genes, "1111111111".toCharArray())){
                solution = population[i];
                finished = true;
                break;
            }
        }
    }

    public Chromosome mutate(Chromosome a){
        Chromosome x = a.clone();
        Random ran = new Random();
        int max = x.genes.length - 1;
        int min = 0;
        int r = ran.nextInt(max - min) + min;
        if(x.genes[r] == '1'){
            x.genes[r] = '0';
        }else {
            x.genes[r] = '1';
        }
        return x;
    }

    public Chromosome crossover(Chromosome a, Chromosome b){
        Random ran = new Random();
        int max = a.genes.length - 1;
        int min = 0;
        int r = ran.nextInt(max - min) + min;

        //int r = a.genes.length / 2;
        char[] c1 = Arrays.copyOfRange(a.genes, 0, r);
        char[] c2 = Arrays.copyOfRange(b.genes, r, b.genes.length);
        char[] c3 = new char[a.genes.length];
        System.arraycopy(c1, 0, c3, 0, c1.length);
        System.arraycopy(c2, 0, c3, c1.length, c2.length);
        Chromosome c = new Chromosome(target.length());
        c.genes = c3;
        return c;
    }

    public void calc(){
        // 1. Evaluate the fitness of each individual in the population
        for(int i = 0; i < population.length; i++){
            population[i].fitness();
        }

        // 2. Compute the probability of selecting each member of the population
        float sumF = sumFitness(population);
        for(int i = 0; i < population.length; i++){
            population[i].proportionateFitness(sumF);
        }

        // 3. Calculate the cumulative probability for each individual
        for(int i = 0; i < population.length; i++){
            if(i == 0){
                population[i].cumulativeProbability = population[i].proportionateFitness;
            }else {
                population[i].cumulativeProbability = population[i - 1].cumulativeProbability + population[i].proportionateFitness;
            }
        }
    }

    public Chromosome rouletteWheel(Chromosome[] p){
        // 1. Evaluate the fitness of each individual in the population
        for(int i = 0; i < p.length; i++){
            p[i].fitness();
        }

        // 2. Compute the probability of selecting each member of the population
        float sumF = sumFitness(p);
        for(int i = 0; i < p.length; i++){
            p[i].proportionateFitness(sumF);
        }

        // 3. Calculate the cumulative probability for each individual
        for(int i = 0; i < p.length; i++){
            if(i == 0){
                p[i].cumulativeProbability = population[i].proportionateFitness;
            }else {
                p[i].cumulativeProbability = population[i - 1].cumulativeProbability + population[i].proportionateFitness;
            }
        }

        Chromosome c = null;
        // 4. Generate a uniform random number (0, 1]
        double r = Math.random();

        // 5. if r < q1, then select the first chromosome
        if(r < p[0].cumulativeProbability){
            c = p[0].clone();
        } else {
            // 5. Else, select the individual xi, such that q1 - 1 < r <= qi
            for(int j = 1; j < p.length; j++){
                if(r >= p[j].cumulativeProbability){
                }else {
                    c = p[j].clone();
                    break;
                }
            }
        }
        return c;
    }

    public float sumFitness(Chromosome[] p){
        float f = 0f;
        for (int i = 0; i < p.length; i++){
            f += p[i].fitness;
        }
        return f;
    }
}

public class GA_MaxOnes {
    public static void main(String args[]){
        Population population = new Population(0.1f, 0.6f, "1111111111", 10);
        while(!population.finished){
            population.selectParents();
        }

        System.out.println("\n\nTotal Generations: " + population.generations);
        System.out.println("Optimal solution: " + population.solution);
    }
}
