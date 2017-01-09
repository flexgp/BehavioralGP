package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
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

    public void addGeneticMaterial(Map<ImmutableList<Double>, TreeNode> geneticMaterial) {
        archive.clear();
        for (ImmutableList<Double> semantics : geneticMaterial.keySet()) {
            if (archive.size() < Archive.CAPACITY) {
                archive.put(semantics, geneticMaterial.get(semantics));
            }
        }
    }
}
