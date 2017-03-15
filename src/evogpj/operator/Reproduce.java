package evogpj.operator;

import evogpj.gp.GPException;
import evogpj.gp.Population;

/**
 * Interface for creating new {@link evogpj.gp.Individual}s from old
 * {@link evogpj.gp.Individual}s.
 *
 * @author Steven Fine
 */
public interface Reproduce {

    /**
     * Add new Individuals to the |childPop| Population based on the Individuals
     * in the |pop| Population.
     * @param childPop The child Population
     * @param pop The parent Population
     * @throws GPException
     */
    void addChildren(Population childPop, Population pop) throws GPException;
}
