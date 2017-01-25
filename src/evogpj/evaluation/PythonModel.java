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
public class PythonModel implements Model {
    protected List<Double> targetValues;
    protected Map<ImmutableList<Double>, TreeNode> processedGeneticMaterial;
    protected Map<ImmutableList<Double>, Double> weights;
    protected int numThreads;

    public PythonModel(List<Double> targetValues, int numThreads) {
        this.targetValues = targetValues;
        processedGeneticMaterial = new HashMap<>();
        weights = new HashMap<>();
        this.numThreads = numThreads;
    }

    @Override
    public void buildModel(Population population) {

        processedGeneticMaterial.clear();
        weights.clear();

        ArrayList<SRJavaThread> alThreads = new ArrayList<>();
        for(int i = 0; i < numThreads; i++){
            SRJavaThread threadAux = new SRJavaThread(i, population, numThreads);
            alThreads.add(threadAux);
        }

        for(int i = 0; i < numThreads; i++){
            SRJavaThread threadAux = alThreads.get(i);
            threadAux.start();
        }

        for(int i=0;i<numThreads;i++){
            SRJavaThread threadAux = alThreads.get(i);
            try {
                threadAux.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(PythonModel.class.getName()).log(Level.SEVERE, null, ex);
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

    public class SRJavaThread extends Thread{
        private int indexThread;
        private int totalThreads;
        private Population pop;

        public SRJavaThread(int anIndex, Population aPop, int aTotalThreads){
            indexThread = anIndex;
            pop = aPop;
            totalThreads = aTotalThreads;
        }

        @Override
        public void run(){
            int indexIndi = 0;
            for (Individual individual : pop) {
                if(indexIndi % totalThreads == indexThread){
                    try {
                        buildModelFromIndividual(individual);
                    } catch (Exception e) {
                        individual.setModelError(1.0);
                        individual.setModelComplexity(1.0);
                    }
                }
                indexIndi++;
            }
        }
    }
}
