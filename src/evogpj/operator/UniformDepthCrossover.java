package evogpj.operator;

import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Extend {@link SinglePointUniformCrossover} so that first we choose the
 * the cross-over depth uniformly at random in given tree, then select point
 * uniformly at random at given depth.
 *
 * Created by stevenfine on 1/6/17.
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
