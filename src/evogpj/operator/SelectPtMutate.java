package evogpj.operator;

import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;

/**
 * Created by stevenfine on 1/5/17.
 */
public abstract class SelectPtMutate implements Mutate {

    /**
     * Select point (node) uniformly in the given tree.
     *
     * @param t Tree to select from
     * @return Chosen node
     */
    protected abstract TreeNode selectMutationPt(Tree t);
}
