package evogpj.evaluation;

import evogpj.gp.Population;

import java.util.LinkedHashMap;

/**
 * Created by stevenfine on 1/11/17.
 */
public class OrdinaryFitnessFunctionEvaluator implements FitnessFunctionEvaluator  {

    protected LinkedHashMap<String, FitnessFunction> fitnessFunctions;

    public OrdinaryFitnessFunctionEvaluator(LinkedHashMap<String, FitnessFunction> fitnessFunctions) {
        this.fitnessFunctions = fitnessFunctions;
    }

    public void evalPop(Population pop) {
        for (String name : fitnessFunctions.keySet()) {
            FitnessFunction f = fitnessFunctions.get(name);
            f.evalPop(pop);
        }
    }
}
