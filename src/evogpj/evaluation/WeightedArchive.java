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

    private Map<ImmutableList<Double>, TreeNode> archiveStorage;
    private NavigableMap<Double, ImmutableList<Double>> weights;
    private NavigableMap<Double, ImmutableList<Double>> cumulativeWeights;
    private double totalWeight;
    private boolean reComputeCumulative;

    /**
     * Abstract weighted Archive
     * @param rand random number generator
     */
    public WeightedArchive(MersenneTwisterFast rand) {
        super(rand);
        archiveStorage = new HashMap<>();
        weights = new TreeMap<>();
        cumulativeWeights = new TreeMap<>();
        totalWeight = 0;
        reComputeCumulative = false;
    }

    public TreeNode getSubtree() throws EmptyArchiveException {
        if (archiveStorage.size() == 0) {
            throw new EmptyArchiveException();
        } else {
            if (reComputeCumulative) {
                totalWeight = 0;
                cumulativeWeights.clear();
                for (Map.Entry entry : weights.entrySet()) {
                    double weight = (double) entry.getKey();
                    ImmutableList<Double> value = (ImmutableList<Double>) entry.getValue();
                    totalWeight += weight;
                    cumulativeWeights.put(totalWeight, value);
                }
                reComputeCumulative = false;
            }
            double cumulativeWeight = rand.nextDouble() * totalWeight;
            ImmutableList<Double> semantics = cumulativeWeights.ceilingEntry(cumulativeWeight).getValue();
            TreeNode subtree = archiveStorage.get(semantics);
            return TreeGenerator.generateTree(subtree.toStringAsTree()).getRoot();
        }
    }

    /**
     * Insert subtree into Archive. If Archive is full, and smallest weight is
     * less than weight parameter, remove subtree with smallest weight before
     * inserting new subtree. If Archive is full, and smallest weight is greater
     * than weight parameter, do nothing.
     * @param weight The weight of the subtree to be inserted.
     * @param semantics The output vector of the subtree on the training samples.
     * @param syntax The subtree to be inserted.
     */
    protected void addSubtree(double weight, ImmutableList<Double> semantics, TreeNode syntax) {
        if (weight <= 0) {
            // Do nothing
        } else if (archiveStorage.size() >= Archive.CAPACITY) {
            double smallestWeight = weights.firstKey();
            if (smallestWeight < weight) {
                ImmutableList<Double> value = weights.get(smallestWeight);
                weights.remove(smallestWeight);
                weights.put(weight, semantics);
                archiveStorage.remove(value);
                archiveStorage.put(semantics, syntax);
                reComputeCumulative = true;
            }
        } else {
            weights.put(weight, semantics);
            archiveStorage.put(semantics, syntax);
            totalWeight += weight;
            cumulativeWeights.put(totalWeight, semantics);
        }
    }

}
