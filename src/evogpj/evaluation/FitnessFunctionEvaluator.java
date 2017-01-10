package evogpj.evaluation;

import evogpj.gp.Population;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by stevenfine on 1/9/17.
 */
public class FitnessFunctionEvaluator {

    protected LinkedHashMap<String, FitnessFunction> fitnessFunctions;

    public FitnessFunctionEvaluator(LinkedHashMap<String, FitnessFunction> fitnessFunctions) {
        this.fitnessFunctions = fitnessFunctions;
    }

    public void evalPop(Population pop) {
        for (String name : fitnessFunctions.keySet()) {
            FitnessFunction f = fitnessFunctions.get(name);
            f.evalPop(pop);
        }
    }
}
