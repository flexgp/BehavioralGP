package evogpj.evaluation;

import evogpj.gp.Population;

import java.util.LinkedHashMap;

/**
 * Simply evaluates all of the {@link FitnessFunction}s for each
 * {@link evogpj.gp.Individual} in the {@link Population} used in the genetic
 * programming run.
 *
 * @author Steven Fine
 */
public class OrdinaryFitnessFunctionEvaluator implements FitnessFunctionEvaluator  {

    protected LinkedHashMap<String, FitnessFunction> fitnessFunctions;

    public OrdinaryFitnessFunctionEvaluator(LinkedHashMap<String, FitnessFunction> fitnessFunctions) {
        this.fitnessFunctions = fitnessFunctions;
    }

    @Override
    public void evalPop(Population pop) {
        for (String name : fitnessFunctions.keySet()) {
            FitnessFunction f = fitnessFunctions.get(name);
            f.evalPop(pop);
        }
    }
}
