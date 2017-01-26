package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.*;

import java.util.*;

/**
 * Created by stevenfine on 1/25/17.
 */
public class FullPopulationREPTreeModel implements Model {
    protected List<Double> targetValues;
    protected Map<ImmutableList<Double>, TreeNode> processedGeneticMaterial;
    protected Map<ImmutableList<Double>, Double> weights;

    public FullPopulationREPTreeModel(List<Double> targetValues) {
        this.targetValues = targetValues;
        processedGeneticMaterial = new HashMap<>();
        weights = new HashMap<>();
    }

    @Override
    public void buildModel(Population population) {
        processedGeneticMaterial.clear();
        weights.clear();
        Map<ImmutableList<Double>, TreeNode> collectedGeneticMaterial = new HashMap<>();
        for (Individual individual : population) {
            Map<ImmutableList<Double>, TreeNode> geneticMaterial = individual.getGeneticMaterial();
            for (Map.Entry entry : geneticMaterial.entrySet()) {
                ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
                TreeNode syntax = geneticMaterial.get(semantics);
                combineGeneticMaterial(collectedGeneticMaterial, semantics, syntax);
            }
        }
        try {
            buildModelFromCollectedGeneticMaterial(collectedGeneticMaterial);
        } catch (Exception e) {
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
    public double getModelError(Individual individual) throws IndividualModelValueNotDefinedException {
        throw new IndividualModelValueNotDefinedException();
    }

    @Override
    public double getModelComplexity(Individual individual) throws IndividualModelValueNotDefinedException {
        throw new IndividualModelValueNotDefinedException();
    }

    private void buildModelFromCollectedGeneticMaterial(Map<ImmutableList<Double>, TreeNode> geneticMaterial) throws Exception {

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

        // TODO: try to find a better way to do this
        Set<ImmutableList<Double>> usefulSubtrees = new HashSet<>();
        String treeString = repTree.toString();
        for (String name : featureNamesList) {
            if (treeString.contains(name)) {
                usefulSubtrees.add(featureNamesMap.get(name));
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

    /**
     * Add semantics and syntax to geneticMaterial if either there is no key
     * with those semantics, or the syntax tree is smaller than the one
     * already in the geneticMaterial Map.
     * @param geneticMaterial A map that represents a set of subtrees, where
     *                        each key is a subtree's output on the training
     *                        data, and each value is its syntax.
     * @param semantics The key to geneticMaterial
     * @param syntax The value to geneticMaterial
     */
    private void combineGeneticMaterial(
            Map<ImmutableList<Double>, TreeNode> geneticMaterial,
            ImmutableList<Double> semantics,
            TreeNode syntax
    ) {
        if (geneticMaterial.containsKey(semantics)) {
            int oldSize = geneticMaterial.get(semantics).getSubtreeSize();
            int newSize = syntax.getSubtreeSize();
            if (newSize < oldSize) {
                geneticMaterial.put(semantics, syntax);
            }
        } else {
            geneticMaterial.put(semantics, syntax);
        }
    }
}
