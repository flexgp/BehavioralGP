package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Population;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In addition to evaluating all of the {@link FitnessFunction}s for each
 * {@link evogpj.gp.Individual} in the {@link Population}, builds a
 * {@link Model} on the collected genetic material, and adds subtrees to the
 * {@link Archive} based on the {@link Model}.  Some of the
 * {@link FitnessFunction}s may depend on the {@link Model}.
 *
 * @author Steven Fine
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

    @Override
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
