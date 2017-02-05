package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.*;

import java.util.*;

/**
 * Created by stevenfine on 1/25/17.
 */
public class RandomModel implements Model {

    protected List<Double> targetValues;
    protected Map<ImmutableList<Double>, TreeNode> processedGeneticMaterial;
    protected Map<ImmutableList<Double>, Double> weights;
    protected MersenneTwisterFast rand;

    public RandomModel(List<Double> targetValues, MersenneTwisterFast rand) {
        this.targetValues = targetValues;
        processedGeneticMaterial = new HashMap<>();
        weights = new HashMap<>();
        this.rand = rand;
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

        List<String> featureNamesList = new ArrayList<>();
        Map<String, ImmutableList<Double>> featureNamesMap = new HashMap<>();
        int counter = 0;
        int numberOfFitnessCases = 0;
        for (Map.Entry entry : geneticMaterial.entrySet()) {
            ImmutableList<Double> key = (ImmutableList<Double>) entry.getKey();
            String name = "TREE" + counter++;
            featureNamesList.add(name);
            featureNamesMap.put(name, key);
            if (numberOfFitnessCases == 0) {
                numberOfFitnessCases = key.size();
            }
        }

        // Build REPTree classifier.
        ArrayList<Attribute> fvWekaAttributes = new ArrayList<>(featureNamesList.size() + 1);
        for (String name : featureNamesList) {
            fvWekaAttributes.add(new Attribute(name));
        }
        fvWekaAttributes.add(new Attribute("OUTPUT"));

        Instances trainingData = new Instances("Rel", fvWekaAttributes, numberOfFitnessCases);
        trainingData.setClassIndex(trainingData.numAttributes() - 1);
        for (int i = 0; i < numberOfFitnessCases; i++) {
            Instance dataPoint = new DenseInstance(featureNamesList.size() + 1);
            for (int j = 0; j < featureNamesList.size(); j++) {
                String name = featureNamesList.get(j);
                dataPoint.setValue(
                        fvWekaAttributes.get(j),
                        featureNamesMap.get(name).get(i)
                );
            }
            dataPoint.setValue(
                    fvWekaAttributes.get(featureNamesList.size()),
                    targetValues.get(i)
            );
            trainingData.add(dataPoint);
        }

        REPTree repTree = new REPTree();
        repTree.setNoPruning(true);
        repTree.buildClassifier(trainingData);
        Evaluation eval = new Evaluation(trainingData);
        eval.evaluateModel(repTree, trainingData);

        double error = 1.0 - 1.0/(1.0 + eval.meanAbsoluteError()*numberOfFitnessCases);
        double complexity = 1.0 - 1.0/repTree.numNodes();

        individual.setModelError(error);
        individual.setModelComplexity(complexity);

        // TODO: try to find a better way to do this
        Set<ImmutableList<Double>> usefulSubtrees = new HashSet<>();
        String treeString = repTree.toString();
        for (String name : featureNamesList) {
            if (treeString.contains(name)) {
                usefulSubtrees.add(featureNamesMap.get(name));
            }
        }

        List<ImmutableList<Double>> allSubtrees = new ArrayList<>();
        for (ImmutableList<Double> semantics : geneticMaterial.keySet()) {
            allSubtrees.add(semantics);
        }

        int size = usefulSubtrees.size();
        usefulSubtrees.clear();
        while (usefulSubtrees.size() < size) {
            int index = rand.nextInt(allSubtrees.size());
            usefulSubtrees.add(allSubtrees.get(index));
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
