package evogpj.evaluation.java;

import evogpj.algorithm.Parameters;
import evogpj.evaluation.FitnessFunction;
import evogpj.evaluation.Model;
import evogpj.evaluation.IndividualModelValueNotDefinedException;
import evogpj.gp.Individual;
import evogpj.gp.Population;

/**
 * Created by stevenfine on 1/10/17.
 */
public class ModelErrorFitness extends ModelFitnessFunction {

    public static final String FITNESS_KEY = Parameters.Operators.MODEL_ERROR_FITNESS;
    public final Boolean isMaximizingFunction = false;

    public ModelErrorFitness(Model model) {
        super(model);
    }

    @Override
    public Boolean isMaximizingFunction() {
        return this.isMaximizingFunction;
    }

    private void eval(Individual ind) {
        double fitness = 1.0;
        try {
            fitness = model.getModelError(ind);
        } catch (IndividualModelValueNotDefinedException e) {
            e.printStackTrace();
        }
        if (fitness < FitnessFunction.PRECISION) {
            fitness = 0;
        }
        ind.setFitness(ModelErrorFitness.FITNESS_KEY, fitness);
    }

    @Override
    public void  evalPop(Population pop) {
        for (Individual ind : pop) {
            this.eval(ind);
        }
    }
}
