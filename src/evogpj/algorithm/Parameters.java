/**
 * Copyright (c) 2011-2013 Evolutionary Design and Optimization Group
 * 
 * Licensed under the MIT License.
 * 
 * See the "LICENSE" file for a copy of the license.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  
 *
 */
package evogpj.algorithm;

import java.util.ArrayList;
import java.util.List;

import evogpj.math.means.Mean;
import evogpj.operator.Operator;

/**
 * Simple class to collect all default values and names, so they can be found in
 * one location and properly documented.
 *
 * @author Owen Derby
 */
public final class Parameters {
    /**
     * Names used to identify properties in the properties file. Every key in
     * the properties file is matched against these strings by
     * {@link AlgorithmBase} to extract the new property value.
     *
     * @author Owen Derby
     */
    public final static class Names {

        public static final String MUTATION_RATE = "mutation_rate";
        public static final String XOVER_RATE = "xover_rate";
        public static final String ARCHIVE_MUTATION_RATE = "archive_mutation_rate";
        public static final String POP_SIZE = "pop_size";
        public static final String NUM_GENS = "num_gens";
        public static final String TIME_OUT = "timeout";
        public static final String PROBLEM = "problem";
        public static final String CROSS_VAL_SET = "cross_validation_set";
        public static final String PROBLEM_TYPE = "problem_type";
        public static final String PROBLEM_SIZE = "problem_size";
        public static final String EXTERNAL_THREADS = "external_threads";
        public static final String FUNCTION_SET = "function_set";
        public static final String FUNCTION_SET_SIZE = "function_set_size";
        public static final String UNARY_FUNCTION_SET = "unary_function_set";
        public static final String TERMINAL_SET = "terminal_set";
        public static final String MUTATE = "mutate_op";
        public static final String XOVER = "xover_op";
        public static final String ARCHIVE_MUTATE = "archive_mutate_op";
        public static final String REPRODUCE = "reproduce_op";
        public static final String SELECTION = "selection_op";
        public static final String FITNESS = "fitness_op";
        public static final String ARCHIVE = "archive_op";
        public static final String FALSE_NEGATIVE_WEIGHT = "false_negative_weight";
        public static final String INITIALIZE = "initialize_op";
        public static final String EQUALIZER = "equalizer_op";
        public static final String BIN_WIDTH = "bin_width";
        public static final String NUM_TRIALS = "num_trials";
        public static final String SEED = "rng_seed";
        public static final String TREE_INIT_MAX_DEPTH = "tree_initial_max_depth";
        public static final String MEAN_POW = "fitness_mean_pow";
        public static final String MODEL = "model";
        public static final String FITNESS_FUNCTION_EVALUATOR = "fitness_function_evaluator";

        /**
         * Is the output variable for our problem integer-valued, such that we
         * can "cheat" and force our models to output integer values?
         */
        public static final String COERCE_TO_INT = "integer_fitness";
        public static final String ALLOW_DUPLICATE_TREES = "allow_duplicate_trees";
        public static final String BROOD_SIZE = "brood_size";
        public static final String KOZA_FUNC_RATE = "koza_function_rate";
        public static final String TREE_XOVER_MAX_DEPTH = "tree_xover_max_depth";
        public static final String TREE_XOVER_TRIES = "tree_xover_tries";
        public static final String TREE_MUTATE_MAX_DEPTH = "tree_mutate_max_depth";
        public static final String TOURNEY_SIZE = "tourney_size";
        public static final String ARCHIVE_CAPACITY = "archive_capacity";
        public static final String COMBINED_POPULATION_MODEL_FRACTION = "combined_population_model_fraction";
        /**
         * For multi-objective optimization
         */
        // methods of sorting within fronts
        public static final String FRONT_RANK_METHOD = "front_rank_method";
        public static final String EUCLIDEAN = "euclidean"; // use euclidean
        public static final String FIRST_FITNESS = "first_fitness"; // use first
        /**
         * The main json log
         */
        public static final String JSON_LOG_PATH= "json_path";
        /**
         * For logging the population
         */
        public static final String POP_SAVE_FILENAME = "pop_save_filename";
        public static final String POP_LOAD_FILENAME = "pop_load_filename";
        public static final String POP_DATA_PATH = "pop_data_path";
        public static final String POP_DATA_PATH_PREFIX = "pop_data_path_prefix";
        /**
         * For logging models
         */
        public static final String MODELS_PATH = "models_path";
        /**
         * Enable population logging?
         */
        public static final String ENABLE_POP_LOG = "enable_pop_log";
        /**
         * In what format should tree models be displayed?
         */
        public static final String PREFIX_SCHEME = "prefix";
        public static final String INFIX_MATLAB = "infix";
        /**
         * Should JSON logs be enabled?
         */
        public static final String SAVE_JSON = "save_json";
    }

    /**
     * Names for specific operators, as understood by the library when reading
     * in values from the properties file.
     *
     * @author Owen Derby
     */
    public final static class Operators {

        // FITNESS values

        // Behavioral Programming
        public static final String PROGRAM_ERROR_FITNESS = "fitness.ProgramErrorFitness.Java";
        public static final String PROGRAM_SIZE_FITNESS = "fitness.ProgramSizeFitness.Java";
        public static final String MODEL_ERROR_FITNESS = "fitness.ModelErrorFitness.Java";
        public static final String MODEL_COMPLEXITY_FITNESS = "fitness.ModelComplexityFitness.Java";
        public static final String MODEL_CONTRIBUTION_FITNESS = "fitness.ModelContributionFitness.Java";

        // Symbolic Regression
        public static final String SR_JAVA_FITNESS = "fitness.SRFitness.Java";
        public static final String SR_CPP_FITNESS = "fitness.SRFitness.Cpp";
        public static final String SR_CUDA_FITNESS = "fitness.SRFitness.Cuda";
        public static final String SR_CUDA_FITNESS_DUAL = "fitness.SRFitness.CudaDual";
        public static final String SR_CUDA_FITNESS_CORRELATION = "fitness.SRFitness.CudaCorrelation";
        public static final String SR_CUDA_FITNESS_CORRELATION_DUAL = "fitness.SRFitness.CudaCorrelationDual";

        // GPFunction Classification
        public static final String GPFUNCTION_JAVA = "fitness.GPFunctionFitness.Java";
        public static final String GPFUNCTION_CV_JAVA = "fitness.GPFunctionCVFitness.Java";
        public static final String GPFUNCTION_CPP = "fitness.GPFunctionFitness.Cpp";
        public static final String GPFUNCTION_CUDA = "fitness.GPFunctionFitness.Cuda";
        public static final String GPFUNCTION_CV_CPP = "fitness.GPFunctionCVFitness.Cpp";
        public static final String GPFUNCTION_CV_CUDA = "fitness.GPFunctionCVFitness.Cuda";
        public static final String GPFUNCTION_PRED_CUDA = "fitness.GPFunctionPredFitness.Cuda";

        // GPFunction-KDE Classification
        public static final String GPFUNCTION_KDE_JAVA = "fitness.GPFunctionKDEFitness.Java";
        public static final String GPFUNCTION_KDE_CPP = "fitness.GPFunctionKDEFitness.Cpp";

        // RULE TREE CLASSIFIER
        public static final String RT_COST_JAVA_FITNESS = "fitness.RT_Cost_Fitness.Java";
        public static final String RT_MO_JAVA_FITNESS = "fitness.RT_MO_Fitness.Java";
        public static final String RT_FP_JAVA_FITNESS = "fitness.RT_FP_Fitness.Java";
        public static final String RT_FN_JAVA_FITNESS = "fitness.RT_FN_Fitness.Java";

        // Complexity
        public static final String SUBTREE_COMPLEXITY_FITNESS = "fitness.SubtreeComplexity";

        // INITIALIZE values
        public static final String TREE_INITIALIZE = "operator.TreeInitialize";

        // SELECTION values
        public static final String TOURNEY_SELECT = "operator.TournamentSelection";
        public static final String CROWD_SELECT = "operator.CrowdedTournamentSelection";

        // EQUALIZER values
        public static final String TOURNEY_EQUAL = "operator.TournamentEqualization";
        public static final String TREE_DYN_EQUAL = "operator.TreeDynamicEqualizer";
        public static final String DUMB_EQUAL = "operator.DummyEqualizer";
        public static final String DUMB_TREE_EQUAL = "operator.DummyTreeEqualizer";

        // MUTATE values
        public static final String SUBTREE_MUTATE = "operator.SubtreeMutate";
//        public static final String SUBTREE_MUTATE = "operator.SubtreeMutateConstants";
        public static final String UD_MUTATE = "operator.UniformDepthMutate";

        // XOVER values
        public static final String BROOD_XOVER = "operator.BroodSelection";
        // single point uniform crossover
        public static final String SPU_XOVER = "operator.SinglePointUniformCrossover";
        // single point Koza crossover
        public static final String SPK_XOVER = "operator.SinglePointKozaCrossover";
        public static final String UD_XOVER = "operator.UniformDepthCrossover";

        // ARCHIVE_MUTATE values
        public static final String ARCHIVE_MUTATE = "operator.ArchiveMutate";
        public static final String UD_ARCHIVE_MUTATE = "operator.UniformDepthArchiveMutate";

        // REPRODUCE values
        public static final String ORDINARY_REPRODUCE = "operator.OrdinaryReproduce";
        public static final String ARCHIVE_CROSSOVER_REPRODUCE = "operator.ArchiveCrossoverReproduce";
        public static final String ARCHIVE_AUGMENTED_REPRODUCE = "operator.ArchiveAugmentedReproduce";

        // ARCHIVE values
        public static final String BP_ARCHIVE = "fitness.BPArchive";

        // MODEL values
        public static final String REPTREE_MODEL = "fitness.REPTreeModel";
        public static final String PYTHON_MODEL = "fitness.PythonModel";
        public static final String RANDOM_MODEL = "fitness.RandomModel";
        public static final String FULL_POP_REPTREE_MODEL = "fitness.FullPopulationREPTreeModel";
        public static final String LASSO_MODEL = "fitness.LASSOModel";
        public static final String RANDOM_DRAWS_REPTREE_MODEL = "fitness.RandomDrawsREPTreeModel";

        // FITNESS_FUNCTION_EVALUATOR values
        public static final String ORDINARY_FITNESS_FUNCTION_EVALUATOR = "fitness.OrdinaryFitnessFunctionEvaluator";
        public static final String BP2A_FITNESS_FUNCTION_EVALUATOR = "fitness.BP2AFitnessFunctionEvaluator";
        public static final String BP4A_FITNESS_FUNCTION_EVALUATOR = "fitness.BP4AFitnessFunctionEvaluator";
        public static final String BP4_FITNESS_FUNCTION_EVALUATOR = "fitness.BP4FitnessFunctionEvaluator";
    }

    /**
     * All default values for running the library.
     * <p>
     * To specify other values, please use the properties file.
     *
     * @author Owen Derby
     */
    public final static class Defaults {
        /**
         * verbosity flag. Helpful for debugging.
         */
        public static final Boolean VERBOSE = false;


        public static final int POP_SIZE = 100;
        public static final int NUM_GENS = 10000;
        public static final int TIME_OUT = 60;
        // Frequency for selecting each operator
        public static final double MUTATION_RATE = 0.1;
        public static final double XOVER_RATE = 0.5;
        public static final double ARCHIVE_MUTATION_RATE = 0.4;
        // reproduction/replication frequency is implicitly defined as
        // (1 - XOVER_RATE - MUTATION_RATE)

        /**
         * number of best individuals to carry over to next generation
         */
        public static final int ELITE = 3;
        // public static final int NUM_TRIALS = 30;
        public static final int TREE_INIT_MAX_DEPTH = 17;
        public static final int BIN_WIDTH = 5;
        /**
         * The power p to use in the power mean for computing the absolute
         * error.
         *
         * @see Mean
         */
        public static final int MEAN_POW = 2;
        public static final int BROOD_SIZE = 1;
        // default value of 90% suggested by Koza.
        public static final double KOZA_FUNC_RATE = .9;
        public static final int TREE_XOVER_MAX_DEPTH = 17;
        public static final int TREE_XOVER_TRIES = 10;
        public static final int TREE_MUTATE_MAX_DEPTH = 17;
        public static final int TOURNEY_SIZE = 7;


        public static final int PROBLEM_SIZE = 3;
        public static final String PROBLEM_TYPE = "SRFunction";
        public static final String SRFITNESS = "fitness.SRFitness.Java, fitness.SubtreeComplexity";
        public static final String GPFUNCTION_FITNESS = "fitness.GPFunctionFitness.Java, fitness.SubtreeComplexity";
        public static final String GPFUNCTIONKDE_FITNESS = "fitness.GPFunctionKDEFitness.Java, fitness.SubtreeComplexity";
        public static final String RULETREE_FITNESS = "fitness.RT_MO_Fitness.Java, fitness.SubtreeComplexity";
        public static final String PROBLEM = "ProblemData/TrainDatasetBalanced2.txt";
        public static final String CROSS_VAL_SET = "ProblemData/TrainDatasetBalanced2.txt";
        public static final String ORDINARY_GP_FITNESS = "fitness.ProgramErrorFitness.Java, fitness.ProgramSizeFitness.Java";

        public static final int EXTERNAL_THREADS = 4;
        public static final int TARGET_NUMBER = 1;

        public static final double FALSE_NEGATIVE_WEIGHT = 0.5;
        /**
         * the initial seed to use for the rng in the algorithm.
         */
        public static final long SEED = System.currentTimeMillis();
        /**
         * Normally regression is over real numbers. Sometimes we want to do
         * regression over Integers. Set this to true to do so.
         */
        public static final boolean COERCE_TO_INT = false;
        public static final boolean ALLOW_DUPLICATE_TREES = true;

        public static final String INITIALIZE = Operators.TREE_INITIALIZE;
        public static final String SELECT = Operators.CROWD_SELECT;
        public static final String MUTATE = Operators.SUBTREE_MUTATE;
        public static final String EQUALIZER = Operators.TREE_DYN_EQUAL;
        public static final String XOVER = Operators.SPU_XOVER;
        public static final String REPRODUCE = Operators.ORDINARY_REPRODUCE;

        public static final int ARCHIVE_CAPACITY = 50;
        public static final double COMBINED_POPULATION_MODEL_FRACTION = 1.0;
        public static final String ARCHIVE = Operators.BP_ARCHIVE;
        public static final String ARCHIVE_MUTATE = Operators.ARCHIVE_MUTATE;
        public static final String MODEL = Operators.REPTREE_MODEL;
        public static final String FITNESS_FUNCTION_EVALUATOR = Operators.ORDINARY_FITNESS_FUNCTION_EVALUATOR;

        /**
         * To handle support for multiple fitness functions, this field can be
         * filled with any number of comma-separated fitness operator class
         * names
         */
        public static final List<String> TERMINALS;
        static {
                TERMINALS = new ArrayList<String>();
                TERMINALS.add("X1");
        }
        public static final List<String> FUNCTIONS;
        static {
                FUNCTIONS = new ArrayList<String>();
                FUNCTIONS.add("+");
                FUNCTIONS.add("*");
                FUNCTIONS.add("-");
                FUNCTIONS.add("mydivide");
                
                FUNCTIONS.add("mylog");
                FUNCTIONS.add("exp");
                FUNCTIONS.add("sin");
                FUNCTIONS.add("cos");
                FUNCTIONS.add("sqrt");
                FUNCTIONS.add("square");
                FUNCTIONS.add("cube");
                FUNCTIONS.add("quart");
        }
        
        // the main JSON log's default name
        public static final String JSON_LOG_PATH= "evogpj-log.json";

        // TODO dylan clear up this mess
        // for serializing the population
        public static final String POP_SAVE_FILENAME = "evogpj-population.ser";

        // for deserializing the population
        public static final String POP_LOAD_FILENAME = POP_SAVE_FILENAME;

        // controls the location at which to save population dumps
        public static final String POP_DATA_PATH_PREFIX = "populationLog";
        public static final String POP_DATA_PATH = POP_DATA_PATH_PREFIX
                        + "-unspecifiedAlgorighmType-unspecifiedSeed.txt";

        /**
         * For logging models
         */
        public static final String MODELS_PATH = "bestModelGeneration.txt";
        public static final String CONDITIONS_PATH = "conditions.txt";
        public static final String MODELS_CV_PATH = "bestCrossValidation.txt";
        public static final String PARETO_PATH = "pareto.txt";
        public static final String LEAST_COMPLEX_PATH = "leastComplex.txt";
        public static final String MOST_ACCURATE_PATH = "mostAccurate.txt";
        public static final String KNEE_PATH = "knee.txt";
        public static final String FUSED_PATH = "fusedModel.txt";
        public static final String RESULT_PATH = "result.txt";

        public static final String ENABLE_POP_LOG = "false";
        public static final String CACHE_PREDICTIONS = "false";

        public static final String FRONT_RANK_METHOD = Names.FIRST_FITNESS;
        public static final String SAVE_JSON = "false";

        /**
         * Experiment output file
         */
        public static final String EXPERIMENT_OUTPUT = "output.txt";
    }
}