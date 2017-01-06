package evogpj.operator;

import evogpj.genotype.Tree;
import evogpj.genotype.TreeGenerator;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by stevenfine on 1/6/17.
 */
public class UniformDepthMutate extends SubtreeMutate {

    /**
     * Construct Mutation operator that overrides the selectMutationPt method to
     * first select the mutation depth uniformly at random, then select the
     * mutation point at that depth.
     *
     * @param rand random number generator.
     * @param props object encoding system properties.
     * @param TGen generator to use for growing new subtrees.
     */
    public UniformDepthMutate(MersenneTwisterFast rand, Properties props, TreeGenerator TGen) {
        super(rand, props, TGen);
    }

    /**
     * Select depth uniformly at random in the given tree, then select point
     * (node) uniformly at random at given depth.
     *
     * @param t Tree to select from
     * @return Chosen node
     */
    @Override
    protected TreeNode selectMutationPt(Tree t) {
        return selectUniformDepth(t);
    }
}
