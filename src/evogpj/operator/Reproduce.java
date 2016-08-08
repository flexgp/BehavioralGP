package evogpj.operator;

import evogpj.gp.GPException;
import evogpj.gp.Population;

/**
 * Created by stevenfine on 7/27/16.
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
