package evogpj.evaluation;

import evogpj.gp.Population;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * This interface is used to evaluate all of the {@link FitnessFunction}s for
 * all of the {@link evogpj.gp.Individual}s in a given {@link Population}, in
 * addition to performing any additional operations that are needed for the
 * specified genetic programming configuration.
 *
 * @author Steven Fine
 */
public interface FitnessFunctionEvaluator {

    /**
     * Evaluate the Population.
     * @param pop
     */
    void evalPop(Population pop);
}
