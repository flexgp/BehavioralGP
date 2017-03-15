package evogpj.evaluation;

import evogpj.gp.Population;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * In addition to evaluating all of the {@link FitnessFunction}s for each
 * {@link evogpj.gp.Individual} in the {@link Population}, builds a
 * {@link Model} on the collected genetic material. Some of the
 * {@link FitnessFunction}s may depend on the {@link Model}.
 *
 * @author Steven Fine
 */
public class BP4FitnessFunctionEvaluator implements FitnessFunctionEvaluator {

    protected List<FitnessFunction> noModelFitnessFunctions = new ArrayList<>();
    protected List<FitnessFunction> modelFitnessFunctions = new ArrayList<>();
    protected Model model;

    public BP4FitnessFunctionEvaluator(
            LinkedHashMap<String, FitnessFunction> fitnessFunctions,
            Model model
    ) {
        this.model = model;
        for (String name : fitnessFunctions.keySet()) {
            FitnessFunction f = fitnessFunctions.get(name);
            if (f.requiresModel()) {
                modelFitnessFunctions.add(f);
            } else {
                noModelFitnessFunctions.add(f);
            }
        }
    }

    @Override
    public void evalPop(Population pop) {
        for (FitnessFunction f : noModelFitnessFunctions) {
            f.evalPop(pop);
        }
        model.buildModel(pop);
        for (FitnessFunction f: modelFitnessFunctions) {
            f.evalPop(pop);
        }
    }
}
