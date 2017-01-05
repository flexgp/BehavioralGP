package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeGenerator;
import evogpj.genotype.TreeNode;

import java.util.*;

import evogpj.gp.MersenneTwisterFast;

/**
 * Created by stevenfine on 8/3/16.
 */
public class SimpleArchive extends UnweightedArchive {

    /**
     * Construct a new Archive that simply overwrites itself each time genetic
     * material is added.
     * @param rand random number generator.
     */
    public SimpleArchive(MersenneTwisterFast rand) {
        super(rand);
    }

    public void addGeneticMaterial(List<TreeNode> subtrees,
                            List<ImmutableList<Double>> trace,
                            List<Double> weights) {
        archive.clear();
        for (int i = 0; i < Archive.CAPACITY; i++) {
            TreeNode node = subtrees.get(i);
            TreeNode duplicateNode = TreeGenerator.generateTree(node.toStringAsTree()).getRoot();
            archive.add(duplicateNode);
        }
    }
}
