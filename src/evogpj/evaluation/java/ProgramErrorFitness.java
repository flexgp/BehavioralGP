package evogpj.evaluation.java;

import com.google.common.collect.ImmutableList;
import evogpj.algorithm.Parameters;
import evogpj.evaluation.FitnessFunction;
import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;
import evogpj.math.Function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evaluates the program fitness for each {@link Individual} by computing the
 * Manhattan distance between the output of the {@link Tree} on each data point
 * and the desired output. Additionally collects and saves the trace of each
 * {@link Individual} on the training data.
 *
 * @author Steven Fine
 */
public class ProgramErrorFitness extends FitnessFunction {

    public static final String FITNESS_KEY = Parameters.Operators.PROGRAM_ERROR_FITNESS;
    public Boolean isMaximizingFunction = false;
    private final DataJava data;
    private int numThreads;
    private double pow;

    public ProgramErrorFitness(DataJava data, double pow, int numThreads) {
        this.data = data;
        this.pow = pow;
        this.numThreads = numThreads;
    }

    public ProgramErrorFitness(DataJava data, int numThreads) {
        this(data, 1.0, numThreads);
    }

    public ProgramErrorFitness(DataJava data) {
        this(data, 1.0, Parameters.Defaults.EXTERNAL_THREADS);
    }

    @Override
    public Boolean isMaximizingFunction() {
        return this.isMaximizingFunction;
    }

    private void eval(Individual ind) throws Exception {

        Tree genotype = (Tree) ind.getGenotype();
        double[][] inputValuesAux = data.getInputValues();
        double[] targetAux = data.getTargetValues();
        Function func = genotype.getRoot().generateWithReferenceToTreeNode();
        double error = 0.0;

        List<Double> dataPoint;
        List<Double> outputValues;
        List<TreeNode> treeNodes = null;
        List<TreeNode> treeNodesTemp;
        List<List<Double>> trace = new ArrayList<>();

        for (int i = 0; i < genotype.getSize(); i++) {
            trace.add(new ArrayList<>());
        }

        for (int i = 0; i < data.getNumberOfFitnessCases(); i++) {
            dataPoint = new ArrayList<>();
            for (int j = 0; j < data.getNumberOfFeatures(); j++) {
                dataPoint.add(j, inputValuesAux[i][j]);
            }
            outputValues = new ArrayList<>();
            treeNodesTemp = new ArrayList<>();
            double output = func.evalAndCollectGeneticMaterial(
                    dataPoint,
                    outputValues,
                    treeNodesTemp
            );

            error += Math.pow(Math.abs(targetAux[i] - output), pow);

            if (treeNodes == null) {
                treeNodes = treeNodesTemp;
            }

            for (int t = 0; t < treeNodes.size(); t++) {
                trace.get(t).add(outputValues.get(t));
            }
            dataPoint.clear();
        }

        Map<ImmutableList<Double>, TreeNode> geneticMaterial = new HashMap<>();
        for (int i = 0; i < treeNodes.size(); i++) {
            ImmutableList<Double> semantics = ImmutableList.copyOf(trace.get(i));
            TreeNode syntax = treeNodes.get(i);
            combineGeneticMaterial(geneticMaterial, semantics, syntax);
        }

        double fitness = 1.0 - 1.0/(1.0 + error);
        if (fitness < FitnessFunction.PRECISION) {
            fitness = 0;
        } else if (Double.isNaN(fitness)) {
            fitness = 1.0;
        }
        ind.setFitness(ProgramErrorFitness.FITNESS_KEY, fitness);
        ind.setGeneticMaterial(geneticMaterial);
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

    @Override
    public void evalPop(Population pop) {

        ArrayList<SRJavaThread> alThreads = new ArrayList<>();
        for(int i = 0; i < numThreads; i++){
            SRJavaThread threadAux = new SRJavaThread(i, pop, numThreads);
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
                Logger.getLogger(ProgramErrorFitness.class.getName()).log(Level.SEVERE, null, ex);
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
                        eval(individual);
                    } catch (Exception ex) {
                        Logger.getLogger(ProgramErrorFitness.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                indexIndi++;
            }
        }
    }
}
