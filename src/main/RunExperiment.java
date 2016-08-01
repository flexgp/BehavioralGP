package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by stevenfine on 7/29/16.
 */
public class RunExperiment {

    public static void main(String[] args) throws FileNotFoundException {
        String filename;
        Scanner scanner;
        if (args.length == 2) {
            if (args[0].equals("-runs")) {
                filename = args[1];
                scanner = new Scanner(new File(filename));
                scanner.useDelimiter("\n");
                while (scanner.hasNext()) {
                    parseRun(scanner.next());
                }
            } else {
                System.err.println("Error: expected -runs flag");
                System.exit(-1);
            }
        } else {
            System.err.println("Error: wrong number of arguments");
            System.exit(-1);
        }
    }

    /*
     * trainingData
     * testingData
     * store output
     */
    private static void parseRun(String run) {
        String trainingDataPath;
        String testingDataPath;
        String propertiesPath;
        String durationOfRun;
        int repetitions;
        String[] args = run.split(" ");
        if (args.length == 9) {
            if (args[0].equals("-data")) {
                trainingDataPath = args[1];
                testingDataPath = args[2];
                if (args[3].equals("-properties")) {
                    propertiesPath = args[4];
                    if (args[5].equals("-minutes")) {
                        durationOfRun = args[6];
                        if (args[7].equals("-repetitions")) {
                            repetitions = Integer.parseInt(args[8]);
                            for (int i = 0; i < repetitions; i++) {
                                SRLearnerMenuManager m = new SRLearnerMenuManager();
                                train(m, trainingDataPath, durationOfRun, propertiesPath);
                                test(m, testingDataPath);
                            }
                        } else {
                            System.err.println("Error: expected -repetitions flag");
                            System.exit(-1);
                        }
                    } else {
                        System.err.println("Error: expected -minutes flag");
                        System.exit(-1);
                    }
                } else {
                    System.err.println("Error: expected -properties flag");
                    System.exit(-1);
                }
            } else {
                System.err.println("Error: expected -data flag");
                System.exit(-1);
            }
        } else {
            System.err.println("Error: wrong number of arguments");
            System.exit(-1);
        }
    }

    private static void train(SRLearnerMenuManager m, String dataPath, String durationOfRun, String propertiesPath) {
        String[] args = {
                "-train",
                dataPath,
                "-minutes",
                durationOfRun,
                "-properties",
                propertiesPath
        };
        try {
            m.parseSymbolicRegressionTrain(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void test(SRLearnerMenuManager m, String dataPath) {
        String[] args = {
                "-test",
                dataPath
        };
        try {
            m.parseSymbolicRegressionTest(args);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void storeResults() {

    }
}
