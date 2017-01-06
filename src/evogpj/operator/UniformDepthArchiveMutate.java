package evogpj.operator;

import evogpj.evaluation.Archive;
import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;

import java.util.Properties;

/**
 * Created by stevenfine on 1/6/17.
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
