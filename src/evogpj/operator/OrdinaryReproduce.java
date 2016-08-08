package evogpj.operator;

import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;

/**
 * Created by stevenfine on 7/27/16.
 */
public class OrdinaryReproduce implements Reproduce {

    protected final MersenneTwisterFast rand;
    protected final Select select;
    protected final Mutate mutate;
    protected final Crossover xover;

    protected final double MUTATION_RATE;
    protected final double XOVER_RATE;
    protected final int POP_SIZE;

    public OrdinaryReproduce(
            MersenneTwisterFast rand,
            Select select,
            Mutate mutate,
            Crossover xover,
            double mutationRate,
            double xoverRate,
            int popSize
    ) {

        this.rand = rand;
        this.select = select;
        this.mutate = mutate;
        this.xover = xover;

        MUTATION_RATE = mutationRate;
        XOVER_RATE = xoverRate;
        POP_SIZE = popSize;

    }

    public void addChildren(Population childPop, Population pop) throws GPException {
        Individual p1 = select.select(pop);
        double prob = rand.nextDouble();
        // Select exactly one operator to use
        if (prob < XOVER_RATE) {
            Individual p2 = select.select(pop);
            Population children = xover.crossOver(p1, p2);
            for (Individual ind : children) {
                if (!ind.equals(p1) && !ind.equals(p2) && (childPop.size() < POP_SIZE)) {
                    childPop.add(ind);
                }
            }
        } else if (prob < MUTATION_RATE + XOVER_RATE) {
            Individual ind = mutate.mutate(p1);
            if (!ind.equals(p1) && (childPop.size() < POP_SIZE)) {
                childPop.add(ind);
            }
        }
    }
}
