package evogpj.evaluation.java;

import evogpj.algorithm.Parameters;
import evogpj.evaluation.FitnessFunction;
import evogpj.genotype.Tree;
import evogpj.gp.Individual;
import evogpj.gp.Population;

/**
 * Created by stevenfine on 1/10/17.
 */
public class ProgramSizeFitness extends FitnessFunction {

    public static final String FITNESS_KEY = Parameters.Operators.PROGRAM_SIZE_FITNESS;
    public final Boolean isMaximizingFunction = false;

    @Override
    public Boolean isMaximizingFunction() {
        return this.isMaximizingFunction;
    }

    private void eval(Individual ind) {
        Tree t = (Tree) ind.getGenotype();
        Double size = (double) t.getSize();
        double fitness = 1.0 - 1.0/size;
        if (fitness < FitnessFunction.PRECISION) {
            fitness = 0;
        }
        ind.setFitness(ProgramSizeFitness.FITNESS_KEY, fitness);
    }

    @Override
    public void evalPop(Population pop) {
        for (Individual individual : pop) {
            this.eval(individual);
        }
    }
}
