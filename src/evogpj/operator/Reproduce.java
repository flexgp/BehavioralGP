package evogpj.operator;

import evogpj.algorithm.Parameters;
import evogpj.gp.GPException;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;

import java.util.Properties;

/**
 * Created by stevenfine on 7/27/16.
 */
public interface Reproduce {

    void addChildren(Population childPop, Population pop) throws GPException;

    class ReproduceBuilder {
        private MersenneTwisterFast rand;
        private Select select;
        private Mutate mutate;
        private Crossover xover;

        private double MUTATION_RATE = Parameters.Defaults.MUTATION_RATE;
        private double XOVER_RATE = Parameters.Defaults.XOVER_RATE;
        private int POP_SIZE = Parameters.Defaults.POP_SIZE;

        public ReproduceBuilder setMersenneTwisterFast(MersenneTwisterFast rand) {
            this.rand = rand;
            return this;
        }

        public ReproduceBuilder setSelect(Select select) {
            this.select = select;
            return this;
        }

        public ReproduceBuilder setMutate(Mutate mutate) {
            this.mutate = mutate;
            return this;
        }

        public ReproduceBuilder setCrossover(Crossover xover) {
            this.xover = xover;
            return this;
        }

        public ReproduceBuilder setProperties(Properties props) {
            if (props.containsKey(Parameters.Names.MUTATION_RATE))
                MUTATION_RATE = Double.valueOf(props.getProperty(Parameters.Names.MUTATION_RATE));
            if (props.containsKey(Parameters.Names.XOVER_RATE))
                XOVER_RATE = Double.valueOf(props.getProperty(Parameters.Names.XOVER_RATE));
            if (props.containsKey(Parameters.Names.POP_SIZE))
                POP_SIZE = Integer.valueOf(props.getProperty(Parameters.Names.POP_SIZE));
            return this;
        }

        public MersenneTwisterFast getMersenneTwisterFast() { return rand; }
        public Select getSelect() { return select; }
        public Mutate getMutate() { return mutate; }
        public Crossover getCrossover() { return xover; }
        public double getMutationRate() { return MUTATION_RATE; }
        public double getCrossoverRate() { return XOVER_RATE; }
        public int getPopulationSize() { return POP_SIZE; }


    }
}
