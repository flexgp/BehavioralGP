package evogpj.evaluation.java;

import evogpj.algorithm.Parameters;
import evogpj.evaluation.FitnessFunction;
import evogpj.evaluation.Model;
import evogpj.evaluation.IndividualModelValueNotDefinedException;
import evogpj.gp.Individual;
import evogpj.gp.Population;

/**
 * Uses {@link Model#getModelComplexity(Individual)} as the
 * {@link Individual}'s fitness value.
 *
 * @author Steven Fine
 */
public class ModelComplexityFitness extends ModelFitnessFunction {

    public static final String FITNESS_KEY = Parameters.Operators.MODEL_COMPLEXITY_FITNESS;
    public final Boolean isMaximizingFunction = false;

    public ModelComplexityFitness(Model model) {
        super(model);
    }

    @Override
    public Boolean isMaximizingFunction() {
        return this.isMaximizingFunction;
    }

    private void eval(Individual ind) {
        double fitness = 1.0;
        try {
            fitness = model.getModelComplexity(ind);
        } catch (IndividualModelValueNotDefinedException e) {
            e.printStackTrace();
        }
        if (fitness < FitnessFunction.PRECISION) {
            fitness = 0;
        }
        ind.setFitness(ModelComplexityFitness.FITNESS_KEY, fitness);
    }

    @Override
    public void  evalPop(Population pop) {
        for (Individual ind : pop) {
            this.eval(ind);
        }
    }
}
