package evogpj.evaluation;


import com.google.common.collect.ImmutableList;
import evogpj.algorithm.Parameters;
import evogpj.genotype.TreeNode;

import java.util.List;

/**
 * Created by stevenfine on 8/3/16.
 */
public interface Archive {

    int CAPACITY = Parameters.Defaults.ARCHIVE_CAPACITY;

    /**
     * Takes the genetic material, and combines it with the archive. A given
     * index in each List corresponds to a single subtree.
     * @param syntax The TreeNodes to be put in the Archive.
     * @param semantics The output of the TreeNode on each training data point.
     * @param weights The weight that the TreeNode should be given in the Archive.
     */
    void addGeneticMaterial(List<TreeNode> syntax,
                            List<ImmutableList<Double>> semantics,
                            List<Double> weights);

    /**
     * Get a subtree from the archive.
     * @return A deep copy of the subtree that is retrieved from the archive.
     */
    TreeNode getSubtree() throws EmptyArchiveException;
}
