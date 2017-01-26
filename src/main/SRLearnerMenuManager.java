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
package main;

import evogpj.algorithm.Parameters;
import evogpj.algorithm.SymbRegMOO;
import evogpj.genotype.Tree;
import evogpj.gp.Individual;
import evogpj.test.TestModels;

import java.io.*;
import java.util.Properties;

/**
 *
 * @author Ignacio Arnaldo
 */
public class SRLearnerMenuManager {
    
    public SRLearnerMenuManager(){
        
    }
    
    public void printUsage(){
        System.err.println();
        System.err.println("USAGE:");
        System.err.println();
        System.err.println("TRAIN:");
        System.err.println("java -jar sr.jar -train path_to_data -minutes min [-properties path_to_properties]");
        System.err.println();
        System.err.println("OBTAIN PREDICTIONS:");
        System.err.println("java -jar sr.jar -predict path_to_data -o path_to_predictions -integer true -scaled path_to_scaled_models");
        System.err.println();
        System.err.println("TEST:");
        System.err.println("java -jar sr.jar -test path_to_data");
        System.err.println("or");
        System.err.println("java -jar sr.jar -test path_to_data -integer true -scaled path_to_scaled_models");
        System.err.println();
    }

    public void parseSymbolicRegressionRun(String args[]) throws IOException {
        String dataPath;
        int numMinutes = 0;
        SymbRegMOO srEvoGPj;
        if (args.length==10) {
            dataPath = args[1];
            if (args[2].equals("-minutes")) {
                numMinutes = Integer.valueOf(args[3]);
                if(args[4].equals("-properties")){ // JAVA WITH PROPERTIES
                    String propsFile = args[5];
                    if (args[6].equals("-runs") && args[8].equals("-filename")) {
                        int numRuns = Integer.parseInt(args[7]);
                        String filename = args[9];
                        double totalFitness = 0;
                        double totalSize = 0;
                        long totalTime = 0;
                        long currentTime = System.currentTimeMillis();
                        saveText(filename, "", false);
                        System.out.format("Running %s\n", filename);
                        System.out.flush();
                        Properties props = new Properties();
                        for (int i = 0; i < numRuns; i++) {
                            props.clear();
                            props.put(Parameters.Names.PROBLEM, dataPath);
                            props.put(Parameters.Names.SEED, String.valueOf(System.currentTimeMillis() + i));
                            srEvoGPj = new SymbRegMOO(props,propsFile,numMinutes*60);
                            Individual bestIndi = srEvoGPj.run_population();
                            double fitness = bestIndi.getFitness();
                            int size = ((Tree) bestIndi.getGenotype()).getSize();
                            long time = System.currentTimeMillis() - currentTime;
                            totalFitness += fitness;
                            totalSize += (double) size;
                            totalTime += time;
                            saveText(filename, Double.toString(fitness) + "," + Integer.toString(size) + "," + Long.toString(time) + "\n", true);
                            currentTime = System.currentTimeMillis();
                        }
                        double averageFitness = totalFitness/(double) numRuns;
                        double averageSize = totalSize/(double) numRuns;
                        double averageTime = (double) totalTime/(double) numRuns;
//                        System.out.format("Average fitness: %s; Average size: %s", averageFitness, averageSize);
                        saveText(filename, "Average:", true);
                        saveText(filename, Double.toString(averageFitness) + "," + Double.toString(averageSize) + "," + Double.toString(averageTime) + "\n", true);
                    }
                    // run evogpj with properties file and modified properties
                }else{
                    printUsage();
                    System.exit(-1);
                }
            }else{
                printUsage();
                System.exit(-1);
            }
        } else {
            printUsage();
            System.exit(-1);
        }
    }
    
    public void parseSymbolicRegressionTrain(String args[]) throws IOException{
        String dataPath;
        int numMinutes=0;
        String propsFile = "";
        SymbRegMOO srEvoGPj;
        if(args.length==4 || args.length==6){
            dataPath = args[1];
            // run evogpj with standard properties
            Properties props = new Properties();
            props.put(Parameters.Names.PROBLEM, dataPath);
            if (args[2].equals("-minutes")) {
                numMinutes = Integer.valueOf(args[3]);
                if(args.length==4){// JAVA NO PROPERTIES
                    // run evogpj with standard properties
                    srEvoGPj = new SymbRegMOO(props,numMinutes*60);
                    Individual bestIndi = srEvoGPj.run_population();
                }else if(args.length==6){
                    if(args[4].equals("-properties")){ // JAVA WITH PROPERTIES
                        propsFile = args[5];
                        // run evogpj with properties file and modified properties
                        srEvoGPj = new SymbRegMOO(props,propsFile,numMinutes*60);
                        Individual bestIndi = srEvoGPj.run_population();
                    }else{
                        System.err.println("Error: wrong argument. Expected -cpp flag");
                        printUsage();
                        System.exit(-1);
                    }
                }
            }else{
                System.err.println("Error: must specify the optimization time in minutes");
                printUsage();
                System.exit(-1);
            }
        }else{
            System.err.println("Error: wrong number of arguments");
            printUsage();
            System.exit(-1);
        }
    }
    
    
    //java -jar evogpj.jar -predictions path_to_data -o filename -integer true -scaled path_to_scaled_models
    public void parseSymbolicRegressionPredictions(String args[]) throws IOException, ClassNotFoundException{
        String dataPath;
        String popPath;
        String predPath;
        boolean integerTarget;
        if(args.length==8){
            dataPath = args[1];
            if(args[2].equals("-o")){
                predPath = args[3];
                if(args[4].equals("-integer")){
                    integerTarget = Boolean.valueOf(args[5]);
                    popPath = args[7];
                    if(args[6].equals("-scaled")){
                        TestModels tsm = new TestModels(dataPath, popPath,integerTarget);
                        tsm.predictionsPop(predPath);
                    }else{
                        System.err.println("Error: wrong argument. Expected -scaled flag");
                        printUsage();
                        System.exit(-1);
                    }
                }else{
                    System.err.println("Error: wrong argument. Expected -integer flag");
                    printUsage();
                    System.exit(-1);
                }
            }else{
                System.err.println("Error: wrong argument. Expected -o flag");
                printUsage();
                System.exit(-1);
            }
        }else {
            System.err.println("Error: wrong number of arguments");
            printUsage();
            System.exit(-1);
        }
        
    }
    
    //java -jar evogpj.jar -test path_to_data -integer true -scaled path_to_scaled_models
    public void parseSymbolicRegressionTest(String args[]) throws IOException, ClassNotFoundException{
        String dataPath;
        String popPath;
        boolean integerTarget;
        if (args.length==2){
            // by default integer targets = false
            integerTarget = false;
            dataPath = args[1];
            // check if knee model exists
            popPath = "knee.txt";
            System.out.println();
            if(new File(popPath).isFile()){
                System.out.println("TESTING KNEE MODEL:");
                TestModels tsm = new TestModels(dataPath, popPath,integerTarget);
                tsm.evalPop();
                tsm.saveModelsToFile("test"+popPath);
                System.out.println();
            }
            popPath = "mostAccurate.txt";
            if(new File(popPath).isFile()){
                System.out.println("TESTING MOST ACCURATE MODEL: ");
                TestModels tsm = new TestModels(dataPath, popPath,integerTarget);
                tsm.evalPop();
                tsm.saveModelsToFile("test"+popPath);
                System.out.println();
            }
            popPath = "leastComplex.txt";
            if(new File(popPath).isFile()){
                System.out.println("TESTING SIMPLEST MODEL: ");
                TestModels tsm = new TestModels(dataPath, popPath,integerTarget);
                tsm.evalPop();
                tsm.saveModelsToFile("test"+popPath);
                System.out.println();
            }
            popPath = "pareto.txt";
            if(new File(popPath).isFile()){
                System.out.println("TESTING PARETO MODELS: ");
                TestModels tsm = new TestModels(dataPath, popPath,integerTarget);
                tsm.evalPop();
                tsm.saveModelsToFile("test"+popPath);
                System.out.println();
            }
            
        }else if(args.length==6){
            dataPath = args[1];
            if(args[2].equals("-integer")){
                integerTarget = Boolean.valueOf(args[3]);
                popPath = args[5];
                if(args[4].equals("-scaled")){
                    TestModels tsm = new TestModels(dataPath, popPath,integerTarget);
                    tsm.evalPop();
                    tsm.saveModelsToFile("test"+popPath);
                }else{
                    System.err.println("Error: wrong argument. Expected -scaled or -fused flag");
                    printUsage();
                    System.exit(-1);
                }
            }else{
                System.err.println("Error: wrong argument. Expected -integer flag");
                printUsage();
                System.exit(-1);
            }
        }else {
            System.err.println("Error: wrong number of arguments");
            printUsage();
            System.exit(-1);
        }
        
    }
    
    
    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException{
        SRLearnerMenuManager m = new SRLearnerMenuManager();
        if (args.length == 0) {
            System.err.println("Error: too few arguments");
            m.printUsage();
            System.exit(-1);
        }else{
            switch (args[0]) {
                case "-run":
                    m.parseSymbolicRegressionRun(args);
                    break;
                case "-train":
                    m.parseSymbolicRegressionTrain(args);
                    break;
                case "-predict":
                    m.parseSymbolicRegressionPredictions(args);
                    break;
                case "-test":
                    m.parseSymbolicRegressionTest(args);
                    break;
                default:
                    System.err.println("Error: unknown argument");
                    m.printUsage();
                    System.exit(-1);
                    break;
            }
        }
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
}
