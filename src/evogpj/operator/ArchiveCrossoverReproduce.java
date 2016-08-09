package evogpj.operator;

import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;

/**
 * Created by stevenfine on 8/9/16.
 */
public class ArchiveCrossoverReproduce extends RandomOperator implements Reproduce {

    protected final Select select;
    protected final Mutate ordinaryMutate;
    protected final ArchiveMutate archiveMutate;

    protected final double MUTATION_RATE;
    protected final double ARCHIVE_MUTATION_RATE;
    protected final int POP_SIZE;

    /**
     * Reproduce operator that replaces Crossover with ArchiveMutate.
     * @param rand
     * @param select
     * @param ordinaryMutate
     * @param archiveMutate
     * @param mutationRate
     * @param archiveMutationRate
     * @param popSize
     */
    public ArchiveCrossoverReproduce(
            MersenneTwisterFast rand,
            Select select,
            Mutate ordinaryMutate,
            ArchiveMutate archiveMutate,
            double mutationRate,
            double archiveMutationRate,
            int popSize
    ) {
        super(rand);
        this.select = select;
        this.ordinaryMutate = ordinaryMutate;
        this.archiveMutate = archiveMutate;

        MUTATION_RATE = mutationRate;
        ARCHIVE_MUTATION_RATE = archiveMutationRate;
        POP_SIZE = popSize;
    }

    public void addChildren(Population childPop, Population pop) throws GPException {
        Individual ind = select.select(pop);
        double prob = rand.nextDouble();
        // Perform twice because Crossover produces two new offspring.
        // Perhaps change doing this twice, or change ARCHIVE_MUTATION_RATE.
        if (prob < ARCHIVE_MUTATION_RATE) {
            for (int i = 0; i < 2; i++) {
                Individual mutant = archiveMutate.mutate(ind);
                if (!mutant.equals(ind) && childPop.size() < POP_SIZE) {
                    childPop.add(mutant);
                }
            }
        } else if (prob < MUTATION_RATE + ARCHIVE_MUTATION_RATE) {
            Individual mutant = ordinaryMutate.mutate(ind);
            if (!mutant.equals(ind) && childPop.size() < POP_SIZE) {
                childPop.add(mutant);
            }
        }
    }
}
