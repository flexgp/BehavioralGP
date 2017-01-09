package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;
import weka.classifiers.trees.REPTree;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

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

    public void addGeneticMaterial(Map<ImmutableList<Double>, TreeNode> geneticMaterial) {
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
                if (archive.size() < Archive.CAPACITY) {
                    archive.put(semantics, syntax);
                }
            }
        }
    }
}
