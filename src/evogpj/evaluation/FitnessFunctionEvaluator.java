package evogpj.evaluation;

import evogpj.gp.Population;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by stevenfine on 1/9/17.
 */
public interface FitnessFunctionEvaluator {

    void evalPop(Population pop);
}
