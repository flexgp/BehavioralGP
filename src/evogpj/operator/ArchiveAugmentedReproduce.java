package evogpj.operator;

import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;

import java.util.HashSet;
import java.util.Set;

/**
 * Generate new {@link Individual}s by using either the {@link Mutate} operator,
 * the {@link Crossover}, or the {@link ArchiveMutate} operator, each with an
 * associated probability.
 *
 * @author Steven Fine
 */
public class ArchiveAugmentedReproduce extends RandomOperator implements Reproduce {

    protected final Select select;
    protected final Mutate ordinaryMutate;
    protected final Crossover xover;
    protected final ArchiveMutate archiveMutate;

    protected final double MUTATION_RATE;
    protected final double XOVER_RATE;
    protected final double ARCHIVE_MUTATION_RATE;
    protected final int POP_SIZE;

    protected final boolean allowDuplicateTrees;
    protected final Set<String> existingTrees = new HashSet<>();
    protected boolean resetExistingTreesIfChildPopIsEmpty = true;

    /**
     * Reproduce operator that in addition to performing Mutate and Crossover
     * performs ArchiveMutate
     * @param rand
     * @param select
     * @param ordinaryMutate
     * @param xover
     * @param archiveMutate
     * @param mutationRate
     * @param xoverRate
     * @param archiveMutationRate
     * @param popSize
     * @param allowDuplicateTrees
     */
    public ArchiveAugmentedReproduce(
            MersenneTwisterFast rand,
            Select select,
            Mutate ordinaryMutate,
            Crossover xover,
            ArchiveMutate archiveMutate,
            double mutationRate,
            double xoverRate,
            double archiveMutationRate,
            int popSize,
            boolean allowDuplicateTrees
    ) {
        super(rand);
        this.select = select;
        this.ordinaryMutate = ordinaryMutate;
        this.xover = xover;
        this.archiveMutate = archiveMutate;
        this.allowDuplicateTrees = allowDuplicateTrees;

        MUTATION_RATE = mutationRate;
        XOVER_RATE = xoverRate;
        ARCHIVE_MUTATION_RATE = archiveMutationRate;
        POP_SIZE = popSize;
    }

    public void addChildren(Population childPop, Population pop) throws GPException {
        if (!allowDuplicateTrees && resetExistingTreesIfChildPopIsEmpty && childPop.size() == 0) {
            resetExistingTreesIfChildPopIsEmpty = false;
            existingTrees.clear();
            for (Individual ind : pop) {
                existingTrees.add(ind.toString());
            }
        }

        Individual p1 = select.select(pop);
        double prob = rand.nextDouble();
        // Select exactly one operator to use
        if (prob < XOVER_RATE) {
            Individual p2 = select.select(pop);
            Population children = xover.crossOver(p1, p2);
            for (Individual ind : children) {
                if (allowDuplicateTrees && !ind.equals(p1) && !ind.equals(p2) && (childPop.size() < POP_SIZE)) {
                    childPop.add(ind);
                } else if (!allowDuplicateTrees && !existingTrees.contains(ind.toString())) {
                    childPop.add(ind);
                    existingTrees.add(ind.toString());
                    resetExistingTreesIfChildPopIsEmpty = true;
                }
            }
        } else if (prob < MUTATION_RATE + XOVER_RATE) {
            Individual ind = ordinaryMutate.mutate(p1);
            if (allowDuplicateTrees && !ind.equals(p1) && childPop.size() < POP_SIZE) {
                childPop.add(ind);
            } else if (!allowDuplicateTrees && !existingTrees.contains(ind.toString())) {
                childPop.add(ind);
                existingTrees.add(ind.toString());
                resetExistingTreesIfChildPopIsEmpty = true;
            }
        } else if (prob < ARCHIVE_MUTATION_RATE + MUTATION_RATE + XOVER_RATE) {
            Individual ind = archiveMutate.mutate(p1);
            if (allowDuplicateTrees && !ind.equals(p1) && childPop.size() < POP_SIZE) {
                childPop.add(ind);
            } else if (!allowDuplicateTrees && !existingTrees.contains(ind.toString())) {
                childPop.add(ind);
                existingTrees.add(ind.toString());
                resetExistingTreesIfChildPopIsEmpty = true;
            }
        }
    }
}
