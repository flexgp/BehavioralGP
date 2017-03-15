package evogpj.evaluation;

import com.google.common.collect.ImmutableList;
import evogpj.genotype.TreeNode;
import evogpj.gp.Individual;
import evogpj.gp.Population;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the machine learning model used in Behavioral Genetic Programming.
 *
 * @author Steven Fine
 */
public interface Model {

    /**
     * Take in the {@link Population}'s genetic material and build an ML model to
     * determine what should go in the {@link Archive} and to create additional
     * fitness measures.
     * @param population The {@link Population} from which to build the model.
     */
    void buildModel(Population population);

    /**
     * @return The genetic material that should be put in the {@link Archive}.
     */
    Map<ImmutableList<Double>, TreeNode> getProcessedGeneticMaterial();

    /**
     * @return A mapping of the semantics of the {@link TreeNode}s that should
     * go in the {@link Archive} to the weights they should be given in the
     * {@link Archive}.
     */
    Map<ImmutableList<Double>, Double> getWeights();

    /**
     * @param individual
     * @return The model error for the specified {@link Individual}.
     * @throws IndividualModelValueNotDefinedException if this value is not
     * defined for the {@link Individual}.
     */
    double getModelError(Individual individual) throws IndividualModelValueNotDefinedException;

    /**
     * @param individual
     * @return The model complexity for the specified {@link Individual}.
     * @throws IndividualModelValueNotDefinedException if this value is not
     * defined for the {@link Individual}.
     */
    double getModelComplexity(Individual individual) throws IndividualModelValueNotDefinedException;

    /**
     * @param individual
     * @return The model contribution for the specified {@link Individual}.
     * @throws IndividualModelValueNotDefinedException if this value is not
     * defined for the {@link Individual}.
     */
    double getModelContribution(Individual individual) throws IndividualModelValueNotDefinedException;

    /**
     * Pass the {@link FitnessFunction}s to the {@link Model}.
     * @param fitnessFunctions
     */
    void passFitnessFunctions(LinkedHashMap<String, FitnessFunction> fitnessFunctions);
}
