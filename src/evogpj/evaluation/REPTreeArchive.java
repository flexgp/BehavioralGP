package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeGenerator;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;
import weka.classifiers.trees.REPTree;
import weka.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stevenfine on 8/19/16.
 */
public class REPTreeArchive extends UnweightedArchive {

    private ImmutableList<Double> targetValues;

    /**
     * Construct an archive that uses the REPTree classifier to determine the
     * utility of subtrees.
     * @param rand random number generator.
     */
    public REPTreeArchive(MersenneTwisterFast rand, List<Double> targetValues) {
        super(rand);
        this.targetValues = ImmutableList.copyOf(targetValues);
    }

    public void addGeneticMaterial(
            Map<ImmutableList<Double>, TreeNode> geneticMaterial,
            Map<ImmutableList<Double>, Double> weights
    ) {
        archive.clear();
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
        try {
            repTree.buildClassifier(trainingData);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        String tree = repTree.toString();
        for (String name : featureNamesList) {
            if (tree.contains(name)) {
                ImmutableList<Double> semantics = featureNamesMap.get(name);
                TreeNode syntax = geneticMaterial.get(semantics);
                TreeNode duplicate = TreeGenerator.generateTree(syntax.toStringAsTree()).getRoot();
                archive.put(semantics, duplicate);
            }
        }
    }
}
