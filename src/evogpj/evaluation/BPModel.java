package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;

import java.util.List;
import java.util.Map;

/**
 * Created by stevenfine on 1/11/17.
 */
public interface BPModel {

    /**
     * Take in the Population's genetic material and build an ML model to
     * determine what should go in the Archive and to create additional fitness
     * measures.
     * @param pop The Population from which to build the model.
     */
    void buildModel(Population pop);

    /**
     * @return The genetic material that should be put in the Archive.
     */
    Map<ImmutableList<Double>, TreeNode> getProcessedGeneticMaterial();

    /**
     * @return A mapping of the semantics of the TreeNodes that should go in the
     * Archive to the weights they should be given in the Archive.
     */
    Map<ImmutableList<Double>, Double> getWeights();

    /**
     * @param individual
     * @return The model error for the specified Individual.
     */
    double getModelError(Individual individual);

    /**
     * @param individual
     * @return The model complexity for the specified Individual.
     */
    double getModelComplexity(Individual individual);
}
