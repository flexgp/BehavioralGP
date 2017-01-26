package evogpj.evaluation;


import com.google.common.collect.ImmutableList;
import evogpj.algorithm.Parameters;
import evogpj.genotype.TreeNode;

import java.util.List;
import java.util.Map;

/**
 * Created by stevenfine on 8/3/16.
 */
public interface Archive {

    /**
     * Takes the genetic material, and combines it with the archive.
     * @param geneticMaterial A map that represents a set of subtrees, where
     *                        each key is a subtree's output on the training
     *                        data, and each value is its syntax.
     * @param weights A map whose keys are the subtree's outputs as in the
     *                geneticMaterial parameter, and whose values are the
     *                weights that subtree should have in the Archive
     */
    void addGeneticMaterial(
            Map<ImmutableList<Double>, TreeNode> geneticMaterial,
            Map<ImmutableList<Double>, Double> weights
    );

    /**
     * Get a subtree from the archive.
     * @return A deep copy of the subtree that is retrieved from the archive.
     * @throws EmptyArchiveException if the Archive is empty.
     */
    TreeNode getSubtree() throws EmptyArchiveException;
}
