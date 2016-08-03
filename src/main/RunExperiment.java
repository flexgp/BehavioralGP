package main;

import evogpj.algorithm.Parameters;

import java.io.*;
import java.util.Scanner;

/**
 * Created by stevenfine on 7/29/16.
 */
public class RunExperiment {

    private static final String EXPERIMENT_OUTPUT = Parameters.Defaults.EXPERIMENT_OUTPUT;

    public static void main(String[] args) throws FileNotFoundException {
        saveText(EXPERIMENT_OUTPUT, "", false);
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
                            saveText(EXPERIMENT_OUTPUT, "########################\n", true);
                            saveText(EXPERIMENT_OUTPUT, "training data: " + trainingDataPath + "\n", true);
                            saveText(EXPERIMENT_OUTPUT, "testing data: " + testingDataPath + "\n", true);
                            saveText(EXPERIMENT_OUTPUT, "properties: " + propertiesPath + "\n", true);
                            saveText(EXPERIMENT_OUTPUT, "run duration: " + durationOfRun + "\n", true);
                            saveText(EXPERIMENT_OUTPUT, "repetitions: " + repetitions + "\n", true);
                            saveText(EXPERIMENT_OUTPUT, "########################\n", true);
                            for (int i = 0; i < repetitions; i++) {
                                SRLearnerMenuManager m = new SRLearnerMenuManager();
                                train(m, trainingDataPath, durationOfRun, propertiesPath);
                                test(m, testingDataPath);
                                saveText(EXPERIMENT_OUTPUT, "##### Repetition " + (i+1) + " #####\n", true);
                                saveOutput();
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

    private static void saveOutput() {
        File folder = new File("./");
        for (File file : folder.listFiles()) {
            if (file.getName().matches("test(.*).txt")) {
                saveText(EXPERIMENT_OUTPUT, file.getName() + "\n", true);
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        saveText(EXPERIMENT_OUTPUT, line + "\n", true);
                    }
                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                saveText(EXPERIMENT_OUTPUT, "------------------------\n", true);
            }
        }
    }

    /**
     * Save text to a filepath
     * @param filepath
     * @param text
     * @param append
     */
    private static void saveText(String filepath, String text, Boolean append) {
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
}
