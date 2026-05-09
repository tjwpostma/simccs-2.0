package solver;

import dataStore.DataInOut;
import dataStore.DataStorer;
import java.io.File;
import java.text.DecimalFormat;

/**
 * Generates multiple MPS files for a given dataset/scenario with different CRF values.
 * Loads data once and generates MPS files for each CRF in the provided sequence.
 * Useful for sensitivity analysis on capital recovery factors while keeping capacity/price fixed.
 * 
 * @author Generated
 */
public class RangeCrfMPSGenerator {

    private String basePath;
    private String dataset;
    private String scenario;
    private DataStorer data;
    private double numYears;

    public RangeCrfMPSGenerator(String basePath, String dataset, String scenario, double numYears) {
        this.basePath = basePath;
        this.dataset = dataset;
        this.scenario = scenario;
        this.numYears = numYears;
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
     * Generates MPS files for each CRF value in the provided array.
     * 
     * @param crfValues Array of CRF values to generate MPS for
     * @param modelVersion "c" for capacity model, "p" for price model
     * @param modelParamValue Capacity target (if modelVersion="c") or CO2 price (if modelVersion="p")
     */
    public void generateMPSSequence(double[] crfValues, String modelVersion, double modelParamValue) {
        if (data.getSources() == null) {
            System.err.println("Error: Data not loaded. Call loadData() first.");
            return;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        int total = crfValues.length;
        int count = 0;

        for (double crf : crfValues) {
            count++;
            System.out.println("[" + count + "/" + total + "] Generating MPS for CRF = " + df.format(crf) + "...");
            
            // Generate MPS
            MPSWriter.writeCapPriceMPS(data, crf, numYears, modelParamValue, basePath, dataset, scenario, modelVersion);
            
            // Rename the output file to include CRF value
            String mipsDirectory = basePath + "/" + dataset + "/Scenarios/" + scenario + "/MIP/";
            String modelTypeStr = modelVersion.equals("c") ? "cap" : "price";
            String originalFile = mipsDirectory + modelTypeStr + ".mps";
            String newFileName = mipsDirectory + modelTypeStr + "_crf_" + df.format(crf) + ".mps";
            
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
     * Generates MPS files for a range of CRF values.
     * 
     * @param crfMin Minimum CRF value
     * @param crfMax Maximum CRF value
     * @param crfStep Step size
     * @param modelVersion "c" for capacity model, "p" for price model
     * @param modelParamValue Capacity target or CO2 price
     */
    public void generateMPSRange(double crfMin, double crfMax, double crfStep, String modelVersion, double modelParamValue) {
        int numSteps = (int) Math.round((crfMax - crfMin) / crfStep) + 1;
        double[] crfValues = new double[numSteps];
        
        for (int i = 0; i < numSteps; i++) {
            crfValues[i] = crfMin + i * crfStep;
        }
        
        generateMPSSequence(crfValues, modelVersion, modelParamValue);
    }

    /**
     * Gets the loaded DataStorer (useful for inspection or custom processing).
     */
    public DataStorer getData() {
        return data;
    }
}
