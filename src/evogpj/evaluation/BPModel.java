package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;

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
     * @param popGeneticMaterial The Population's genetic material.
     */
    void buildModel(List<Map<ImmutableList<Double>, TreeNode>> popGeneticMaterial);

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
     * @return The model error.
     */
    double getModelError();

    /**
     * @return The model complexity.
     */
    double getModelComplexity();
}
