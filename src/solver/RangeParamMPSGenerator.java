package solver;

import dataStore.DataInOut;
import dataStore.DataStorer;
import java.io.File;
import java.text.DecimalFormat;

/**
 * Generates multiple MPS files for a given dataset/scenario with different model parameter values.
 * Loads data once and generates MPS files for each parameter value in the provided sequence.
 * Useful for sensitivity analysis on capacity targets or CO2 prices while keeping CRF fixed.
 * 
 * @author Generated
 */
public class RangeParamMPSGenerator {

    private String basePath;
    private String dataset;
    private String scenario;
    private DataStorer data;
    private double numYears;
    private double crf;

    public RangeParamMPSGenerator(String basePath, String dataset, String scenario, double numYears, double crf) {
        this.basePath = basePath;
        this.dataset = dataset;
        this.scenario = scenario;
        this.numYears = numYears;
        this.crf = crf;
        this.data = new DataStorer(basePath, dataset, scenario);
    }

    /**
     * Loads all data once from disk.
     */
    public void loadData() {
        System.out.println("Loading data for " + dataset + "/" + scenario + "...");
        DataInOut loader = new DataInOut();
        loader.loadData(basePath, dataset, scenario, data);
        System.out.println("Data loaded successfully.\n");
    }

    /**
     * Generates MPS files for each parameter value in the provided array.
     * 
     * @param paramValues Array of model parameter values (capacity targets or CO2 prices)
     * @param modelVersion "c" for capacity model, "p" for price model
     */
    public void generateMPSSequence(double[] paramValues, String modelVersion) {
        if (data.getSources() == null) {
            System.err.println("Error: Data not loaded. Call loadData() first.");
            return;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        String paramName = modelVersion.equals("c") ? "cap" : "price";
        int total = paramValues.length;
        int count = 0;

        for (double paramValue : paramValues) {
            count++;
            System.out.println("[" + count + "/" + total + "] Generating MPS for " + paramName + " = " + df.format(paramValue) + "...");
            
            // Generate MPS
            MPSWriter.writeCapPriceMPS(data, crf, numYears, paramValue, basePath, dataset, scenario, modelVersion);
            
            // Rename the output file to include parameter value
            String mipsDirectory = basePath + "/" + dataset + "/Scenarios/" + scenario + "/MIP/";
            String modelTypeStr = modelVersion.equals("c") ? "cap" : "price";
            String originalFile = mipsDirectory + modelTypeStr + ".mps";
            String newFileName = mipsDirectory + modelTypeStr + "_crf_" + df.format(crf) + "_" + paramName + "_" + df.format(paramValue) + ".mps";
            
            File original = new File(originalFile);
            File renamed = new File(newFileName);
            
            if (original.exists()) {
                if (original.renameTo(renamed)) {
                    System.out.println("  → Saved as: " + renamed.getName());
                } else {
                    System.err.println("  ✗ Failed to rename file.");
                }
            } else {
                System.err.println("  ✗ Generated MPS file not found at: " + originalFile);
            }
        }

        System.out.println("\nBatch generation complete. Generated " + total + " MPS files.");
    }

    /**
     * Generates MPS files for a range of parameter values.
     * 
     * @param paramMin Minimum parameter value
     * @param paramMax Maximum parameter value
     * @param paramStep Step size
     * @param modelVersion "c" for capacity model, "p" for price model
     */
    public void generateMPSRange(double paramMin, double paramMax, double paramStep, String modelVersion) {
        int numSteps = (int) Math.round((paramMax - paramMin) / paramStep) + 1;
        double[] paramValues = new double[numSteps];
        
        for (int i = 0; i < numSteps; i++) {
            paramValues[i] = paramMin + i * paramStep;
        }
        
        generateMPSSequence(paramValues, modelVersion);
    }

    /**
     * Gets the loaded DataStorer (useful for inspection or custom processing).
     */
    public DataStorer getData() {
        return data;
    }

    /**
     * Gets the CRF value being used.
     */
    public double getCRF() {
        return crf;
    }
}
