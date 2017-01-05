package evogpj.operator;

import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;

/**
 * Created by stevenfine on 1/5/17.
 */
public abstract class SelectPtMutate extends RandomOperator implements Mutate {

    /**
     * Constructor for SelectPtMutate
     *
     * @param rand random number generator
     */
    public SelectPtMutate(MersenneTwisterFast rand) {
        super(rand);
    }

    /**
     * Select point (node) uniformly in the given tree.
     *
     * @param t Tree to select from
     * @return Chosen node
     */
    protected abstract TreeNode selectMutationPt(Tree t);
}
