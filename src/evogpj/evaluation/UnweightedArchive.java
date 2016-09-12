package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeGenerator;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;
import evogpj.operator.RandomOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stevenfine on 9/12/16.
 */
public abstract class UnweightedArchive extends RandomOperator implements Archive {

    private Map<ImmutableList<Double>, TreeNode> archive;

    /**
     * Abstract unweighted Archive
     * @param rand random number generator
     */
    public UnweightedArchive(MersenneTwisterFast rand) {
        super(rand);
        archive = new HashMap<>();
    }

    public TreeNode getSubtree() throws EmptyArchiveException {
        if (archive.size() == 0) {
            throw new EmptyArchiveException();
        } else {
            List<ImmutableList<Double>> keys = new ArrayList<>(archive.keySet());
            int index = rand.nextInt(keys.size());
            ImmutableList<Double> subtree = keys.get(index);
            TreeNode node = archive.get(subtree);
            return TreeGenerator.generateTree(node.toStringAsTree()).getRoot();
        }
    }
}
