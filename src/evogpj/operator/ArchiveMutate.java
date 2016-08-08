package evogpj.operator;

import evogpj.algorithm.Parameters;
import evogpj.evaluation.Archive;
import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;

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
        return new Individual(i.getGenotype().copy());
    }
}
