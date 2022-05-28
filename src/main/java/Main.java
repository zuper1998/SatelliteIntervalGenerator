import java.io.File;

public class Main {

    public static void main(String[] args) {
        SatOrbitPropagation.loadDefaultValues();
        File dir = new File(("src/main/Data/In"));
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                System.out.println(child.getName());
                if (SimValues.IsSim) {
                    SimValues.calc.set(new QuantumBitTransmittanceCalculator());
                    SatOrbitPropagation.Generate(child);
                    return;
                }
            }
        }

    }
}
