package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a {@link Model} that can be built using multiple {@link Thread}s.
 *
 * @author Steven Fine
 */
public abstract class MultiThreadedModel implements Model {

    protected List<Double> targetValues;
    protected Map<ImmutableList<Double>, TreeNode> processedGeneticMaterial;
    protected Map<ImmutableList<Double>, Double> weights;
    protected final int NUM_THREADS;

    public MultiThreadedModel(List<Double> targetValues, int numThreads) {
        this.targetValues = targetValues;
        processedGeneticMaterial = new HashMap<>();
        weights = new HashMap<>();
        NUM_THREADS = numThreads;
    }

    @Override
    public void buildModel(Population population) {
        processedGeneticMaterial.clear();
        weights.clear();

        List<ModelThread> threads = new ArrayList<>();
        for (int i = 0; i < NUM_THREADS; i++) {
            ModelThread thread = new ModelThread(i, population, NUM_THREADS);
            threads.add(thread);
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            ModelThread thread = threads.get(i);
            thread.start();
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            ModelThread thread = threads.get(i);
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(MultiThreadedModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        for (Individual individual : population) {
            Map<ImmutableList<Double>, TreeNode> individualProcessedGeneticMaterial = individual.getProcessedGeneticMaterial();
            Map<ImmutableList<Double>, Double> individualProcessedWeights = individual.getProcessedWeights();
            for (Map.Entry entry : individualProcessedGeneticMaterial.entrySet()) {
                ImmutableList<Double> semantics =(ImmutableList<Double>) entry.getKey();
                TreeNode syntax = individualProcessedGeneticMaterial.get(semantics);
                double weight = individualProcessedWeights.get(semantics);
                if (processedGeneticMaterial.containsKey(semantics)) {
                    int oldSize = processedGeneticMaterial.get(semantics).getSubtreeSize();
                    int newSize = syntax.getSubtreeSize();
                    if (newSize < oldSize) {
                        processedGeneticMaterial.put(semantics, syntax);
                        weights.put(semantics, weight);
                    }
                } else {
                    processedGeneticMaterial.put(semantics, syntax);
                    weights.put(semantics, weight);
                }
            }
        }
    }

    @Override
    public Map<ImmutableList<Double>, TreeNode> getProcessedGeneticMaterial() {
        return processedGeneticMaterial;
    }

    @Override
    public Map<ImmutableList<Double>, Double> getWeights() {
        return weights;
    }

    @Override
    public double getModelError(Individual individual) {
        return individual.getModelError();
    }

    @Override
    public double getModelComplexity(Individual individual) {
        return individual.getModelComplexity();
    }

    @Override
    public double getModelContribution(Individual individual) throws IndividualModelValueNotDefinedException {
        throw new IndividualModelValueNotDefinedException();
    }

    @Override
    public void passFitnessFunctions(LinkedHashMap<String, FitnessFunction> fitnessFunctions) {
    }

    protected abstract void buildModelFromIndividual(Individual individual) throws Exception;

    public class ModelThread extends Thread {
        private int threadIndex;
        private Population population;
        private final int NUM_THREADS;

        public ModelThread(int threadIndex, Population pop, int numThreads){
            this.threadIndex = threadIndex;
            this.population = pop;
            NUM_THREADS = numThreads;
        }

        @Override
        public void run(){
            for (int i = 0; i < population.size(); i++) {
                if (i % NUM_THREADS == threadIndex) {
                    Individual individual = population.get(i);
                    try {
                        buildModelFromIndividual(individual);
                    } catch (Exception e) {
                        individual.setModelError(1.0);
                        individual.setModelComplexity(1.0);
                        individual.setProcessedGeneticMaterial(new HashMap<>());
                        individual.setProcessedWeights(new HashMap<>());
                    }
                }
            }
        }
    }
}
