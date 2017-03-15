package evogpj.evaluation.java;

import evogpj.evaluation.Model;
import evogpj.evaluation.FitnessFunction;

/**
 * Represents a {@link FitnessFunction} that requires a {@link Model} for its
 * evaluation.
 *
 * @author Steven Fine
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
