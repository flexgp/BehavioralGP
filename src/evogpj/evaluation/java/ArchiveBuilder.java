package evogpj.evaluation.java;

import evogpj.algorithm.Parameters;
import evogpj.evaluation.FitnessFunction;
import evogpj.genotype.Tree;
import evogpj.gp.Individual;
import evogpj.gp.Population;
import evogpj.math.Function;
import evogpj.math.means.ArithmeticMean;
import evogpj.math.means.Maximum;
import evogpj.math.means.Mean;
import evogpj.math.means.PowerMean;

import java.util.ArrayList;
import java.util.List;
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

    /**
     *
     * @param data
     */
    public ArchiveBuilder(DataJava data) {
        this(data, 2, false,1);
    }

    public ArchiveBuilder(DataJava aData, int aPow, boolean is_int,int anumThreads) {
        this.data = aData;
        pow = aPow;
        USE_INT = is_int;
        numThreads = anumThreads;
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
    public void eval(Individual ind) throws Exception {

        Tree genotype = (Tree) ind.getGenotype();
        Mean MEAN_FUNC = getMeanFromP(pow);
        Function func = genotype.generate();
        List<Double> d;
        double[][] inputValuesAux = data.getInputValues();
        double[] targetAux = data.getTargetValues();

        for (int i = 0; i < data.getNumberOfFitnessCases(); i++) {
            d = new ArrayList<Double>();
            for (int j = 0; j < data.getNumberOfFeatures(); j++) {
                d.add(j, inputValuesAux[i][j]);
            }
            MEAN_FUNC.addValue(Math.abs(targetAux[i] - func.eval(d)));
            d.clear();
        }

        Double error = MEAN_FUNC.getMean();
        double fitness = errorToFitness(error);

        ind.setFitness(OrdinaryGP.FITNESS_KEY, fitness);
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
            } catch (InterruptedException ex) {
                Logger.getLogger(SRLARSJava.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        public SRJavaThread(int anIndex, Population aPop,int aTotalThreads){
            indexThread = anIndex;
            pop = aPop;
            totalThreads = aTotalThreads;
        }

        @Override
        public void run(){
            int indexIndi = 0;
            for (Individual individual : pop) {
                if(indexIndi%totalThreads==indexThread){
                    try {
                        eval(individual);
                    } catch (Exception ex) {
                        Logger.getLogger(SRLARSJava.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                indexIndi++;
            }
        }
    }
}
