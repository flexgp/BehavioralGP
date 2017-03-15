package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.MersenneTwisterFast;

import java.util.*;

/**
 * Implementation of the archive used in BGP.
 *
 * @author Steven Fine
 */
public class BPArchive extends WeightedArchive {

    public BPArchive(MersenneTwisterFast rand, int capacity) {
        super(rand, capacity);
    }

    public void addGeneticMaterial(
            Map<ImmutableList<Double>, TreeNode> geneticMaterial,
            Map<ImmutableList<Double>, Double> weights
    ) {
        Map<ImmutableList<Double>, TreeNode> allSubtrees = new HashMap<>();
        Map<ImmutableList<Double>, Double> allWeights = new HashMap<>();
        allSubtrees.putAll(archiveStorage);
        allWeights.putAll(archiveWeights);

        for (Map.Entry entry : geneticMaterial.entrySet()) {
            ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
            TreeNode syntax = geneticMaterial.get(semantics);
            double weight = weights.get(semantics);
            if (allSubtrees.containsKey(semantics)) {
                int oldSize = allSubtrees.get(semantics).getSubtreeSize();
                int newSize = syntax.getSubtreeSize();
                if (newSize < oldSize) {
                    allSubtrees.put(semantics, syntax);
                    allWeights.put(semantics, weight);
                }
            } else {
                allSubtrees.put(semantics, syntax);
                allWeights.put(semantics, weight);
            }
        }

        this.clearArchive();

        if (allSubtrees.size() <= CAPACITY) {
            for (Map.Entry entry : allSubtrees.entrySet()) {
                ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
                TreeNode syntax = allSubtrees.get(semantics);
                double weight = allWeights.get(semantics);
                try {
                    this.addSubtree(semantics, syntax, weight);
                } catch (FullArchiveException fae) {
                    fae.printStackTrace();
                }
            }
        } else {
            // cc Efraimidis and Spirakis (2006)
            Map<ImmutableList<Double>, Double> semanticsToTransformedWeights = new HashMap<>();
            for (Map.Entry entry : allSubtrees.entrySet()) {
                ImmutableList<Double> semantics = (ImmutableList<Double>) entry.getKey();
                double nextExpSample = -Math.log(1 - rand.nextDouble());
                double transformedWeight = nextExpSample/allWeights.get(semantics);
                semanticsToTransformedWeights.put(semantics, transformedWeight);
            }
            TreeMap<ImmutableList<Double>, Double> treeMap = new TreeMap<>(
                    new WeightComparator(semanticsToTransformedWeights)
            );
            treeMap.putAll(semanticsToTransformedWeights);
            Set<ImmutableList<Double>> sortedSemantics = treeMap.keySet();
            for (ImmutableList<Double> semantics : sortedSemantics) {
                TreeNode syntax = allSubtrees.get(semantics);
                double weight = allWeights.get(semantics);
                try {
                    this.addSubtree(semantics, syntax, weight);
                } catch (FullArchiveException fae) {
                    break;
                }
            }
        }
    }

    private static class WeightComparator implements Comparator<ImmutableList<Double>> {

        Map<ImmutableList<Double>, Double> map = new HashMap<>();

        public WeightComparator(Map<ImmutableList<Double>, Double> map) {
            this.map.putAll(map);
        }

        @Override
        public int compare(ImmutableList<Double> l1, ImmutableList<Double> l2) {
            double v1 = map.get(l1);
            double v2 = map.get(l2);
            if (v1 <= v2) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
