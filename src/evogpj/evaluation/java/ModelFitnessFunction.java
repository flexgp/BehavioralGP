package evogpj.evaluation.java;

import evogpj.evaluation.BPModel;
import evogpj.evaluation.FitnessFunction;

/**
 * Created by stevenfine on 1/11/17.
 */
public abstract class ModelFitnessFunction extends FitnessFunction {

    protected BPModel model;

    public ModelFitnessFunction(BPModel model) {
        this.model = model;
    }

    @Override
    public Boolean requiresModel() {
        return true;
    }
}
