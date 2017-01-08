package evogpj.math;

import evogpj.genotype.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevenfine on 1/8/17.
 */
public class Negate extends OneArgFunction {

    public Negate(Function a1) {
        super(a1);
    }

    public Negate(Function a1, TreeNode treeNode) {
        super(a1);
        this.treeNode = treeNode;
    }

    @Override
    public Double eval(List<Double> t) {
        return -arg.eval(t);
    }

    @Override
    public Double evalIntermediate(List<Double> t, ArrayList<Double> interVals) {
        double result = -arg.evalIntermediate(t, interVals);
        interVals.add(result);
        return result;
    }

    @Override
    public Double evalAndCollectGeneticMaterial(
            List<Double> inputVals,
            List<Double> outputVals,
            List<TreeNode> treeNodes
    ) {
        double result = -arg.evalAndCollectGeneticMaterial(inputVals, outputVals, treeNodes);
        outputVals.add(result);
        treeNodes.add(treeNode);
        return result;
    }

    @Override
    public String getInfixFormatString() {
        return "(negate %s)";
    }
}
