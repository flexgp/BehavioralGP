package evogpj.evaluation.java;

import evogpj.algorithm.Parameters;
import evogpj.evaluation.BPModel;
import evogpj.gp.Individual;
import evogpj.gp.Population;

/**
 * Created by stevenfine on 1/10/17.
 */
public class ModelComplexityFitness extends ModelFitnessFunction {

    public static final String FITNESS_KEY = Parameters.Operators.MODEL_COMPLEXITY_FITNESS;
    public final Boolean isMaximizingFunction = false;

    public ModelComplexityFitness(BPModel model) {
        super(model);
    }

    @Override
    public Boolean isMaximizingFunction() {
        return this.isMaximizingFunction;
    }

    private void eval(Individual ind) {
        double fitness = model.getModelComplexity(ind);
        ind.setFitness(ModelComplexityFitness.FITNESS_KEY, fitness);
    }

    @Override
    public void  evalPop(Population pop) {
        for (Individual ind : pop) {
            this.eval(ind);
        }
    }
}