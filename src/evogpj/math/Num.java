package evogpj.math;

import evogpj.gp.GPException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenfine on 8/12/16.
 */
public class Num extends ZeroArgFunction {

    public Num(double coeff) {
        super(null, coeff);
    }

    @Override
    public Double eval(List<Double> t) {
        return coeff;
    }

    @Override
    public Double evalIntermediate(List<Double> t, ArrayList<Double> interVals) {
        interVals.add(coeff);
        return coeff;
    }
}
