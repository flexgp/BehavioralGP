package evogpj.evaluation.java;

import com.google.common.collect.ImmutableList;
import evogpj.algorithm.Parameters;
import evogpj.evaluation.Archive;
import evogpj.evaluation.FitnessFunction;
import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;
import evogpj.math.Function;
import evogpj.math.means.ArithmeticMean;
import evogpj.math.means.Maximum;
import evogpj.math.means.Mean;
import evogpj.math.means.PowerMean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by stevenfine on 7/28/16.
 */
public class ArchiveBuilder extends FitnessFunction {
    private final DataJava data;
    private int pow;
    private final boolean USE_INT;
    public static String FITNESS_KEY = Parameters.Operators.ARCHIVE_BUILDER_FITNESS;
    public Boolean isMaximizingFunction = true;
    private int numThreads;
    private Archive archive;


    public ArchiveBuilder(DataJava aData, int aPow, boolean is_int, int anumThreads, Archive archive) {
        this.data = aData;
        pow = aPow;
        USE_INT = is_int;
        numThreads = anumThreads;
        this.archive = archive;
    }

    /**
     * Should this fitness function be minimized (i.e. mean squared error) or
     * maximized?
     * @return
     */
    @Override
    public Boolean isMaximizingFunction() { return this.isMaximizingFunction; }

    /**
     * Simple "factory"-like method for returning the proper generalized mean
     * object to use, given the parameter p (see
     * http://en.wikipedia.org/wiki/Generalized_mean). Since we are only
     * concerned with means of errors, we don't utilize all the valid values of
     * p defined for generalized means.
     *
     * @param p
     *            power to use in computing a mean
     * @return an instance of {@link PowerMean} if p > 1; an instance of
     *         {@link ArithmeticMean} if p == 1; otherwise, an instance of
     *         {@link Maximum}.
     */
    public static Mean getMeanFromP(int p) {
        if (p == 1) {
            return new ArithmeticMean();
        } else if (p > 1) {
            return new PowerMean(p);
        } else {
            return new Maximum();
        }
    }

    /**
     * @param ind
     * @see Function
     */
    public Map<ImmutableList<Double>, TreeNode> eval(Individual ind) throws Exception {

        Tree genotype = (Tree) ind.getGenotype();
        Mean MEAN_FUNC = getMeanFromP(pow);
        List<Double> d;
        double[][] inputValuesAux = data.getInputValues();
        double[] targetAux = data.getTargetValues();
        Function func = genotype.getRoot().generateWithReferenceToTreeNode();
        ArrayList<Double> outputValues;
        ArrayList<TreeNode> treeNodes = null;
        ArrayList<TreeNode> treeNodesTemp;
        ArrayList<ArrayList<Double>> trace = new ArrayList<>();
        for (int i = 0; i < genotype.getSize(); i++) {
            trace.add(new ArrayList<>());
        }

        for (int i = 0; i < data.getNumberOfFitnessCases(); i++) {
            d = new ArrayList<>();
            for (int j = 0; j < data.getNumberOfFeatures(); j++) {
                d.add(j, inputValuesAux[i][j]);
            }
            outputValues = new ArrayList<>();
            treeNodesTemp = new ArrayList<>();
            double output = func.evalAndCollectGeneticMaterial(
                    d,
                    outputValues,
                    treeNodesTemp
            );
            if (treeNodes == null) {
                treeNodes = treeNodesTemp;
            }
            if (USE_INT) {
                output = Math.round(output);
            }
            MEAN_FUNC.addValue(Math.abs(targetAux[i] - output));

            for (int t = 0; t < treeNodes.size(); t++) {
                trace.get(t).add(outputValues.get(t));
            }
            d.clear();
        }

        Map<ImmutableList<Double>, TreeNode> geneticMaterial = new HashMap<>();
        for (int i = 0; i < treeNodes.size(); i++) {
            ImmutableList<Double> semantics = ImmutableList.copyOf(trace.get(i));
            TreeNode syntax = treeNodes.get(i);
            combineGeneticMaterial(geneticMaterial, semantics, syntax);
        }

        Double error = MEAN_FUNC.getMean();
        double fitness = errorToFitness(error);

        ind.setFitness(ArchiveBuilder.FITNESS_KEY, fitness);
        return geneticMaterial;
    }

    /**
     * Transform errors to fitness values. For errors, smaller values are
     * better, while for fitness, values closer to 1 are better. This particular
     * transformation also places a greater emphasis on small changes close to
     * the solution (fitness == 1.0 represents a complete solution). However,
     * this transformation also assumes that the error is in the range [0,1].
     *
     * @param error
     *            Error on training set; value in range [0,1].
     * @return 0 if error is not a number (NaN) or outside of the range [0,1].
     *         Otherwise, return the fitness.
     */
    private Double errorToFitness(Double error) {
        if(error==0){
            return 1.0;
        }else if (error.isNaN() || error < 0.0 ) {
            return 0.0;
        } else {
            return (1.0) / error;
        }
    }

    @Override
    public void evalPop(Population pop) {

        Map<ImmutableList<Double>, TreeNode> combinedGeneticMaterial = new HashMap<>();
        Map<ImmutableList<Double>, TreeNode> threadGeneticMaterial;

        ArrayList<SRJavaThread> alThreads = new ArrayList<SRJavaThread>();
        for(int i=0;i<numThreads;i++){
            SRJavaThread threadAux = new SRJavaThread(i, pop,numThreads);
            alThreads.add(threadAux);
        }

        for(int i=0;i<numThreads;i++){
            SRJavaThread threadAux = alThreads.get(i);
            threadAux.start();
        }

        for(int i=0;i<numThreads;i++){
            SRJavaThread threadAux = alThreads.get(i);
            try {
                threadAux.join();
                threadGeneticMaterial = threadAux.getGeneticMaterial();
                for (Map.Entry entry : threadGeneticMaterial.entrySet()) {
                    ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
                    TreeNode syntax = (TreeNode) entry.getValue();
                    combineGeneticMaterial(combinedGeneticMaterial, semantics, syntax);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ArchiveBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        List<TreeNode> subtrees = new ArrayList<>();
        List<ImmutableList<Double>> trace = new ArrayList<>();
        for (Map.Entry entry : combinedGeneticMaterial.entrySet()) {
            ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
            TreeNode syntax = combinedGeneticMaterial.get(semantics);
            subtrees.add(syntax);
            trace.add(semantics);
        }
        // ArchiveBuilder uses an UnweightedArchive so the weights list is empty.
        archive.addGeneticMaterial(subtrees, trace, new ArrayList<>());
    }

    /**
     * Add semantics and syntax to geneticMaterial if either there is no key
     * with those semantics, or the syntax tree is less complex than the one
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
            int oldComplexity = geneticMaterial.get(semantics).getSubtreeComplexity();
            int newComplexity = syntax.getSubtreeComplexity();
            if (newComplexity < oldComplexity) {
                geneticMaterial.put(semantics, syntax);
            }
        } else {
            geneticMaterial.put(semantics, syntax);
        }
    }

    /**
     * @return the fitnessCases
     */
    public double[][] getFitnessCases() {
        return data.getInputValues();
    }

    /**
     * @return the scaled_target
     */
    public double[] getTarget() {
        return data.getScaledTargetValues();
    }

    /**
     * Utility class for computing the element-wise normalization of a vector.
     * That is, for a given vector <code>A</code>, where all values are in the
     * range <code>[min(A), max(A)]</code>, we want an easy way to compute the
     * scaled vector <code>B</code> such that every value in B is in the range
     * <code>[0, 1]</code>.
     * <p>
     * In particular, if the ith element of <code>A</code> has value
     * <code>a_i</code>, then the ith element of <code>B</code> will have the
     * value <code>(a_i-min(A))/(max(A)-min(A))</code>.
     * <p>
     * Also, provide the inverse transformation, to recover the values of
     * <code>A</code>.
     *
     * @author Owen Derby
     * @see ScaledData
     */
    private class ScaledTransform {
        private final double min, range;

        public ScaledTransform(double min, double max) {
            this.min = min;
            this.range = max - min;
        }

        public Double scaleValue(Double val) {
            return (val - min) / range;
        }

        public Double unScaleValue(Double scaled_val) {
            return (scaled_val * range) + min;
        }
    }

    public class SRJavaThread extends Thread{
        private int indexThread, totalThreads;
        private Population pop;
        private Map<ImmutableList<Double>, TreeNode> geneticMaterial;

        public SRJavaThread(int anIndex, Population aPop,int aTotalThreads){
            indexThread = anIndex;
            pop = aPop;
            totalThreads = aTotalThreads;
        }

        public Map<ImmutableList<Double>, TreeNode> getGeneticMaterial() {
            return geneticMaterial;
        }

        @Override
        public void run(){
            int indexIndi = 0;
            for (Individual individual : pop) {
                if(indexIndi%totalThreads==indexThread){
                    try {
                        geneticMaterial = eval(individual);
                    } catch (Exception ex) {
                        Logger.getLogger(ArchiveBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                indexIndi++;
            }
        }
    }
}
