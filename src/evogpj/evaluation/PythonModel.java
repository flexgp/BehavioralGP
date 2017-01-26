package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stevenfine on 1/25/17.
 */
public class PythonModel extends MultiThreadedModel {

    public PythonModel(List<Double> targetValues, int numThreads) {
        super(targetValues, numThreads);
    }

    @Override
    protected void buildModelFromIndividual(Individual individual) throws Exception {
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

        List<String> command = new ArrayList<>();
        command.add("python");
        command.add("./regression_tree.py"); // Make robust for final implementation
        command.add(Integer.toString(featureNamesList.size()));

        for (int dataPointIndex = 0; dataPointIndex < numberOfFitnessCases; dataPointIndex++) {
            for (String name : featureNamesList) {
                command.add(Double.toString(featureNamesMap.get(name).get(dataPointIndex)));
            }
            command.add(Double.toString(targetValues.get(dataPointIndex)));
        }

        ProcessBuilder builder = new ProcessBuilder(command.toArray(new String[0]));
        builder.redirectErrorStream(true);
        Process process = builder.start();
        Scanner scanner = new Scanner(process.getInputStream());

        double error = Double.parseDouble(scanner.nextLine());
        double complexity = Double.parseDouble(scanner.nextLine());

        individual.setModelError(error);
        individual.setModelComplexity(complexity);

        Set<ImmutableList<Double>> usefulSubtrees = new HashSet<>();
        for (int i = 0; i < featureNamesList.size(); i++) {
            if (Integer.parseInt(scanner.nextLine()) == 1) {
                String name = featureNamesList.get(i);
                ImmutableList<Double> semantics = featureNamesMap.get(name);
                usefulSubtrees.add(semantics);
            }
        }

        // Set processedGeneticMaterial
        Map<ImmutableList<Double>, TreeNode> individualProcessedGeneticMaterial = new HashMap<>();
        Map<ImmutableList<Double>, Double> individualProcessedWeights = new HashMap<>();
        if (!usefulSubtrees.isEmpty()) {
            double weight = 1.0 / ((1.0 + error) * usefulSubtrees.size());
            for (ImmutableList<Double> semantics : usefulSubtrees) {
                TreeNode syntax = geneticMaterial.get(semantics);
                individualProcessedGeneticMaterial.put(semantics, syntax);
                individualProcessedWeights.put(semantics, weight);
            }
        }
        individual.setProcessedGeneticMaterial(individualProcessedGeneticMaterial);
        individual.setProcessedWeights(individualProcessedWeights);
    }
}
