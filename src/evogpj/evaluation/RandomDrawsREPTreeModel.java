package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;

/**
 * Created by stevenfine on 2/3/17.
 */
public class RandomDrawsREPTreeModel implements Model {
    protected List<Double> targetValues;
    protected MersenneTwisterFast rand;
    protected Map<ImmutableList<Double>, TreeNode> processedGeneticMaterial;
    protected Map<ImmutableList<Double>, Double> weights;

    public RandomDrawsREPTreeModel(List<Double> targetValues, MersenneTwisterFast rand) {
        this.targetValues = targetValues;
        this.rand = rand;
        processedGeneticMaterial = new HashMap<>();
        weights = new HashMap<>();
    }

    @Override
    public void buildModel(Population population) {
        processedGeneticMaterial.clear();
        weights.clear();

        int numSubtreesInPopulation = 0;
        for (Individual individual : population) {
            individual.resetModelContribution();
            numSubtreesInPopulation += ((Tree) individual.getGenotype()).getSize();
        }
        double averageTreeSize = (double) numSubtreesInPopulation / (double) population.size();
        int numFeaturesPerModel = new Double(Math.floor(averageTreeSize)).intValue();

        Map<ImmutableList<Double>, TreeNode> collectedGeneticMaterial = new HashMap<>();
        Map<ImmutableList<Double>, Individual> semanticsToIndividual = new HashMap<>();
        for (Individual individual : population) {
            Map<ImmutableList<Double>, TreeNode> geneticMaterial = individual.getGeneticMaterial();
            for (Map.Entry entry : geneticMaterial.entrySet()) {
                ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
                TreeNode syntax = geneticMaterial.get(semantics);
                combineGeneticMaterial(
                        collectedGeneticMaterial,
                        semantics,
                        syntax,
                        semanticsToIndividual,
                        individual
                );
            }
        }
        try {
            int totalFeaturesUsed = buildModelsFromCollectedGeneticMaterial(
                    collectedGeneticMaterial,
                    semanticsToIndividual,
                    population.size(),
                    numFeaturesPerModel
                    );
            if (totalFeaturesUsed > 0) {
                for (Individual individual : population) {
                    int modelContribution = individual.getModelContribution();
                    double modelContributionFitness = 1.0 - (double) modelContribution / (double) totalFeaturesUsed;
                    individual.setModelContributionFitness(modelContributionFitness);
                }
            } else {
                for (Individual individual : population) {
                    individual.setModelContributionFitness(1.0);
                }
            }
        } catch (Exception e) {
            for (Individual individual : population) {
                individual.setModelContributionFitness(1.0);
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
    public double getModelError(Individual individual) throws IndividualModelValueNotDefinedException {
        throw new IndividualModelValueNotDefinedException();
    }

    @Override
    public double getModelComplexity(Individual individual) throws IndividualModelValueNotDefinedException {
        throw new IndividualModelValueNotDefinedException();
    }

    @Override
    public double getModelContribution(Individual individual) {
        return individual.getModelContributionFitness();
    }

    private int buildModelsFromCollectedGeneticMaterial(
            Map<ImmutableList<Double>, TreeNode> combinedGeneticMaterial,
            Map<ImmutableList<Double>, Individual> semanticsToIndividual,
            int numModels,
            int numFeaturesPerModel
    ) throws Exception {

        List<ImmutableList<Double>> semanticsList = new ArrayList<>();
        for (Map.Entry entry : combinedGeneticMaterial.entrySet()) {
            ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
            semanticsList.add(semantics);
        }

        int totalFeaturesUsed = 0;

        for (int modelBuild = 0; modelBuild < numModels; modelBuild++) {

            Map<ImmutableList<Double>, TreeNode> geneticMaterial = new HashMap<>();
            for (int feature = 0; feature < numFeaturesPerModel; feature++) {
                int index = rand.nextInt(semanticsList.size());
                ImmutableList<Double> semantics = semanticsList.get(index);
                TreeNode syntax = combinedGeneticMaterial.get(semantics);
                geneticMaterial.put(semantics, syntax);
            }

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

            double error = 1.0 - 1.0 / (1.0 + eval.meanAbsoluteError() * numberOfFitnessCases);

            // TODO: try to find a better way to do this
            Set<ImmutableList<Double>> usefulSubtrees = new HashSet<>();
            String treeString = repTree.toString();
            for (String name : featureNamesList) {
                if (treeString.contains(name)) {
                    usefulSubtrees.add(featureNamesMap.get(name));
                }
            }
            totalFeaturesUsed += usefulSubtrees.size();

            // Place geneticMaterial in processedGeneticMaterial
            if (!usefulSubtrees.isEmpty()) {
                double weight = 1.0 / ((1.0 + error) * usefulSubtrees.size());
                for (ImmutableList<Double> semantics : usefulSubtrees) {
                    semanticsToIndividual.get(semantics).updateModelContribution(1);
                    TreeNode syntax = geneticMaterial.get(semantics);
                    if (processedGeneticMaterial.containsKey(semantics)) {
                        int oldSize = processedGeneticMaterial.get(semantics).getSubtreeSize();
                        int newSize = syntax.getSubtreeSize();
                        if (newSize < oldSize) {
                            processedGeneticMaterial.put(semantics, syntax);
                        }
                        double oldWeight = weights.get(semantics);
                        weights.put(semantics, weight + oldWeight);
                    } else {
                        processedGeneticMaterial.put(semantics, syntax);
                        weights.put(semantics, weight);
                    }
                }
            }
        }
        return totalFeaturesUsed;
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
     * @param semanticsToIndividual A map that represents which Individual
     *                              contributed the semantics that are in
     *                              the geneticMaterial.
     * @param individual The individual to whom the semantics and syntax
     *                   belong.
     */
    private void combineGeneticMaterial(
            Map<ImmutableList<Double>, TreeNode> geneticMaterial,
            ImmutableList<Double> semantics,
            TreeNode syntax,
            Map<ImmutableList<Double>, Individual> semanticsToIndividual,
            Individual individual
    ) {
        if (geneticMaterial.containsKey(semantics)) {
            int oldSize = geneticMaterial.get(semantics).getSubtreeSize();
            int newSize = syntax.getSubtreeSize();
            if (newSize < oldSize) {
                geneticMaterial.put(semantics, syntax);
                semanticsToIndividual.put(semantics, individual);
            }
        } else {
            geneticMaterial.put(semantics, syntax);
            semanticsToIndividual.put(semantics, individual);
        }
    }
}
