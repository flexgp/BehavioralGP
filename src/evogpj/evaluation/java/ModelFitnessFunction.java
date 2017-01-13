package evogpj.evaluation.java;

import evogpj.evaluation.Model;
import evogpj.evaluation.FitnessFunction;

/**
 * Created by stevenfine on 1/11/17.
 */
public abstract class ModelFitnessFunction extends FitnessFunction {

    protected Model model;

    public ModelFitnessFunction(Model model) {
        this.model = model;
    }

    @Override
    public Boolean requiresModel() {
        return true;
    }
}
