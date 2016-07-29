package evogpj.test;

import evogpj.evaluation.java.CSVDataJava;
import evogpj.evaluation.java.DataJava;
import evogpj.genotype.Tree;
import evogpj.genotype.TreeGenerator;
import evogpj.gp.Individual;
import evogpj.gp.Population;
import evogpj.math.Function;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by stevenfine on 7/29/16.
 */
public class TestModels {
    private String pathToData;
    private final DataJava data;

    private String pathToPop;
    private Population models;

    private boolean round;

    private double minTarget, maxTarget;

    private static final int MRGP_TOKENS = 5;
    private boolean mrgp = false;
    /**
     * Create a new fitness operator, using the provided data, for assessing
     * individual solutions to Symbolic Regression problems. There is one
     * parameter for this fitness evaluation:
     * @param aPathToData
     * @param aPathToPop
     * @param aRound
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public TestModels(String aPathToData, String aPathToPop,boolean aRound) throws IOException, ClassNotFoundException {
        pathToData = aPathToData;
        pathToPop = aPathToPop;
        round = aRound;
        this.data = new CSVDataJava(pathToData);
        readScaledModels(pathToPop);
    }


    private void readScaledModels(String filePath) throws IOException, ClassNotFoundException{
        models = new Population();
        ArrayList<String> alModels = new ArrayList<String>();
        Scanner sc = new Scanner(new FileReader(filePath));
        int indexModel =0;
        while(sc.hasNextLine()){
            String sAux = sc.nextLine();
            alModels.add(indexModel, sAux);
            indexModel++;
        }
        int popSize = alModels.size();
        for(int i=0;i<popSize;i++){
            String scaledModel = alModels.get(i);
            String[] tokens = scaledModel.split(",");
            mrgp = tokens.length == MRGP_TOKENS;
            minTarget = Double.parseDouble(tokens[0]);
            maxTarget = Double.parseDouble(tokens[1]);
            ArrayList<String> alWeights = null;
            String interceptS = null;
            String model = null;
            if (mrgp) {
                String[] weightsArrayS = tokens[2].split(" ");
                alWeights = new ArrayList<String>();
                for(int j=0;j<weightsArrayS.length;j++){
                    alWeights.add(weightsArrayS[j]);
                }
                interceptS = tokens[3];
                model = tokens[4];
            } else {
                model = tokens[2];
            }
            Tree g = TreeGenerator.generateTree(model);
            Individual iAux = new Individual(g);
            if (mrgp) {
                iAux.setWeights(alWeights);
                iAux.setLassoIntercept(interceptS);
            }
            models.add(i, iAux);
        }
    }

    /**
     * @see Function
     */
    public void predictionsPop(String filePath) throws IOException {

        int indexIndi = 0;

        double[] targets = data.getTargetValues();
        for(Individual ind:models){
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath + "_" + indexIndi + ".csv"));
            PrintWriter printWriter = new PrintWriter(bw);
            ArrayList<String> alWeights = null;
            double[] lassoWeights = null;
            double lassoIntercept = 0;
            if (mrgp) {
                alWeights = ind.getWeights();
                lassoWeights = new double[alWeights.size()];
                for(int i=0;i<alWeights.size();i++){
                    lassoWeights[i] = Double.parseDouble(alWeights.get(i));
                }
                lassoIntercept = Double.parseDouble(ind.getLassoIntercept());
            }
            double sqDiff = 0;
            double absDiff = 0;
            Tree genotype = (Tree) ind.getGenotype();
            Function func = genotype.generate();
            List<Double> d;
            ArrayList<Double> interVals;
            double[][] inputValuesAux = data.getInputValues();
            float[][] intermediateValues = new float[data.getNumberOfFitnessCases()][genotype.getSize()];
            for (int i = 0; i < data.getNumberOfFitnessCases(); i++) {
                d = new ArrayList<Double>();
                for (int j = 0; j < data.getNumberOfFeatures(); j++) {
                    d.add(j, inputValuesAux[i][j]);
                }
                interVals = new ArrayList<Double>();
                double prediction = 0;
                if (mrgp) {
                    func.evalIntermediate(d,interVals);
                    for(int t=0;t<interVals.size();t++){
                        intermediateValues[i][t] = interVals.get(t).floatValue();
                    }
                    for(int j=0;j<lassoWeights.length;j++){
                        prediction += intermediateValues[i][j]*lassoWeights[j];
                    }
                    prediction += lassoIntercept;
                } else {
                    prediction = func.eval(d);
                }
                if (round) prediction = Math.round(prediction);
                if(prediction<minTarget) prediction = minTarget;
                if(prediction>maxTarget) prediction = maxTarget;
                d.clear();
                interVals.clear();
                printWriter.println(prediction);
            }
            printWriter.flush();
            printWriter.close();
            func = null;
            indexIndi++;
        }
    }

    /**
     * @see Function
     */
    public void evalPop() {
        double[] targets = data.getTargetValues();
        for(Individual ind:models){
            ArrayList<String> alWeights = null;
            double[] lassoWeights = null;
            double lassoIntercept = 0;
            if (mrgp) {
                alWeights = ind.getWeights();
                lassoWeights = new double[alWeights.size()];
                for(int i=0;i<alWeights.size();i++){
                    lassoWeights[i] = Double.parseDouble(alWeights.get(i));
                }
                lassoIntercept = Double.parseDouble(ind.getLassoIntercept());
            }
            double sqDiff = 0;
            double absDiff = 0;
            Tree genotype = (Tree) ind.getGenotype();
            Function func = genotype.generate();
            List<Double> d;
            ArrayList<Double> interVals;
            double[][] inputValuesAux = data.getInputValues();
            float[][] intermediateValues = new float[data.getNumberOfFitnessCases()][genotype.getSize()];
            for (int i = 0; i < data.getNumberOfFitnessCases(); i++) {
                d = new ArrayList<Double>();
                for (int j = 0; j < data.getNumberOfFeatures(); j++) {
                    d.add(j, inputValuesAux[i][j]);
                }
                interVals = new ArrayList<Double>();
                double prediction = 0;
                if (mrgp) {
                    func.evalIntermediate(d, interVals);
                    for (int t = 0; t < interVals.size(); t++) {
                        intermediateValues[i][t] = interVals.get(t).floatValue();
                    }
                    for (int j = 0; j < lassoWeights.length; j++) {
                        prediction += intermediateValues[i][j] * lassoWeights[j];
                    }
                    prediction += lassoIntercept;
                } else {
                    prediction = func.eval(d);
                }
                //phenotype_tmp.addNewDataValue(prediction);
                if (round) prediction = Math.round(prediction);
                if(prediction<minTarget) prediction = minTarget;
                if(prediction>maxTarget) prediction = maxTarget;
                d.clear();
                interVals.clear();

                sqDiff += Math.pow(targets[i] - prediction, 2);
                absDiff += Math.abs(targets[i] - prediction);
                d.clear();
            }
            sqDiff = sqDiff / data.getNumberOfFitnessCases();
            absDiff= absDiff / data.getNumberOfFitnessCases();
            ind.setScaledMSE(sqDiff);
            ind.setScaledMAE(absDiff);
            func = null;
        }
    }

    public void saveModelsToFile(String filePath) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
        PrintWriter printWriter = new PrintWriter(bw);

        for(Individual ind:models){
            System.out.print( ind.toString() + "\nMSE: " + ind.getScaledMSE() + "\nMAE: " + ind.getScaledMAE() + "\n");
            printWriter.write(ind.toString() + "\nMSE: " + ind.getScaledMSE() + "\nMAE: " + ind.getScaledMAE() + "\n");
        }
        printWriter.flush();
        printWriter.close();
    }
}
