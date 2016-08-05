package evogpj.evaluation;


import evogpj.genotype.TreeNode;

import java.util.List;
import java.util.Map;

/**
 * Created by stevenfine on 8/3/16.
 */
public interface Archive {

    /**
     * Takes the genetic material, and combines it with the archive.
     * @param geneticMaterial A list of subtrees, each represented as a map of
     *                        its output of the training data to its syntax.
     */
    void addGeneticMaterial(List<Map<List<Double>, TreeNode>> geneticMaterial);

    /**
     * Get a subtree from the archive.
     * @return The subtree that is retrieved from the archive.
     */
    TreeNode getSubtree();
}
