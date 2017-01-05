package evogpj.evaluation;

import evogpj.genotype.TreeGenerator;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;
import evogpj.operator.RandomOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenfine on 9/12/16.
 */
public abstract class UnweightedArchive extends RandomOperator implements Archive {

    protected List<TreeNode> archive;

    /**
     * Abstract unweighted Archive
     * @param rand random number generator
     */
    public UnweightedArchive(MersenneTwisterFast rand) {
        super(rand);
        archive = new ArrayList<>();
    }

    public TreeNode getSubtree() throws EmptyArchiveException {
        if (archive.size() == 0) {
            throw new EmptyArchiveException();
        } else {
            int index = rand.nextInt(archive.size());
            TreeNode node = archive.get(index);
            return node;
        }
    }
}
