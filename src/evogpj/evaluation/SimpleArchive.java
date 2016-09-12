package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeGenerator;
import evogpj.genotype.TreeNode;

import java.util.*;

import evogpj.gp.MersenneTwisterFast;
import evogpj.operator.RandomOperator;

/**
 * Created by stevenfine on 8/3/16.
 */
public class SimpleArchive extends RandomOperator implements Archive {

    private Map<ImmutableList<Double>, TreeNode> archive;

    /**
     * Construct a new Archive that simply overwrites itself each time genetic
     * material is added.
     * @param rand random number generator.
     */
    public SimpleArchive(MersenneTwisterFast rand) {
        super(rand);
        archive = new HashMap<>();
    }

    public void addGeneticMaterial(Map<ImmutableList<Double>, TreeNode> geneticMaterial) {
        archive = geneticMaterial;
    }

    public TreeNode getSubtree() throws EmptyArchiveException {
        if (archive.size() == 0) {
            throw new EmptyArchiveException();
        } else {
            List<ImmutableList<Double>> keys = new ArrayList<>(archive.keySet());
            int index = rand.nextInt(keys.size());
            ImmutableList<Double> subtree = keys.get(index);
            TreeNode node = archive.get(subtree);
            return TreeGenerator.generateTree(node.toStringAsTree()).getRoot();
        }
    }
}
