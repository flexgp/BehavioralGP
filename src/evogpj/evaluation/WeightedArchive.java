package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;
import evogpj.operator.RandomOperator;

import java.util.*;

/**
 * Created by stevenfine on 9/19/16.
 */
public abstract class WeightedArchive extends RandomOperator implements Archive {

    private List<TreeNode> archiveStorage;
    private List<Double> weights;
    private NavigableMap<Double, Integer> cumulativeWeightsToIndex;
    private double cumulativeWeight;
    private int cumulativeIndex;
    private boolean reComputeCumulative;

    /**
     * Abstract weighted Archive
     * @param rand random number generator
     */
    public WeightedArchive(MersenneTwisterFast rand) {
        super(rand);
        archiveStorage = new ArrayList<>();
        weights = new ArrayList<>();
        cumulativeWeightsToIndex = new TreeMap<>();
        cumulativeWeight = 0;
        cumulativeIndex = -1;
        reComputeCumulative = false;
    }

    public TreeNode getSubtree() throws EmptyArchiveException {
        if (archiveStorage.size() == 0) {
            throw new EmptyArchiveException();
        } else {
            if (reComputeCumulative) {
                cumulativeWeight = 0;
                cumulativeIndex = -1;
                cumulativeWeightsToIndex.clear();
                for (double weight : weights) {
                    cumulativeWeight += weight;
                    cumulativeIndex++;
                    cumulativeWeightsToIndex.put(cumulativeWeight, cumulativeIndex);
                }
                reComputeCumulative = false;
            }
            double chosenWeight = rand.nextDouble() * cumulativeWeight;
            int index = cumulativeWeightsToIndex.ceilingEntry(chosenWeight).getValue();
            TreeNode subtree = archiveStorage.get(index);
            return subtree;
        }
    }

    /**
     * Insert subtree into Archive. If Archive is full, and smallest weight is
     * less than weight parameter, remove subtree with smallest weight before
     * inserting new subtree. If Archive is full, and smallest weight is greater
     * than weight parameter, do nothing.
     * @param syntax The subtree to be inserted.
     * @param semantics The output vector of the subtree on the training samples.
     * @param weight The weight of the subtree to be inserted.

     */
    protected void addSubtree(double weight, ImmutableList<Double> semantics, TreeNode syntax) {
        if (weight <= 0) {
            // Do nothing
        } else if (archiveStorage.size() >= MAX_SIZE) {
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
