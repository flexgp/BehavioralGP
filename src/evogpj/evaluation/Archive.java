package evogpj.evaluation;


import evogpj.genotype.TreeNode;

import java.util.List;

/**
 * Created by stevenfine on 8/3/16.
 */
public interface Archive {

    /**
     * Add a subtree to the archive.
     * @param semantics The part of the trace corresponding to the subtree.
     * @param syntax The TreeNode representing the subtree.
     */
    void addSubtree(List<Double> semantics, TreeNode syntax);

    /**
     * Get a subtree from the archive.
     * @return The subtree that is retrieved from the archive.
     */
    TreeNode getSubtree();

    /**
     * Refresh the archive.
     */
    void refresh();
}
