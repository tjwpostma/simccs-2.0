package solver;

/**
 * Command-line interface for generating MPS files across a range of model parameters.
 * Holds CRF constant and varies capacity target (if capacity model) or CO2 price (if price model).
 * 
 * Usage:
 *   java solver.RangeParamMPSCLI <basePath> <dataset> <scenario> <numYears> <crf> <modelVersion> <paramMin> <paramMax> <paramStep>
 * 
 * Example (Capacity Model - varying capacity target):
 *   java solver.RangeParamMPSCLI /path/to/Datasets NZCA 150_sources 30 0.12 c 30 100 10
 *   Generates MPS for capacity targets: 30, 40, 50, 60, 70, 80, 90, 100 MtCO2/yr (CRF fixed at 0.12)
 * 
 * Example (Price Model - varying CO2 price):
 *   java solver.RangeParamMPSCLI /path/to/Datasets NZCA 150_sources 30 0.12 p 20 100 10
 *   Generates MPS for CO2 prices: 20, 30, 40, 50, 60, 70, 80, 90, 100 $/tCO2 (CRF fixed at 0.12)
 * 
 * Parameters:
 *   basePath         - Root path to Datasets directory
 *   dataset          - Dataset name (e.g., NZCA, CA_May2020)
 *   scenario         - Scenario name within dataset
 *   numYears         - Project length in years (e.g., 30)
 *   crf              - Capital Recovery Factor (e.g., 0.12 = 12%)
 *   modelVersion     - "c" for capacity-constrained, "p" for price-sensitive
 *   paramMin         - Minimum parameter (capacity MtCO2/yr if c, price $/tCO2 if p)
 *   paramMax         - Maximum parameter
 *   paramStep        - Parameter increment
 */
public class RangeParamMPSCLI {

    public static void main(String[] args) {
        if (args.length < 9) {
            printUsage();
            System.exit(1);
        }

        try {
            String basePath = args[0];
            String dataset = args[1];
            String scenario = args[2];
            double numYears = Double.parseDouble(args[3]);
            double crf = Double.parseDouble(args[4]);
            String modelVersion = args[5];
            double paramMin = Double.parseDouble(args[6]);
            double paramMax = Double.parseDouble(args[7]);
            double paramStep = Double.parseDouble(args[8]);

            // Validate inputs
            if (!modelVersion.equals("c") && !modelVersion.equals("p")) {
                System.err.println("Error: modelVersion must be 'c' or 'p'");
                System.exit(1);
            }

            if (crf < 0) {
                System.err.println("Error: CRF must be non-negative");
                System.exit(1);
            }

            if (paramMin < 0 || paramMax < 0 || paramStep <= 0) {
                System.err.println("Error: Parameter values must be non-negative, step must be positive");
                System.exit(1);
            }

            if (paramMin > paramMax) {
                System.err.println("Error: paramMin cannot be greater than paramMax");
                System.exit(1);
            }

            // Create and run range generator
            RangeParamMPSGenerator generator = new RangeParamMPSGenerator(basePath, dataset, scenario, numYears, crf);
            generator.loadData();
            generator.generateMPSRange(paramMin, paramMax, paramStep, modelVersion);

            System.out.println("\nGenerated MPS files are located in:");
            System.out.println(basePath + "/" + dataset + "/Scenarios/" + scenario + "/MIP/");

        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid numeric argument - " + e.getMessage());
            printUsage();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Range Parameter MPS File Generator");
        System.out.println("===================================");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -cp bin solver.RangeParamMPSCLI <basePath> <dataset> <scenario> <numYears> <crf> <modelVersion> <paramMin> <paramMax> <paramStep>");
        System.out.println();
        System.out.println("Example (Capacity Model - varying capacity targets):");
        System.out.println("  java -cp bin solver.RangeParamMPSCLI /path/to/Datasets NZCA 150_sources 30 0.12 c 30 100 10");
        System.out.println("  Generates: cap_crf_0.12_cap_30.00.mps, cap_crf_0.12_cap_40.00.mps, ..., cap_crf_0.12_cap_100.00.mps");
        System.out.println();
        System.out.println("Example (Price Model - varying CO2 prices):");
        System.out.println("  java -cp bin solver.RangeParamMPSCLI /path/to/Datasets NZCA 150_sources 30 0.12 p 20 100 10");
        System.out.println("  Generates: price_crf_0.12_price_20.00.mps, price_crf_0.12_price_30.00.mps, ..., price_crf_0.12_price_100.00.mps");
        System.out.println();
        System.out.println("Parameters:");
        System.out.println("  basePath         Root path to Datasets directory");
        System.out.println("  dataset          Dataset name (e.g., NZCA, CA_May2020, SoutheastUS)");
        System.out.println("  scenario         Scenario name within dataset (e.g., 150_sources)");
        System.out.println("  numYears         Project length in years (e.g., 30)");
        System.out.println("  crf              Capital Recovery Factor (e.g., 0.12 = 12%, held constant)");
        System.out.println("  modelVersion     'c' for capacity-constrained, 'p' for price-sensitive");
        System.out.println("  paramMin         Minimum parameter value (capacity MtCO2/yr if c, price $/tCO2 if p)");
        System.out.println("  paramMax         Maximum parameter value");
        System.out.println("  paramStep        Parameter increment");
        System.out.println();
        System.out.println("Use cases:");
        System.out.println("  • Capacity model: Study how changing CO2 capture targets affects optimal network");
        System.out.println("  • Price model:    Study how CO2 tax/credit policies affect solution for fixed CRF");
        System.out.println();
    }
}
