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

import evogpj.evaluation.*;
import evogpj.evaluation.java.*;
import evogpj.genotype.Tree;
import evogpj.genotype.TreeGenerator;
import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import evogpj.operator.*;
import evogpj.sort.CrowdingSort;
import evogpj.sort.DominatedCount;
import evogpj.sort.DominatedCount.DominationException;

/**
 * This class contains the main method that runs the GP algorithm.
 * 
 * @author Owen Derby and Ignacio Arnaldo
 **/
public class SymbRegMOO {
    
    /* NUMBER OF THREADS EMPLOYED IN THE EXERNAL EVALUATION */
    protected int EXTERNAL_THREADS = Parameters.Defaults.EXTERNAL_THREADS;
    
    /* DATA */
    // TRAINING SET
    protected String PROBLEM;
    // INTEGER TARGETS
    protected boolean COERCE_TO_INT = Parameters.Defaults.COERCE_TO_INT;
    protected int TARGET_NUMBER = 1;
    // ALLOW DUPLICATE TREES
    protected boolean ALLOW_DUPLICATE_TREES = Parameters.Defaults.ALLOW_DUPLICATE_TREES;
    // FEATURES
    protected List<String> TERM_SET;    
    
    /* PARAMETERS GOVERNING THE GENETIC PROGRAMMING PROCESS */
    // POPULATION SIZE
    protected int POP_SIZE = Parameters.Defaults.POP_SIZE;
    // NUMBER OF GENERATIONS
    protected int NUM_GENS = Parameters.Defaults.NUM_GENS;
    // start time
    protected Long startTime;
    // TIME OUT
    protected Long TIMEOUT;
    
    // MUTATION RATE
    protected double MUTATION_RATE = Parameters.Defaults.MUTATION_RATE;
    // CROSSOVER RATE
    protected double XOVER_RATE = Parameters.Defaults.XOVER_RATE;
    // ARCHIVE MUTATION RATE
    protected double ARCHIVE_MUTATION_RATE = Parameters.Defaults.ARCHIVE_MUTATION_RATE;
    // DEFAULT ARCHIVE CAPACITY
    protected int ARCHIVE_CAPACITY = Parameters.Defaults.ARCHIVE_CAPACITY;
    // DEFAULT COMBINED POPULATION MODEL FRACTION
    protected double COMBINED_POPULATION_MODEL_FRACTION = Parameters.Defaults.COMBINED_POPULATION_MODEL_FRACTION;
    
    
    // DEFAULT MUTATION OPERATOR
    protected String INITIALIZE = Parameters.Defaults.INITIALIZE;
    // DEFAULT MUTATION OPERATOR
    protected String SELECT = Parameters.Defaults.SELECT;
    // DEFAULT CROSSOVER OPERATOR
    protected String XOVER = Parameters.Defaults.XOVER;
    // DEFAULT MUTATION OPERATOR
    protected String MUTATE = Parameters.Defaults.MUTATE;
    // DEFAULT FITNESS FUNCTION
    protected String FITNESS = Parameters.Defaults.ORDINARY_GP_FITNESS;
    // METHOD EMPLOYED TO AGGREGATE THE FITNESS OF CANDIDATE SOLUTIONS
    protected int MEAN_POW = Parameters.Defaults.MEAN_POW;
    // METHOD EMPLOYED TO SELECT A SOLUTION FROM A PARETO FRONT
    protected String FRONT_RANK_METHOD = Parameters.Defaults.FRONT_RANK_METHOD;
    // DEFAULT REPRODUCE OPERATOR
    protected String REPRODUCE = Parameters.Defaults.REPRODUCE;
    // DEFAULT ARCHIVE
    protected String ARCHIVE = Parameters.Defaults.ARCHIVE;
    // DEFAULT ARCHIVE MUTATE
    protected String ARCHIVE_MUTATE = Parameters.Defaults.ARCHIVE_MUTATE;
    // DEFAULT MODEL
    protected String MODEL = Parameters.Defaults.MODEL;
    // DEFAULT FITNESS FUNCTION EVALUATOR
    protected String FITNESS_FUNCTION_EVALUATOR = Parameters.Defaults.FITNESS_FUNCTION_EVALUATOR;
    
    
    // ALL THE OPERATORS USED TO BUILD GP TREES
    protected List<String> FUNC_SET = Parameters.Defaults.FUNCTIONS;
    
    // UNARY OPERATORS USED TO BUILD GP TREES
    protected List<String> UNARY_FUNC_SET;
    
    // RANDOM SEED
    protected Long SEED = Parameters.Defaults.SEED;
    
    /* LOG FILES*/
    // LOG BEST INDIVIDUAL PER GENERATION
    protected String MODELS_PATH = Parameters.Defaults.MODELS_PATH;
    // LOG FINAL PARETO FRONT
    protected String PARETO_PATH = Parameters.Defaults.PARETO_PATH;
    // LOG least complex ind
    protected String LEAST_COMPLEX_PATH = Parameters.Defaults.LEAST_COMPLEX_PATH;
    // LOG most accurate individual
    protected String MOST_ACCURATE_PATH = Parameters.Defaults.MOST_ACCURATE_PATH;
    // LOG the model on the knee of the Pareto Front
    protected String KNEE_PATH = Parameters.Defaults.KNEE_PATH;
    // LOG the fused Pareto Front
    protected String FUSED_PATH = Parameters.Defaults.FUSED_PATH;
    
    /* CANDIDATE SOLUTIONS MAINTAINED DURING THE SEARCH */
    // CURRENT POPULATION
    protected Population pop;
    // OFFSPRING
    protected Population childPop;
    // OFFSPRING + PARENTS
    protected Population totalPop;
    // CURRENT NON-DOMINATED SOLUTIONS
    protected Population paretoFront;
    // CURRENT GENERATION'S BEST INDIVIDUAL
    protected Individual best;
    // BEST INDIVIDUAL OF EACH GENERATION
    protected Population bestPop;
    
    /* OPERATORS EMPLOYED IN THE SEARCH PROCESS */
    // RANDOM NUMBER GENERATOR
    protected MersenneTwisterFast rand;
    // INITIALIZATION METHOD
    protected Initialize initialize;
    // CROSSOVER
    protected Crossover xover;
    // SELECTION
    protected Select select;
    // MUTATION
    protected Mutate mutate;
    // REPRODUCTION
    protected Reproduce reproduce;
    // ARCHIVE
    protected Archive archive;
    // ARCHIVE MUTATE
    protected ArchiveMutate archiveMutate;
    // MODEL
    protected Model model;

    // FITNESS FUNCTIONS
    protected LinkedHashMap<String, FitnessFunction> fitnessFunctions;
    // FITNESS FUNCTION EVALUATOR
    protected FitnessFunctionEvaluator fitnessFunctionEvaluator;
    
    
    /* CONTROL FOR END OF EVOLUTIONARY PROCESS*/
    // CURRENT GENERATION
    protected Integer generation;
    // CONTROL FOR END OF PROCESS
    protected Boolean finished;
    // NUMBER OF GENERATIONS WITHOUT FITNESS IMPROVEMENT
    protected int counterConvergence;
    // CURRENT FITNESS OF BEST INDIVIDUAL
    protected double lastFitness;
    
    private Properties props;
    
    double minTarget, maxTarget;
    
    /**
     * Empty constructor, to allow subclasses to override
     */
    public SymbRegMOO() {
        fitnessFunctions = new LinkedHashMap<String, FitnessFunction>();
        finished = false;
        generation = 0;
        counterConvergence = 0;
        lastFitness = 0;
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Create an instance of the algorithm. This simply initializes all the
     * operators to the default parameters or whatever they are set to in the
     * passed in properties object. Use {@link #run_population()} to actually
     * run the population for the specified number of generations.
     * <p>
     * If an invalid operator type is specified, then the program will
     * terminate, indicating which parameter is incorrect.
     * 
     * @param props
     *            Properties object created from a .properties file specifying
     *            parameters for the algorithm
     * @param timeout
     * @throws java.io.IOException
     */
    public SymbRegMOO(Properties props,long timeout) throws IOException {
        this();
        if (timeout > 0)
            TIMEOUT = startTime + (timeout * 1000);
        this.props = props;
        loadParams(props);
        create_operators(props,SEED);
    }

    public SymbRegMOO(String propFile,long timeout) throws IOException {
        this();
        if (timeout > 0)
            TIMEOUT = startTime + (timeout * 1000);
        this.props = loadProps(propFile);
        loadParams(props);
        create_operators(props,SEED);
    }
    
    public SymbRegMOO(Properties aProps,String propFile,long timeout) throws IOException {
        this();
        this.props = loadProps(propFile);
        if (timeout > 0)
            TIMEOUT = startTime + (timeout * 1000);
        loadParams(props);
        Object[] presetProperties = (Object[])aProps.stringPropertyNames().toArray();
        for(int i=0;i<presetProperties.length;i++){
            String keyAux = (String)presetProperties[i];
            String valueAux = aProps.getProperty(keyAux);
            props.setProperty(keyAux, valueAux);
        }
        loadParams(props);
        create_operators(props,SEED);
    }

    /**
     * Read parameters from the Property object and set Algorithm variables.
     * 
     * @see Parameters
     */
    private void loadParams(Properties props) {
        if (props.containsKey(Parameters.Names.SEED))
            SEED = Long.valueOf(props.getProperty(Parameters.Names.SEED)).longValue();
        if (props.containsKey(Parameters.Names.PROBLEM)){
            PROBLEM = props.getProperty(Parameters.Names.PROBLEM);
        }else if(props.containsKey("data")){
            PROBLEM = props.getProperty("data");
        }
        if (props.containsKey(Parameters.Names.EXTERNAL_THREADS))
            EXTERNAL_THREADS = Integer.valueOf(props.getProperty(Parameters.Names.EXTERNAL_THREADS));
        if (props.containsKey(Parameters.Names.FITNESS))
            FITNESS = props.getProperty(Parameters.Names.FITNESS);            
        if (props.containsKey(Parameters.Names.MUTATION_RATE))
            MUTATION_RATE = Double.valueOf(props.getProperty(Parameters.Names.MUTATION_RATE));
        if (props.containsKey(Parameters.Names.XOVER_RATE))
            XOVER_RATE = Double.valueOf(props.getProperty(Parameters.Names.XOVER_RATE));
        if (props.containsKey(Parameters.Names.ARCHIVE_MUTATION_RATE))
            ARCHIVE_MUTATION_RATE = Double.valueOf(props.getProperty(Parameters.Names.ARCHIVE_MUTATION_RATE));
        if (props.containsKey(Parameters.Names.ARCHIVE_CAPACITY))
            ARCHIVE_CAPACITY = Integer.valueOf(props.getProperty(Parameters.Names.ARCHIVE_CAPACITY));
        if (props.containsKey(Parameters.Names.COMBINED_POPULATION_MODEL_FRACTION))
            COMBINED_POPULATION_MODEL_FRACTION = Double.valueOf(props.getProperty(Parameters.Names.COMBINED_POPULATION_MODEL_FRACTION));
        if (props.containsKey(Parameters.Names.POP_SIZE))
            POP_SIZE = Integer.valueOf(props.getProperty(Parameters.Names.POP_SIZE));
        if (props.containsKey(Parameters.Names.POP_DATA_PATH))
            MODELS_PATH = props.getProperty(Parameters.Names.MODELS_PATH);
        if (props.containsKey(Parameters.Names.NUM_GENS))
            NUM_GENS = Integer.valueOf(props.getProperty(Parameters.Names.NUM_GENS));
        if (props.containsKey(Parameters.Names.MEAN_POW))
            MEAN_POW = Integer.valueOf(props.getProperty(Parameters.Names.MEAN_POW));
        if (props.containsKey(Parameters.Names.COERCE_TO_INT))
            COERCE_TO_INT = Boolean.parseBoolean(props.getProperty(Parameters.Names.COERCE_TO_INT));
        if (props.containsKey(Parameters.Names.ALLOW_DUPLICATE_TREES))
            ALLOW_DUPLICATE_TREES = Boolean.parseBoolean(props.getProperty(Parameters.Names.ALLOW_DUPLICATE_TREES));
        if (props.containsKey(Parameters.Names.FUNCTION_SET)) {
            String funcs[] = props.getProperty(Parameters.Names.FUNCTION_SET).split(" ");
            FUNC_SET = new ArrayList<String>();
            FUNC_SET.addAll(Arrays.asList(funcs));
        }
        UNARY_FUNC_SET = new ArrayList<String>();
        for(String func:FUNC_SET){
            if(func.equals("mylog") || func.equals("exp") || func.equals("sin") || func.equals("cos") || 
                    func.equals("sqrt") || func.equals("square") || func.equals("cube") ||
                    func.equals("quart") || func.equals("negate")) {
                UNARY_FUNC_SET.add(func);
            }
        }
        if (props.containsKey(Parameters.Names.TERMINAL_SET)) {
            String term = props.getProperty(Parameters.Names.TERMINAL_SET);
            if (term.equalsIgnoreCase("all")) {
                // defer populating terminal list until we know our problem
                // size!
                TERM_SET = null;
            } else {
                String terms[] = term.split(" ");
                TERM_SET = new ArrayList<String>();
                TERM_SET.addAll(Arrays.asList(terms));
            }
        }
        if (props.containsKey(Parameters.Names.MUTATE))
                MUTATE = props.getProperty(Parameters.Names.MUTATE);
        if (props.containsKey(Parameters.Names.XOVER))
                XOVER = props.getProperty(Parameters.Names.XOVER);
        if (props.containsKey(Parameters.Names.SELECTION))
                SELECT = props.getProperty(Parameters.Names.SELECTION);
        if (props.containsKey(Parameters.Names.INITIALIZE))
                INITIALIZE = props.getProperty(Parameters.Names.INITIALIZE);
        if (props.containsKey(Parameters.Names.FRONT_RANK_METHOD))
                FRONT_RANK_METHOD = props.getProperty(Parameters.Names.FRONT_RANK_METHOD);
        if (props.containsKey(Parameters.Names.REPRODUCE))
                REPRODUCE = props.getProperty(Parameters.Names.REPRODUCE);
        if (props.containsKey(Parameters.Names.ARCHIVE))
                ARCHIVE = props.getProperty(Parameters.Names.ARCHIVE);
        if (props.containsKey(Parameters.Names.ARCHIVE_MUTATE))
                ARCHIVE_MUTATE = props.getProperty(Parameters.Names.ARCHIVE_MUTATE);
        if (props.containsKey(Parameters.Names.MODEL))
                MODEL = props.getProperty(Parameters.Names.MODEL);
        if (props.containsKey(Parameters.Names.FITNESS_FUNCTION_EVALUATOR))
                FITNESS_FUNCTION_EVALUATOR = props.getProperty(Parameters.Names.FITNESS_FUNCTION_EVALUATOR);
        
    }

    /**
     * Handle parsing the FITNESS field (fitness_op), which could contain
     * multiple fitness operators
     *
     * @return a LinkedHashMap with properly ordered operators and null
     *         FitnessFunctions. This enforces the iteration order
     */
    protected LinkedHashMap<String, FitnessFunction> splitFitnessOperators(String fitnessOpsRaw) {
        LinkedHashMap<String, FitnessFunction> fitnessOperators = new LinkedHashMap<String, FitnessFunction>();
        List<String> fitnessOpsSplit = Arrays.asList(fitnessOpsRaw.split("\\s*,\\s*"));
        for (String f : fitnessOpsSplit) {
            fitnessOperators.put(f, null);
        }
        return fitnessOperators;
    }

    /**
     * Create all the operators from the loaded params. Seed is the seed to use
     * for the rng. If specified, d_in is some DataJava to use. Otherwise, d_in
     * should be null and fitness will load in the appropriate data.
     * 
     * @param seed
     * 
     */
    private void create_operators(Properties props, long seed) throws IOException {
        System.out.println("Running evogpj with seed: " + seed);
        rand = new MersenneTwisterFast(seed);
        DataJava data = new CSVDataJava(PROBLEM);

        double[] targetValuesArray = data.getTargetValues();
        List<Double> targetValues = new ArrayList<>();
        for (int i = 0; i < targetValuesArray.length; i++) {
            targetValues.add(targetValuesArray[i]);
        }

        if (MODEL.equals(Parameters.Operators.REPTREE_MODEL)) {
            model = new REPTreeModel(targetValues);
        } else if (MODEL.equals(Parameters.Operators.FULL_POP_REPTREE_MODEL)) {
            model = new FullPopulationREPTreeModel(
                    targetValues,
                    SELECT.equals(Parameters.Operators.CROWD_SELECT),
                    COMBINED_POPULATION_MODEL_FRACTION
            );
        } else if (MODEL.equals(Parameters.Operators.PYTHON_MODEL)) {
            model = new PythonModel(targetValues, EXTERNAL_THREADS);
        } else if (MODEL.equals(Parameters.Operators.RANDOM_MODEL)) {
            model = new RandomModel(targetValues, rand);
        } else if (MODEL.equals(Parameters.Operators.LASSO_MODEL)) {
            model = new LASSOModel(targetValues);
        } else if (MODEL.equals(Parameters.Operators.RANDOM_DRAWS_REPTREE_MODEL)) {
            model = new RandomDrawsREPTreeModel(
                    targetValues,
                    rand,
                    SELECT.equals(Parameters.Operators.CROWD_SELECT),
                    COMBINED_POPULATION_MODEL_FRACTION
            );
        } else {
            System.err.format("Invalid Model %s specified for problem type %s%n", MODEL);
            System.exit(-1);
        }

        // Set up archive
        if (ARCHIVE.equals(Parameters.Operators.BP_ARCHIVE)) {
            archive = new BPArchive(rand, ARCHIVE_CAPACITY);
        } else {
            System.err.format("Invalid Archive %s specified for problem type %s%n", ARCHIVE);
            System.exit(-1);
        }

        fitnessFunctions = splitFitnessOperators(FITNESS);
        for (String fitnessOperatorName : fitnessFunctions.keySet()) {
            if (fitnessOperatorName.equals(Parameters.Operators.SR_JAVA_FITNESS) ||
                    fitnessOperatorName.equals(Parameters.Operators.ORDINARY_GP_FITNESS) ||
                    fitnessOperatorName.equals(Parameters.Operators.ARCHIVE_BUILDER_FITNESS) ||
                    fitnessOperatorName.equals(Parameters.Operators.PROGRAM_ERROR_FITNESS)) {
                minTarget = data.getTargetMin();
                maxTarget = data.getTargetMax();
                
                if (TERM_SET == null) {
                    TERM_SET = new ArrayList<String>();
                    for (int i = 0; i < data.getNumberOfFeatures(); i++) TERM_SET.add("X" + (i + 1));
                    System.out.println(TERM_SET);
                }

                FitnessFunction fitnessFunction = null;
                if (fitnessOperatorName.equals(Parameters.Operators.SR_JAVA_FITNESS)) {
                    fitnessFunction = new SRLARSJava(data, MEAN_POW, COERCE_TO_INT, EXTERNAL_THREADS);
                } else if (fitnessOperatorName.equals(Parameters.Operators.ORDINARY_GP_FITNESS)) {
                    fitnessFunction = new OrdinaryGP(data, MEAN_POW, COERCE_TO_INT, EXTERNAL_THREADS);
                } else if (fitnessOperatorName.equals(Parameters.Operators.ARCHIVE_BUILDER_FITNESS)) {
                    fitnessFunction = new ArchiveBuilder(data, MEAN_POW, COERCE_TO_INT, EXTERNAL_THREADS, archive);
                } else if (fitnessOperatorName.equals(Parameters.Operators.PROGRAM_ERROR_FITNESS)) {
                    fitnessFunction = new ProgramErrorFitness(data, EXTERNAL_THREADS);
                }
                fitnessFunctions.put(fitnessOperatorName, fitnessFunction);
                //modelScalerJava = new SRModelScalerJava(data);
            } else if (fitnessOperatorName.equals(Parameters.Operators.SUBTREE_COMPLEXITY_FITNESS)) {
                fitnessFunctions.put(fitnessOperatorName, new SubtreeComplexityFitness());
            } else if (fitnessOperatorName.equals(Parameters.Operators.PROGRAM_SIZE_FITNESS)) {
                fitnessFunctions.put(fitnessOperatorName, new ProgramSizeFitness());
            } else if (fitnessOperatorName.equals(Parameters.Operators.MODEL_ERROR_FITNESS)) {
                fitnessFunctions.put(fitnessOperatorName, new ModelErrorFitness(model));
            } else if (fitnessOperatorName.equals(Parameters.Operators.MODEL_COMPLEXITY_FITNESS)) {
                fitnessFunctions.put(fitnessOperatorName, new ModelComplexityFitness(model));
            } else if (fitnessOperatorName.equals(Parameters.Operators.MODEL_CONTRIBUTION_FITNESS)) {
                fitnessFunctions.put(fitnessOperatorName, new ModelContributionFitness(model));
            } else {
                System.err.format("Invalid fitness function %s specified for problem type %s%n",fitnessOperatorName);
                System.exit(-1);
            }
        }
        model.passFitnessFunctions(fitnessFunctions);

        if (FITNESS_FUNCTION_EVALUATOR.equals(Parameters.Operators.ORDINARY_FITNESS_FUNCTION_EVALUATOR)) {
            fitnessFunctionEvaluator = new OrdinaryFitnessFunctionEvaluator(fitnessFunctions);
        } else if (FITNESS_FUNCTION_EVALUATOR.equals(Parameters.Operators.BP2A_FITNESS_FUNCTION_EVALUATOR)) {
            fitnessFunctionEvaluator = new BP2AFitnessFunctionEvaluator(fitnessFunctions, model, archive);
        } else if (FITNESS_FUNCTION_EVALUATOR.equals(Parameters.Operators.BP4A_FITNESS_FUNCTION_EVALUATOR)) {
            fitnessFunctionEvaluator = new BP4AFitnessFunctionEvaluator(fitnessFunctions, model, archive);
        } else if (FITNESS_FUNCTION_EVALUATOR.equals(Parameters.Operators.BP4_FITNESS_FUNCTION_EVALUATOR)) {
            fitnessFunctionEvaluator = new BP4FitnessFunctionEvaluator(fitnessFunctions, model);
        } else {
            System.err.format("Invalid fitness function evaluator %s specified%n", FITNESS_FUNCTION_EVALUATOR);
            System.exit(-1);
        }

        TreeGenerator treeGen = new TreeGenerator(rand, FUNC_SET, TERM_SET);
        if (INITIALIZE.equals(Parameters.Operators.TREE_INITIALIZE)) {
            initialize = new TreeInitialize(rand, props, treeGen);
        } else {
            System.err.format("Invalid initialize function %s specified%n",INITIALIZE);
            System.exit(-1);
        }

        // Set up operators.
        if (SELECT.equals(Parameters.Operators.TOURNEY_SELECT)) {
            select = new TournamentSelection(rand, props);
        } else if (SELECT.equals(Parameters.Operators.CROWD_SELECT)) {
            select = new CrowdedTournamentSelection(rand, props);
        } else {
            System.err.format("Invalid select function %s specified%n", SELECT);
            System.exit(-1);
        }

        // Set up archiveMutate
        if (ARCHIVE_MUTATE.equals(Parameters.Operators.ARCHIVE_MUTATE)) {
            archiveMutate = new ArchiveMutate(rand, props, archive);
        } else if (ARCHIVE_MUTATE.equals(Parameters.Operators.UD_ARCHIVE_MUTATE)) {
            archiveMutate = new UniformDepthArchiveMutate(rand, props, archive);
        } else {
            System.err.format("Invalid archiveMutate function %s specified%n", ARCHIVE_MUTATE);
            System.exit(-1);
        }

        // Set up mutate
        if (MUTATE.equals(Parameters.Operators.SUBTREE_MUTATE)) {
            mutate = new SubtreeMutate(rand, props, treeGen);
        } else if (MUTATE.equals(Parameters.Operators.UD_MUTATE)) {
            mutate = new UniformDepthMutate(rand, props, treeGen);
        } else {
            System.err.format("Invalid mutate function %s specified%n", MUTATE);
            System.exit(-1);
        }

        // Set up xover
        if (XOVER.equals(Parameters.Operators.SPU_XOVER)) {
            xover = new SinglePointUniformCrossover(rand, props);
        } else if (XOVER.equals(Parameters.Operators.SPK_XOVER)) {
            xover = new SinglePointKozaCrossover(rand, props);
        } else if (XOVER.equals(Parameters.Operators.UD_XOVER)) {
            xover = new UniformDepthCrossover(rand, props);
        } else {
            System.err.format("Invalid crossover function %s specified%n",XOVER);
            System.exit(-1);
        }

        // Set up reproduction operator
        if (REPRODUCE.equals(Parameters.Operators.ORDINARY_REPRODUCE)) {
            reproduce = new OrdinaryReproduce(
                    rand,
                    select,
                    mutate,
                    xover,
                    MUTATION_RATE,
                    XOVER_RATE,
                    POP_SIZE
            );
        } else if (REPRODUCE.equals(Parameters.Operators.ARCHIVE_CROSSOVER_REPRODUCE)) {
            reproduce = new ArchiveCrossoverReproduce(
                    rand,
                    select,
                    mutate,
                    archiveMutate,
                    MUTATION_RATE,
                    ARCHIVE_MUTATION_RATE,
                    POP_SIZE
            );
        } else if (REPRODUCE.equals(Parameters.Operators.ARCHIVE_AUGMENTED_REPRODUCE)) {
            reproduce = new ArchiveAugmentedReproduce(
                    rand,
                    select,
                    mutate,
                    xover,
                    archiveMutate,
                    MUTATION_RATE,
                    XOVER_RATE,
                    ARCHIVE_MUTATION_RATE,
                    POP_SIZE,
                    ALLOW_DUPLICATE_TREES
            );
        } else {
            System.err.format("Invalid reproduce function %s specified%n",REPRODUCE);
            System.exit(-1);
        }

        Tree t = treeGen.generateLinearModel(TERM_SET);
        Individual linearModelInd = new Individual(t);
        // to set up equalization operator, we need to evaluate all the
        // individuals first
        pop = initialize.initialize(POP_SIZE);
        pop.set(0,linearModelInd);
        // initialize totalPop to simply the initial population
        fitnessFunctionEvaluator.evalPop(pop);
        // calculate domination counts of initial population for tournament selection
        try {
            DominatedCount.countDominated(pop, fitnessFunctions);
        } catch (DominationException e) {
            System.exit(-1);
        }
        // save first front of initial population
        paretoFront = new Population();
        for (int index = 0; index < pop.size(); index++) {
            Individual individual = pop.get(index);
            if (individual.getDominationCount().equals(0))
                paretoFront.add(individual);
        }
        // calculate crowding distances of initial population for crowding sort
        if (SELECT.equals(Parameters.Operators.CROWD_SELECT)) {
            CrowdingSort.computeCrowdingDistances(pop, fitnessFunctions);
        }
    }

    /**
     * Accept potential migrants into the population
     * @param migrants
     */
    protected void acceptMigrants(Population migrants) {
            pop.addAll(migrants);
    }
	
    /**
     * This is the heart of the algorithm. This corresponds to running the
     * {@link #pop} forward one generation
     * <p>
     * Basically while we still need to produce offspring, we select an
     * individual (or two) as parent(s) and perform a genetic operator, chosen
     * at random according to the parameters, to apply to the parent(s) to
     * produce children. Then evaluate the fitness of the new child(ren) and if
     * they are accepted by the equalizer, add them to the next generation.
     * <p>
     * The application of operators is mutually exclusive. That is, for each
     * iteration of this algorithm, we will choose exactly one of crossover,
     * mutation and replication. However, which one we choose is determined by
     * sampling from the distribution specified by the mutation and crossover
     * rates.
     * 
     * @returns a LinkedHashMap mapping fitness function name to the best
     *          individual for that fitness function
     * @throws GPException
     *             if any of the operators receive a individual with an
     *             unexpected genotype, this is an error.
     */
    protected void step() throws GPException {
        // generate children from previous population. don't use elitism
        // here since that's done later
        childPop = new Population();
        while (childPop.size() < POP_SIZE) {
            reproduce.addChildren(childPop, pop);
        }

        // evaluate all children
        fitnessFunctionEvaluator.evalPop(childPop);
        // combine the children and parents for a total of 2*POP_SIZE
        totalPop = new Population(pop, childPop);
        try {
            // for each individual, count number of individuals that dominate it
            DominatedCount.countDominated(totalPop, fitnessFunctions);
        } catch (DominationException e) {
                System.exit(-1);
        }
        // if crowding tournament selection is enabled, calculate crowding distances
        if (SELECT.equals(Parameters.Operators.CROWD_SELECT)) {
            CrowdingSort.computeCrowdingDistances(totalPop, fitnessFunctions);
        }
        // sort the entire 2*POP_SIZE population by domination count and by crowding distance if enabled
        totalPop.sort(SELECT.equals(Parameters.Operators.CROWD_SELECT));

        // use non-dominated sort to take the POP_SIZE best individuals
        // also find the latest pareto front
        pop = new Population();
        paretoFront = new Population();
        for (int index = 0; index < POP_SIZE; index++) {
            Individual individual = totalPop.get(index);
            pop.add(individual);
            // also save the first front for later use
            if (individual.getDominationCount().equals(0))
                paretoFront.add(individual);
        }
        // find best individual
        pop.calculateEuclideanDistances(fitnessFunctions);
        best = pop.get(0);
        for (int index = 0; index < POP_SIZE; index++) {
            Individual individual = pop.get(index);
            // two methods for selecting the best here from the entire population:
            // 1) euclidean distance
            // 2) "first fitness", which for dynamic equalization is simply
            // the individual with the best fitness, and for multi-objective optimization
            // is the individual with the best first fitness
            if (FRONT_RANK_METHOD.equals(Parameters.Names.EUCLIDEAN)) {
                if (individual.getEuclideanDistance() < best.getEuclideanDistance()) {
                    best = individual;
                }
            } else if (FRONT_RANK_METHOD.equals(Parameters.Names.FIRST_FITNESS)) {
                if(individual.getFitness() > best.getFitness()){
                    best = individual;
                }
            } else {
                System.err.format("No such selection method \"%s\"%n", FRONT_RANK_METHOD);
                System.exit(-1);
            }
        }
    }

    /**
     * get the best individual per generation in a Population object
     * 
     * @return the best individual per generation.
     */
    public Population getBestPop(){
        return bestPop;
    }
                
    /**
    * Run the current population for the specified number of generations.
    * 
    * @return the best individual found.
    */
    public Individual run_population() throws IOException {
        bestPop = new Population();
        // get the best individual
        best = pop.get(0);
        // record the best individual in models.txt
        bestPop.add(best);
        long timeStamp = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("ELAPSED TIME: " + timeStamp);
        while ((generation <= NUM_GENS) && (!finished)) {
            System.out.format("Generation %d\n", generation);
            System.out.flush();
            try {
                step();
            } catch (GPException e) {
                System.exit(-1);
            }
            bestPop.add(best);
            timeStamp = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("ELAPSED TIME: " + timeStamp);
            generation++;
            finished = stopCriteria();
            
        }

        Iterator<String> fitnessFunctionIterator = fitnessFunctions.keySet().iterator();
        String firstFitnessFunction = fitnessFunctionIterator.next();
        String secondFitnessFunction = fitnessFunctionIterator.next();
        Individual acc = paretoFront.get(0);
        Individual comp = paretoFront.get(0);
        Individual knee = paretoFront.get(0);
        paretoFront.calculateEuclideanDistances(fitnessFunctions);
        FitnessFunction firstFitness = fitnessFunctions.get(firstFitnessFunction);
        FitnessFunction secondFitness = fitnessFunctions.get(secondFitnessFunction);
        for(Individual ind:paretoFront){
            if((firstFitness.isMaximizingFunction() && ind.getFitness(firstFitnessFunction) > acc.getFitness(firstFitnessFunction)) ||
                    (!firstFitness.isMaximizingFunction() && ind.getFitness(firstFitnessFunction) < acc.getFitness(firstFitnessFunction))){
                acc = ind;
            }
            if((secondFitness.isMaximizingFunction() && ind.getFitness(secondFitnessFunction) > comp.getFitness(secondFitnessFunction)) ||
                    (!secondFitness.isMaximizingFunction() && ind.getFitness(secondFitnessFunction) < comp.getFitness(secondFitnessFunction))){
                comp = ind;
            }
            if(ind.getEuclideanDistance()<knee.getEuclideanDistance()){
                knee = ind;
            }

        }

        // SAVE MOST ACCURATE MODEL OF THE PARETO FRONT
        this.saveText(MOST_ACCURATE_PATH, "", false);
        this.saveText(MOST_ACCURATE_PATH, minTarget + "," + maxTarget + ",", true);
        if (firstFitnessFunction.equals(Parameters.Operators.SR_JAVA_FITNESS)) {
            for(int j = 0;j<acc.getWeights().size()-1;j++){
                this.saveText(MOST_ACCURATE_PATH, acc.getWeights().get(j) + " ", true);
            }
            this.saveText(MOST_ACCURATE_PATH, acc.getWeights().get(acc.getWeights().size()-1) + ",", true);
            this.saveText(MOST_ACCURATE_PATH, acc.getLassoIntercept() + ",", true);
        }
        this.saveText(MOST_ACCURATE_PATH, acc.toString() + "\n", true);

        return acc;
    }
    
    public boolean stopCriteria(){
        boolean stop = false;
        if( System.currentTimeMillis() >= TIMEOUT){
            System.out.println("Timout exceeded, exiting.");
            return true;
        }
        return stop;
    }
        
    public static Properties loadProps(String propFile) {
        Properties props = new Properties();
        BufferedReader f;
        try {
            f = new BufferedReader(new FileReader(propFile));
        } catch (FileNotFoundException e) {
            return null;
        }
        try {
            props.load(f);
        } catch (IOException e) {
        }
        System.out.println(props.toString());
        return props;
    }
       
    /**
     * Save text to a filepath
     * @param filepath
     * @param text
     * @param append
     */
    protected void saveText(String filepath, String text, Boolean append) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath,append));
            PrintWriter printWriter = new PrintWriter(bw);
            printWriter.write(text);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            System.exit(-1);
        }
    }
        
    public List<String> getFuncs(){
        return FUNC_SET;
    }

    public List<String> getUnaryFuncs(){
        return UNARY_FUNC_SET;
    }

    public boolean running() {
        return (generation <= NUM_GENS) && (!finished);
    }
  
}
