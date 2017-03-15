package evogpj.operator;

import evogpj.evaluation.Archive;
import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;

import java.util.Properties;

/**
 * Extension of {@link ArchiveMutate} which determines the mutation point by
 * first selecting the depth of the mutation point in the tree uniformly at
 * random.  Then selecting the subtree uniformly at random at the given depth.
 *
 * @author Steven Fine
 */
public class UniformDepthArchiveMutate extends ArchiveMutate {

    public UniformDepthArchiveMutate(MersenneTwisterFast rand, Properties props, Archive archive) {
        super(rand, props, archive);
    }

    @Override
    protected TreeNode selectMutationPt(Tree t) {
        return selectUniformDepth(t);
    }
}
