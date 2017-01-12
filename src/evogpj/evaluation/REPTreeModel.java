package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

/**
 * Created by stevenfine on 1/12/17.
 */
public class REPTreeModel implements BPModel {

    protected List<Double> targetValues;
    protected Map<ImmutableList<Double>, TreeNode> processedGeneticMaterial;
    protected Map<ImmutableList<Double>, Double> weights;

    public REPTreeModel(List<Double> targetValues) {
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

    public static void main(String args[]) {
        List<Double> l1 = new ArrayList<>();
        List<Double> l2 = new ArrayList<>();
        l1.add(0.9);
        l1.add(0.9);
        l1.add(2.0);
        l1.add(2.0);
        l1.add(2.0);
        l1.add(2.0);
        l1.add(2.0);
        l2.add(1.0);
        l2.add(1.0);
        l2.add(1.0);
        l2.add(1.0);
        l2.add(1.0);
        l2.add(1.0);
        l2.add(1.0);
        ImmutableList<Double> il1 = ImmutableList.copyOf(l1);
        ImmutableList<Double> il2 = ImmutableList.copyOf(l2);

        Individual ind = new Individual(null);
        Map<ImmutableList<Double>, TreeNode> geneticMaterial = new HashMap<>();
        geneticMaterial.put(il1, new TreeNode(null, "a"));
        geneticMaterial.put(il2, new TreeNode(null, "b"));
        ind.setGeneticMaterial(geneticMaterial);

        List<Double> targetVals = new ArrayList<>();
        targetVals.add(0.0);
        targetVals.add(0.0);
        targetVals.add(100.0);
        targetVals.add(100.0);
        targetVals.add(100.0);
        targetVals.add(100.0);
        targetVals.add(101.0);
        REPTreeModel model = new REPTreeModel(targetVals);

        try {
            model.buildModelFromIndividual(ind);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        FastVector fvWekaAttributes = new FastVector(featureNamesList.size() + 1);
        for (String name : featureNamesList) {
            fvWekaAttributes.addElement(new Attribute(name));
        }
        fvWekaAttributes.addElement(new Attribute("OUTPUT"));

        Instances trainingData = new Instances("Rel", fvWekaAttributes, numberOfFitnessCases);
        trainingData.setClassIndex(trainingData.numAttributes() - 1);
        for (int i = 0; i < numberOfFitnessCases; i++) {
            Instance dataPoint = new Instance(featureNamesList.size() + 1);
            for (int j = 0; j < featureNamesList.size(); j++) {
                String name = featureNamesList.get(j);
                dataPoint.setValue(
                        (Attribute) fvWekaAttributes.elementAt(j),
                        featureNamesMap.get(name).get(i)
                );
            }
            dataPoint.setValue(
                    (Attribute) fvWekaAttributes.elementAt(featureNamesList.size()),
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
