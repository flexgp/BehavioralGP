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

    private void buildModelFromIndividual(Individual individual) throws Exception {
        Map<ImmutableList<Double>, TreeNode> geneticMaterial = individual.getGeneticMaterial();

        int numberOfSubtrees = geneticMaterial.size();
        int numberOfFitnessCases = targetValues.size();

        Map<Integer, ImmutableList<Double>> indexToSemantics = new HashMap<>();
        int index = 0;
        for (Map.Entry entry : geneticMaterial.entrySet()) {
            indexToSemantics.put(index++, (ImmutableList<Double>) entry.getKey());
        }

        float[][] trace = new float[numberOfFitnessCases][numberOfSubtrees];
        for (int i = 0; i < indexToSemantics.size(); i++) {
            ImmutableList<Double> semantics = indexToSemantics.get(i);
//            System.out.println(semantics);
            for (int j = 0; j < numberOfFitnessCases; j++) {
                BigDecimal number = new BigDecimal(semantics.get(j));
                trace[j][i] = number.floatValue();
//                System.out.println(semantics.get());
            }
        }

        LassoFitGenerator fitGenerator = new LassoFitGenerator();
        fitGenerator.init(numberOfSubtrees, numberOfFitnessCases);

        for (int i = 0; i < numberOfFitnessCases; i++) {
            fitGenerator.setObservationValues(i, trace[i]);
            for (int k = 0; k < trace[i].length; k++) {
//                System.out.println(trace[i][k]);
            }
//            System.out.println("break");
            fitGenerator.setTarget(i, targetValues.get(i));
        }

        LassoFit fit = fitGenerator.fit(-1);
//        System.out.println(numberOfSubtrees);
//        double[] l = fit.getWeights(3);
//        for (int i = 0; i < l.length; i++) {
//            System.out.println(l[i]);
//        }

        int indexWeights = 0;
        int numFeaturesUsed = 0;
        for (int i = 0; i < fit.lambdas.length; i++) {
            if (fit.nonZeroWeights[i] > numberOfSubtrees/2) {
                numFeaturesUsed = fit.nonZeroWeights[i];
//                System.out.println(numFeaturesUsed);
                indexWeights = i;
                break;
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
            //phenotype_tmp.addNewDataValue(prediction);
            error += Math.abs(targetValues.get(i) - prediction);
        }

        error = 1.0 - 1.0/(1.0 + error);
        double complexity = 1.0 - 1.0/numFeaturesUsed;

        if (Double.isNaN(error)) {
            error = 1;
        }


//        System.out.println(numFeaturesUsed);
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
            double weight = 1.0 / ((1.0 + error) * usefulSubtrees.size());
            for (ImmutableList<Double> semantics : usefulSubtrees) {
                TreeNode syntax = geneticMaterial.get(semantics);
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
