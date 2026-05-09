package solver;

/**
 * Command-line interface for CRF sensitivity analysis.
 * Generates multiple MPS files with varying CRF values while holding capacity target or CO2 price fixed.
 * 
 * Usage:
 *   java -cp bin solver.RangeCrfMPSCLI <basePath> <dataset> <scenario> <numYears> <modelVersion> <modelParamValue> <crfMin> <crfMax> <crfStep>
 * 
 * Example (Capacity Model):
 *   java -cp bin solver.RangeCrfMPSCLI /path/to/Datasets NZCA 150_sources 30 c 50 0.03 0.15 0.01
 *   Generates: cap_crf_0.03.mps, cap_crf_0.04.mps, ..., cap_crf_0.15.mps (capacity target fixed at 50 MtCO2/yr)
 * 
 * Example (Price Model):
 *   java -cp bin solver.RangeCrfMPSCLI /path/to/Datasets NZCA 150_sources 30 p 50 0.06 0.18 0.03
 *   Generates: price_crf_0.06.mps, price_crf_0.09.mps, price_crf_0.12.mps, price_crf_0.15.mps, price_crf_0.18.mps (CO2 price fixed at $50/tCO2)
 * 
 * Parameters:
 *   basePath         - Root path to Datasets directory
 *   dataset          - Dataset name (e.g., NZCA, CA_May2020)
 *   scenario         - Scenario name within dataset
 *   numYears         - Project length in years (e.g., 30)
 *   modelVersion     - "c" for capacity-constrained, "p" for price-sensitive
 *   modelParamValue  - Capacity target (MtCO2/yr) if c, or CO2 price ($/tCO2) if p (held constant)
 *   crfMin           - Minimum CRF (e.g., 0.06 = 6%)
 *   crfMax           - Maximum CRF (e.g., 0.18 = 18%)
 *   crfStep          - CRF increment (e.g., 0.03 = 3% steps)
 * 
 * Complementary tool: RangeParamMPSCLI (holds CRF fixed, varies capacity/price)
 */
public class RangeCrfMPSCLI {

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
            String modelVersion = args[4];
            double modelParamValue = Double.parseDouble(args[5]);
            double crfMin = Double.parseDouble(args[6]);
            double crfMax = Double.parseDouble(args[7]);
            double crfStep = Double.parseDouble(args[8]);

            // Validate inputs
            if (!modelVersion.equals("c") && !modelVersion.equals("p")) {
                System.err.println("Error: modelVersion must be 'c' or 'p'");
                System.exit(1);
            }

            if (crfMin < 0 || crfMax < 0 || crfStep <= 0) {
                System.err.println("Error: CRF values must be non-negative, step must be positive");
                System.exit(1);
            }

            if (crfMin > crfMax) {
                System.err.println("Error: crfMin cannot be greater than crfMax");
                System.exit(1);
            }

            // Create and run CRF range generator
            RangeCrfMPSGenerator generator = new RangeCrfMPSGenerator(basePath, dataset, scenario, numYears);
            generator.loadData();
            generator.generateMPSRange(crfMin, crfMax, crfStep, modelVersion, modelParamValue);

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
        System.out.println("CRF Sensitivity Analysis - MPS File Generator");
        System.out.println("=============================================");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -cp bin solver.RangeCrfMPSCLI <basePath> <dataset> <scenario> <numYears> <modelVersion> <modelParamValue> <crfMin> <crfMax> <crfStep>");
        System.out.println();
        System.out.println("Example (Capacity Model):");
        System.out.println("  java -cp bin solver.RangeCrfMPSCLI /path/to/Datasets NZCA 150_sources 30 c 50 0.03 0.15 0.01");
        System.out.println("  Varies CRF from 3% to 15% in 1% steps (capacity target = 50 MtCO2/yr)");
        System.out.println("  Generates: cap_crf_0.03.mps, cap_crf_0.04.mps, ..., cap_crf_0.15.mps");
        System.out.println();
        System.out.println("Example (Price Model):");
        System.out.println("  java -cp bin solver.RangeCrfMPSCLI /path/to/Datasets NZCA 150_sources 30 p 50 0.06 0.18 0.03");
        System.out.println("  Varies CRF from 6% to 18% in 3% steps (CO2 price = $50/tCO2)");
        System.out.println("  Generates: price_crf_0.06.mps, price_crf_0.09.mps, price_crf_0.12.mps, price_crf_0.15.mps, price_crf_0.18.mps");
        System.out.println();
        System.out.println("Parameters:");
        System.out.println("  basePath         Root path to Datasets directory");
        System.out.println("  dataset          Dataset name (e.g., NZCA, CA_May2020, SoutheastUS)");
        System.out.println("  scenario         Scenario name within dataset (e.g., 150_sources)");
        System.out.println("  numYears         Project length in years (e.g., 30)");
        System.out.println("  modelVersion     'c' for capacity-constrained, 'p' for price-sensitive");
        System.out.println("  modelParamValue  Capacity target (MtCO2/yr) if c, or CO2 price ($/tCO2) if p");
        System.out.println("  crfMin           Minimum CRF value (e.g., 0.06 = 6%)");
        System.out.println("  crfMax           Maximum CRF value (e.g., 0.18 = 18%)");
        System.out.println("  crfStep          CRF increment (e.g., 0.03 = 3% steps)");
        System.out.println();
        System.out.println("Related tools:");
        System.out.println("  RangeParamMPSCLI - Varies capacity/price while holding CRF fixed");
        System.out.println();
    }
}
