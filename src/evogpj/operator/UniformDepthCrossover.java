package evogpj.operator;

import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Extension of {@link SinglePointUniformCrossover} which determines the
 * crossover point by first selecting the depth of the crossover point in the
 * tree uniformly at random.  Then selecting the subtree uniformly at random at
 * the given depth.
 *
 * @author Steven Fine
 */
public class UniformDepthCrossover extends SinglePointUniformCrossover {

    public UniformDepthCrossover(MersenneTwisterFast rand, Properties props) {
        super(rand, props);
    }

    /**
     * Implement our biased point selection.
     */
    @Override
    protected TreeNode selectXOverPt(Tree t) {
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
