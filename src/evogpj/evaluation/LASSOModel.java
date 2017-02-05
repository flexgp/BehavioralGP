package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import edu.uci.lasso.LassoFit;
import edu.uci.lasso.LassoFitGenerator;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by stevenfine on 1/25/17.
 */
public class LASSOModel implements Model {
    protected List<Double> targetValues;
    protected Map<ImmutableList<Double>, TreeNode> processedGeneticMaterial;
    protected Map<ImmutableList<Double>, Double> weights;

    public LASSOModel(List<Double> targetValues) {
        this.targetValues = targetValues;
        processedGeneticMaterial = new HashMap<>();
        weights = new HashMap<>();
    }

    @Override
    public void buildModel(Population population) {
        processedGeneticMaterial.clear();
        weights.clear();
        for (Individual individual : population) {
            try {
                buildModelFromIndividual(individual);
            } catch (Exception e) {
                individual.setModelError(1.0);
                individual.setModelComplexity(1.0);
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

    private void buildModelFromIndividual(Individual individual) throws Exception {
        Map<ImmutableList<Double>, TreeNode> geneticMaterial = individual.getGeneticMaterial();

        int numberOfSubtrees = geneticMaterial.size();
        int numberOfFitnessCases = targetValues.size();

        Map<Integer, ImmutableList<Double>> indexToSemantics = new HashMap<>();
        Map<ImmutableList<Double>, Integer> semanticsToIndex = new HashMap<>();
        int index = 0;
        for (Map.Entry entry : geneticMaterial.entrySet()) {
            ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
            indexToSemantics.put(index, semantics);
            semanticsToIndex.put(semantics, index);
            index++;
        }

        float[][] trace = new float[numberOfFitnessCases][numberOfSubtrees];
        for (int i = 0; i < indexToSemantics.size(); i++) {
            ImmutableList<Double> semantics = indexToSemantics.get(i);
            for (int j = 0; j < numberOfFitnessCases; j++) {
                BigDecimal number = new BigDecimal(semantics.get(j));
                trace[j][i] = number.floatValue();
            }
        }

        LassoFitGenerator fitGenerator = new LassoFitGenerator();
        fitGenerator.init(numberOfSubtrees, numberOfFitnessCases);

        for (int i = 0; i < numberOfFitnessCases; i++) {
            fitGenerator.setObservationValues(i, trace[i]);
            fitGenerator.setTarget(i, targetValues.get(i));
        }

        LassoFit fit = fitGenerator.fit(-1);

        int indexWeights = 0;
        int numFeaturesUsed = 0;
        for (int i = 0; i < fit.lambdas.length; i++) {
            if (fit.nonZeroWeights[i] > numFeaturesUsed) {
                numFeaturesUsed = fit.nonZeroWeights[i];
                indexWeights = i;
            }
        }

        double error = 0;
        double[] lassoWeights = fit.getWeights(indexWeights);
        double lassoIntercept = fit.intercepts[indexWeights];
        for (int i = 0; i < numberOfFitnessCases; i++) {
            double prediction = 0;
            for(int j = 0; j < lassoWeights.length; j++){
                prediction += trace[i][j]*lassoWeights[j];
            }
            prediction += lassoIntercept;
            error += Math.abs(targetValues.get(i) - prediction);
        }

        error = 1.0 - 1.0/(1.0 + error);
        double complexity = 1.0 - 1.0/numFeaturesUsed;

        if (Double.isNaN(error)) {
            error = 1;
        }

        individual.setModelError(error);
        individual.setModelComplexity(complexity);

        Set<ImmutableList<Double>> usefulSubtrees = new HashSet<>();
        for (int i = 0; i < lassoWeights.length; i++) {
            if (lassoWeights[i] != 0) {
                usefulSubtrees.add(indexToSemantics.get(i));
            }
        }


        // Place geneticMaterial in processedGeneticMaterial
        if (!usefulSubtrees.isEmpty()) {
            for (ImmutableList<Double> semantics : usefulSubtrees) {
                TreeNode syntax = geneticMaterial.get(semantics);
                double weight = Math.abs(lassoWeights[semanticsToIndex.get(semantics)]);
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
}
