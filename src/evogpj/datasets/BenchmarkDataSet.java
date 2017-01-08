package evogpj.datasets;

import evogpj.gp.MersenneTwister;
import evogpj.gp.MersenneTwisterFast;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by stevenfine on 1/3/17.
 */
public class BenchmarkDataSet {

    private static List<List<Double>> constructDataSet(String name) {
        List<List<Double>> inputs;
        List<List<Double>> data;
        switch (name) {
            case "Keijzer1":
                inputs = getInputs(-1, 1, 0.1, 1);
                data = getData(inputs, new K1());
                break;
            case "Keijzer4":
                inputs = getInputs(0, 10, 0.5, 1);
                data = getData(inputs, new K4());
                break;
            case "Keijzer5":
                inputs = getInputs(-1, 1, 0.66, 3);
                data = getData(inputs, new K5());
                break;
            case "Keijzer11":
                inputs = getInputs(-2, 2, 1, 2);
                data = getData(inputs, new K11());
                break;
            case "Keijzer12":
                inputs = getInputs(-2, 2, 1, 2);
                data = getData(inputs, new K12());
                break;
            case "Keijzer13":
                inputs = getInputs(-2, 2, 1, 2);
                data = getData(inputs, new K13());
                break;
            case "Keijzer14":
                inputs = getInputs(-2, 2, 1, 2);
                data = getData(inputs, new K14());
                break;
            case "Keijzer15":
                inputs = getInputs(-2, 2, 1, 2);
                data = getData(inputs, new K15());
                break;
            case "Nguyen3":
                inputs = getInputs(-1, 1, 0.1, 1);
                data = getData(inputs, new N3());
                break;
            case "Nguyen4":
                inputs = getInputs(-1, 1, 0.1, 1);
                data = getData(inputs, new N4());
                break;
            case "Nguyen5":
                inputs = getInputs(-1, 1, 0.1, 1);
                data = getData(inputs, new N5());
                break;
            case "Nguyen6":
                inputs = getInputs(-1, 1, 0.1, 1);
                data = getData(inputs, new N6());
                break;
            case "Nguyen7":
                inputs = getInputs(-1, 1, 0.1, 1);
                data = getData(inputs, new N7());
                break;
            case "Nguyen9":
                inputs = getInputs(-2, 2, 1, 2);
                data = getData(inputs, new N9());
                break;
            case "Nguyen10":
                inputs = getInputs(-2, 2, 1, 2);
                data = getData(inputs, new N10());
                break;
            case "Nguyen12":
                inputs = getInputs(-2, 2, 1, 2);
                data = getData(inputs, new N12());
                break;
            case "Sext":
            default:
                data = null;
        }
        return data;
    }

    private static List<List<Double>> getData(List<List<Double>> inputs, BenchmarkFunction f) {
        List<List<Double>> data = new ArrayList<>(inputs);
        for (int i = 0; i < inputs.size(); i++) {
            List<Double> input = inputs.get(i);
            double output = f.call(input);
            data.get(i).add(output);
        }
        return data;
    }

    private static List<List<Double>> getInputs(double low, double high, double step, int numVars) {
        if (numVars == 0) {
            List<List<Double>> output = new ArrayList<>();
            output.add(new ArrayList<>());
            return output;
        } else {
            List<List<Double>> prevInputs = getInputs(low, high, step, numVars - 1);
            List<List<Double>> output = new ArrayList<>();
            for (List<Double> prevInput : prevInputs) {
                double current = low;
                while (current <= high) {
                    List<Double> input = new ArrayList<>(prevInput);
                    input.add(current);
                    output.add(input);
                    current += step;
                }
            }
            return output;
        }
    }

    private static void saveText(String filepath, String text, Boolean append) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, append));
            PrintWriter printWriter = new PrintWriter(bw);
            printWriter.write(text);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    private static void saveDataSet(List<List<Double>> dataSet, String filepath) {
        saveText(filepath, "", false);
        for (List<Double> trainingPoint : dataSet) {
            List<String> trainingPointString = new ArrayList<>();
            for (Double elm : trainingPoint) {
                trainingPointString.add(elm.toString());
            }
            String line = String.join(",", trainingPointString);
            saveText(filepath, line, true);
            saveText(filepath, "\n", true);
        }
    }

    public static void main(String args[]) {
        String problem = args[1];
        String filepath = args[3];
        List<List<Double>> dataSet = constructDataSet(problem);
        saveDataSet(dataSet, filepath);
    }


    private interface BenchmarkFunction {

        Double call(List<Double> input);
    }

    private static class K1 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 1) {
                double x = input.get(0);
                return 0.3 * x * Math.sin(2 * Math.PI * x);
            } else {
                return null;
            }
        }
    }

    private static class K4 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 1) {
                double x = input.get(0);
                return Math.pow(x, 3)*Math.exp(-x)*Math.cos(x)*Math.sin(x)*(Math.pow(Math.sin(x), 2)*Math.cos(x) - 1);
            } else {
                return null;
            }
        }
    }

    private static class K5 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 3) {
                double x = input.get(0);
                double y = input.get(1);
                double z = input.get(2);
                return (30*x*z)/((x-10)*Math.pow(y, 2));
            } else {
                return null;
            }
        }
    }

    private static class K11 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 2) {
                double x = input.get(0);
                double y = input.get(1);
                return x*y + Math.sin((x-1)*(y-1));
            } else {
                return null;
            }
        }
    }

    private static class K12 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 2) {
                double x = input.get(0);
                double y = input.get(1);
                return Math.pow(x, 4) - Math.pow(x, 3) + Math.pow(y, 2)/2 - y;
            } else {
                return null;
            }
        }
    }

    private static class K13 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 2) {
                double x = input.get(0);
                double y = input.get(1);
                return 6*Math.sin(x)*Math.cos(y);
            } else {
                return null;
            }
        }
    }

    private static class K14 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 2) {
                double x = input.get(0);
                double y = input.get(1);
                return 8.0/(1 + Math.pow(x, 2) + Math.pow(y, 2));
            } else {
                return null;
            }
        }
    }

    private static class K15 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 2) {
                double x = input.get(0);
                double y = input.get(1);
                return Math.pow(x, 3)/5 + Math.pow(y, 3)/2 - y - x;
            } else {
                return null;
            }
        }
    }

    private static class N3 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 1) {
                double x = input.get(0);
                return Math.pow(x, 5) + Math.pow(x, 4) + Math.pow(x, 3) + Math.pow(x, 2) + x;
            } else {
                return null;
            }
        }
    }

    private static class N4 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 1) {
                double x = input.get(0);
                return Math.pow(x, 6) + Math.pow(x, 5) + Math.pow(x, 4) + Math.pow(x, 3) + Math.pow(x, 2) + x;
            } else {
                return null;
            }
        }
    }

    private static class N5 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 1) {
                double x = input.get(0);
                return Math.sin(Math.pow(x, 2))*Math.cos(x) - 1;
            } else {
                return null;
            }
        }
    }

    private static class N6 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 1) {
                double x = input.get(0);
                return Math.sin(x) + Math.sin(x + Math.pow(x, 2));
            } else {
                return null;
            }
        }
    }

    private static class N7 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 1) {
                double x = input.get(0);
                return Math.log1p(x) + Math.log1p(Math.pow(x, 2));
            } else {
                return null;
            }
        }
    }

    private static class N9 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 2) {
                double x = input.get(0);
                double y = input.get(1);
                return Math.sin(x) + Math.sin(Math.pow(y, 2));
            } else {
                return null;
            }
        }
    }

    private static class N10 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 2) {
                double x = input.get(0);
                double y = input.get(1);
                return 2*Math.sin(x)*Math.cos(y);
            } else {
                return null;
            }
        }
    }

    private static class N12 implements BenchmarkFunction {

        @Override
        public Double call(List<Double> input) {
            if (input.size() == 2) {
                double x = input.get(0);
                double y = input.get(1);
                return Math.pow(x, 4) - Math.pow(x, 3) + Math.pow(y, 2)/2 - y;
            } else {
                return null;
            }
        }
    }
}
