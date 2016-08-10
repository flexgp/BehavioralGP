package evogpj.operator;

import evogpj.algorithm.Parameters;
import evogpj.evaluation.Archive;
import evogpj.evaluation.EmptyArchiveException;
import evogpj.genotype.Tree;
import evogpj.genotype.TreeNode;
import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by stevenfine on 8/8/16.
 */
public class ArchiveMutate extends RandomOperator implements Mutate {

    private final int TREE_XOVER_MAX_DEPTH;
    private final int TREE_XOVER_TRIES;
    private final Archive archive;

    public ArchiveMutate(MersenneTwisterFast rand, Properties props, Archive archive) {
        super(rand);
        this.archive = archive;

        if (props.containsKey(Parameters.Names.TREE_XOVER_MAX_DEPTH)) {
            TREE_XOVER_MAX_DEPTH = Integer.valueOf(props.getProperty(Parameters.Names.TREE_XOVER_MAX_DEPTH));
        } else {
            TREE_XOVER_MAX_DEPTH = Parameters.Defaults.TREE_XOVER_MAX_DEPTH;
        }
        if (props.containsKey(Parameters.Names.TREE_XOVER_TRIES)) {
            TREE_XOVER_TRIES = Integer.valueOf(props.getProperty(Parameters.Names.TREE_XOVER_TRIES));
        } else {
            TREE_XOVER_TRIES = Parameters.Defaults.TREE_XOVER_TRIES;
        }
    }

    @Override
    public Individual mutate(Individual i) throws GPException {
        if (!(i.getGenotype() instanceof Tree)) {
            throw new GPException("Attempting ArchiveMutate of genotype not of type Tree.");
        }
        Tree copy;
        int tries = 0;
        do {
            copy = (Tree) i.getGenotype().copy();
            ArrayList<TreeNode> nodes = copy.getRoot().depthFirstTraversal();
            TreeNode mutatePoint = nodes.get(rand.nextInt(nodes.size()));
            int mutatePointIndexInChildren = mutatePoint.parent.children.indexOf(mutatePoint);
            TreeNode replacementNode;
            try {
                replacementNode = archive.getSubtree();
            } catch (EmptyArchiveException e) {
                e.printStackTrace();
                return new Individual(copy); // Perhaps revert to ordinary Crossover?
            }
            mutatePoint.parent.children.set(mutatePointIndexInChildren, replacementNode);
            replacementNode.parent = mutatePoint.parent;
            replacementNode.reset();
            tries++;
        } while (copy.getDepth() > TREE_XOVER_MAX_DEPTH && tries < TREE_XOVER_TRIES);

        if (tries >= TREE_XOVER_TRIES) {
            return i.copy();
        } else {
            Individual mutant = new Individual(copy);
            mutant.reset();
            return mutant;
        }
    }
}
