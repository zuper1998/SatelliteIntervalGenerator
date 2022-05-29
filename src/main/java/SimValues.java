import org.orekit.time.AbsoluteDate;

import java.util.ArrayList;

public class SimValues {
    public static final double stepT = 1;
    public static final double duration = 3600 *3 ;
    public static final double entangledPhotonDetectionRateHz = 3.51*1000; //https://www.nature.com/articles/s41534-021-00462-7
    public static final String satData = "src/main/Data/QSAT_RETRO_LOWMID2.txt";
    public static final String cityData = "src/main/Data/cities.txt";
    public static ThreadLocal<QuantumBitTransmittanceCalculator> calc = new ThreadLocal<>();
    public static final ArrayList<City> cities = new ArrayList<>(CityLoader.loadCities(SimValues.cityData));
    public static final double minAngle = 20;
    public static boolean IsSim = true;
    public static AbsoluteDate initialDate;

    public static int minSatElevation=500;
}
