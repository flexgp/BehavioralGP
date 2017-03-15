package evogpj.evaluation.java;

import evogpj.algorithm.Parameters;
import evogpj.evaluation.FitnessFunction;
import evogpj.evaluation.IndividualModelValueNotDefinedException;
import evogpj.evaluation.Model;
import evogpj.gp.Individual;
import evogpj.gp.Population;

/**
 * Uses {@link Model#getModelContribution(Individual)} as the
 * {@link Individual}'s fitness value.
 *
 * @author Steven Fine
 */
public class ModelContributionFitness extends ModelFitnessFunction {

    public static final String FITNESS_KEY = Parameters.Operators.MODEL_CONTRIBUTION_FITNESS;
    public final Boolean isMaximizingFunction = false;

    public ModelContributionFitness(Model model) {
        super(model);
    }

    @Override
    public Boolean isMaximizingFunction() {
        return this.isMaximizingFunction;
    }

    private void eval(Individual ind) {
        double fitness = 1.0;
        try {
            fitness = model.getModelContribution(ind);
        } catch (IndividualModelValueNotDefinedException e) {
            e.printStackTrace();
        }
        if (fitness < FitnessFunction.PRECISION) {
            fitness = 0;
        }
        ind.setFitness(ModelContributionFitness.FITNESS_KEY, fitness);
    }

    @Override
    public void  evalPop(Population pop) {
        for (Individual ind : pop) {
            this.eval(ind);
        }
    }
}
