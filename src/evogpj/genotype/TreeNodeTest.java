package evogpj.genotype;

import evogpj.gp.GPException;
import evogpj.gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by stevenfine on 8/16/16.
 */
class TreeNodeTest {
    public static void testEvalAndCollectGeneticMaterial() throws GPException {
        MersenneTwisterFast rand = new MersenneTwisterFast(1234);
        ArrayList<String> funcSet = new ArrayList<>(asList("+",
                "*",
                "-",
                "mydivide",
                "mylog",
                "exp",
                "sin",
                "cos",
                "sqrt",
                "square",
                "cube",
                "quart"));
        ArrayList<String> termSet = new ArrayList<>(asList("X1", "X2", "X3"));

        TreeGenerator treeGen = new TreeGenerator(rand, funcSet, termSet);
        Tree tree = treeGen.generateLinearModel(termSet);
        TreeNode root = tree.getRoot();

        List<Double> inputVals = new ArrayList<>(asList(1.0, 2.0, 3.0));
        List<TreeNode> treeNodes = new ArrayList<>();
        List<Double> outputVals = new ArrayList<>();
        double output = root.evalAndCollectGeneticMaterial(inputVals, outputVals, treeNodes);
        System.out.println(output);
        System.out.println(tree.generate().eval(inputVals));
        System.out.println(tree);
        System.out.println(outputVals);
        System.out.println(treeNodes);
        for (int i = 0; i < outputVals.size(); i++) {
            double val = outputVals.get(i);
            double val2 = treeNodes.get(i).generate().eval(inputVals);
            System.out.println(val);
            System.out.println(val2);
        }
    }

    public static void main(String[] args) {
        try {
            testEvalAndCollectGeneticMaterial();
        } catch (GPException e) {
            e.printStackTrace();
        }
    }
}
