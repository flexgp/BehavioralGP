package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Population;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stevenfine on 1/11/17.
 */
public class BP4AFitnessFunctionEvaluator implements FitnessFunctionEvaluator {

    protected List<FitnessFunction> noModelFitnessFunctions = new ArrayList<>();
    protected List<FitnessFunction> modelFitnessFunctions = new ArrayList<>();
    protected Model model;
    protected Archive archive;

    public BP4AFitnessFunctionEvaluator(
            LinkedHashMap<String, FitnessFunction> fitnessFunctions,
            Model model,
            Archive archive
    ) {
        this.model = model;
        this.archive = archive;
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
        Map<ImmutableList<Double>, TreeNode> geneticMaterial = model.getProcessedGeneticMaterial();
        Map<ImmutableList<Double>, Double> weights = model.getWeights();
        archive.addGeneticMaterial(geneticMaterial, weights);
    }
}
