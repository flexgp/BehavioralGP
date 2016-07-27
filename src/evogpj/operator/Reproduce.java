package evogpj.operator;

import evogpj.gp.GPException;
import evogpj.gp.Population;

/**
 * Created by stevenfine on 7/27/16.
 */
public interface Reproduce {
    void addChildren(Population childPop, Population pop) throws GPException;
}
