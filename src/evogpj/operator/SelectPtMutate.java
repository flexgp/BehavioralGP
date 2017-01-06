package evogpj.operator;

import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;

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
     * Select point (node) in the given tree.
     *
     * @param t Tree to select from
     * @return Chosen node
     */
    protected abstract TreeNode selectMutationPt(Tree t);

    /**
     * Optional implementation of selectMutationPt.
     * Select point (node) uniformly at random in the given tree.
     *
     * @param t Tree to select from
     * @return Chosen node
     */
    protected TreeNode selectUniformNode(Tree t) {
        List<TreeNode> nodes = t.getRoot().depthFirstTraversal();
        int whichNode = rand.nextInt(nodes.size());
        return nodes.get(whichNode);
    }

    /**
     * Optional implementation of selectMutationPt.
     * Select depth uniformly at random in the given tree, then select point
     * (node) uniformly at random at given depth.
     *
     * @param t Tree to select from
     * @return Chosen node
     */
    protected TreeNode selectUniformDepth(Tree t) {
        int treeDepth = t.getDepth();
        int whichDepth = rand.nextInt(treeDepth + 1);
        List<TreeNode> nodes = t.getRoot().depthFirstTraversal();
        List<TreeNode> nodesAtGivenDepth = new ArrayList<>();
        for (TreeNode n : nodes) {
            if (n.getDepth() == whichDepth) {
                nodesAtGivenDepth.add(n);
            }
        }
        int whichNode = rand.nextInt(nodesAtGivenDepth.size());
        return nodesAtGivenDepth.get(whichNode);
    }
}
