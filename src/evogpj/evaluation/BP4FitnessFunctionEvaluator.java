package evogpj.evaluation;

import evogpj.gp.Population;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by stevenfine on 1/11/17.
 */
public class BP4FitnessFunctionEvaluator implements FitnessFunctionEvaluator {

    protected List<FitnessFunction> noModelFitnessFunctions = new ArrayList<>();
    protected List<FitnessFunction> modelFitnessFunctions = new ArrayList<>();
    protected BPModel model;

    public BP4FitnessFunctionEvaluator(
            LinkedHashMap<String, FitnessFunction> fitnessFunctions,
            BPModel model
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
