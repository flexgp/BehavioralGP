package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeGenerator;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;
import evogpj.operator.RandomOperator;

import java.util.*;

/**
 * Created by stevenfine on 9/19/16.
 */
public abstract class WeightedArchive extends RandomOperator implements Archive {

    protected final int CAPACITY;
    protected Map<ImmutableList<Double>, TreeNode> archiveStorage;
    protected Map<ImmutableList<Double>, Double> archiveWeights;
    protected NavigableMap<Double, ImmutableList<Double>> cumulativeWeights;
    protected double totalWeight;

    /**
     * Abstract weighted Archive
     * @param rand random number generator
     * @param capacity maximum capacity of the Archive
     */
    public WeightedArchive(MersenneTwisterFast rand, int capacity) {
        super(rand);
        CAPACITY = capacity;
        archiveStorage = new HashMap<>();
        archiveWeights = new HashMap<>();
        cumulativeWeights = new TreeMap<>();
        totalWeight = 0;
    }

    public TreeNode getSubtree() throws EmptyArchiveException {
        if (archiveStorage.size() == 0) {
            throw new EmptyArchiveException();
        } else {
            double cumulativeWeight = rand.nextDouble() * totalWeight;
            ImmutableList<Double> semantics = cumulativeWeights.ceilingEntry(cumulativeWeight).getValue();
            TreeNode syntax = archiveStorage.get(semantics);
            return TreeGenerator.generateTree(syntax.toStringAsTree()).getRoot();
        }
    }

    /**
     * Duplicate then insert subtree into Archive.
     * @param semantics The output vector of the subtree on the training samples.
     * @param syntax The subtree to be inserted.
     * @param weight The weight of the subtree to be inserted.
     * @throws FullArchiveException when Archive is full.
     */
    protected void addSubtree(ImmutableList<Double> semantics, TreeNode syntax, double weight) throws FullArchiveException {
        if (weight <= 0) {
            // Do nothing
        } else if (archiveStorage.size() >= CAPACITY) {
            throw new FullArchiveException();
        } else {
            TreeNode duplicate = TreeGenerator.generateTree(syntax.toStringAsTree()).getRoot();
            archiveWeights.put(semantics, weight);
            archiveStorage.put(semantics, duplicate);
            totalWeight += weight;
            cumulativeWeights.put(totalWeight, semantics);
        }
    }

    /**
     * Clear the Archive.
     */
    protected void clearArchive() {
        archiveStorage = new HashMap<>();
        archiveWeights = new HashMap<>();
        cumulativeWeights = new TreeMap<>();
        totalWeight = 0;
    }

}
