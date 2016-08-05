package evogpj.evaluation;


import com.google.common.collect.ImmutableList;
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
     */
    void addGeneticMaterial(Map<ImmutableList<Double>, TreeNode> geneticMaterial);

    /**
     * Get a subtree from the archive.
     * @return The subtree that is retrieved from the archive.
     */
    TreeNode getSubtree();
}
