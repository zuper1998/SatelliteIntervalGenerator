import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.orbits.EquinoctialOrbit;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.PropagatorsParallelizer;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.InterSatDirectViewDetector;
import org.orekit.propagation.sampling.MultiSatStepHandler;
import org.orekit.propagation.sampling.OrekitStepInterpolator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;


public class SatOrbitPropagation {
    public static void loadDefaultValues() {
        final File home = new File(System.getProperty("user.home"));
        final File orekitData = new File(home, "orekit-data");
        if (!orekitData.exists()) {
            System.err.format(Locale.US, "Failed to find %s folder%n",
                    orekitData.getAbsolutePath());
            System.err.format(Locale.US, "You need to download %s from %s, unzip it in %s and rename it 'orekit-data' for this tutorial to work%n",
                    "orekit-data-master.zip", "https://gitlab.orekit.org/orekit/orekit-data/-/archive/master/orekit-data-master.zip",
                    home.getAbsolutePath());
            System.exit(1);
        }
        final DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
        manager.addProvider(new DirectoryCrawler(orekitData));
        SimValues.initialDate = new AbsoluteDate(2021, 1, 1, 23, 30, 00.000, TimeScalesFactory.getUTC());
    }

    public static void Generate(File satData) {
        ArrayList<SatelliteLoader> sats = SatelliteLoader.SatLoader(
                satData.getAbsolutePath());
        ArrayList<City> cities = SimValues.cities;
        ArrayList<TopocentricFrame> cityFrames = getFrameFromCities(cities);
        //Get referenc frame J200 --- Earth centered frame
        final Frame inertialFrame = FramesFactory.getEME2000();

        ArrayList<NamedOrbit> orbits = new ArrayList<>();

        for (SatelliteLoader satellite : sats) {
            final Orbit initialOrbitE = new KeplerianOrbit(satellite.a, satellite.e, satellite.i, satellite.omega, satellite.raan, satellite.lM, PositionAngle.MEAN,
                    inertialFrame, SimValues.initialDate, SatelliteLoader.mu);
            final Orbit initialOrbit = new EquinoctialOrbit(initialOrbitE);

            // Propagator : consider a simple Keplerian motion (could be more elaborate)
            orbits.add(new NamedOrbit(satellite.Name, new KeplerianPropagator(initialOrbit)));
        }



        propagate(orbits,cityFrames,SimValues.initialDate,SimValues.initialDate.shiftedBy(3600),1);








    }

    private static ArrayList<TopocentricFrame> getFrameFromCities(ArrayList<City> cities) {
        final Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
        final BodyShape earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING,
                earthFrame);
        ArrayList<TopocentricFrame> ret = new ArrayList<>();
        for (City c : cities) {
            final GeodeticPoint station1 = new GeodeticPoint(c.latitude, c.longitude, c.altitude);
            final TopocentricFrame sta1Frame = new TopocentricFrame(earth, station1, c.getName());
            ret.add(sta1Frame);


        }


        return ret;
    }


    public static void propagate(ArrayList<NamedOrbit> propagators, ArrayList<TopocentricFrame>  cities, AbsoluteDate start, AbsoluteDate end, double step){
        ArrayList<NamedSpacecraftState> namedSpacecraftStates= new ArrayList<>();
        int CityMatrixWidth = cities.size() * 2;
        //Since def value is 0 it will all be "false";
        boolean[][] visSatMatrix  = new boolean[propagators.size()][propagators.size()];
        boolean[][] visCityMatrix  = new boolean[cities.size()][propagators.size()];

        Vector<VisibilityInteval>[][] visSatIntMatrix = new Vector[propagators.size()][propagators.size()];

        // Double the size of city so it can go both ways
        Vector<VisibilityInteval>[][] visCityIntMatrix = new Vector[CityMatrixWidth][propagators.size()];
        for ( int x =0;x<propagators.size();x++){
            for ( int y =0;y<propagators.size();y++) {
                visSatIntMatrix[x][y] = new Vector<>();
            }
        }
        for (int x = 0; x< CityMatrixWidth; x++){
            for ( int y =0;y<propagators.size();y++) {
                visCityIntMatrix[x][y] = new Vector<>();
            }
        }
        for (AbsoluteDate extrapDate = start;
             extrapDate.isBeforeOrEqualTo(end);
             extrapDate = extrapDate.shiftedBy(step)) {



            double percent = extrapDate.durationFrom(start)/end.durationFrom(start);
            System.out.println("\r"+DisplayBar(percent) + "  "+percent+"% Done");

            for (NamedOrbit namedOrbit : propagators){
                namedSpacecraftStates.add(new NamedSpacecraftState(namedOrbit.name, namedOrbit.propagator.propagate(extrapDate)));
            }

            //Compute City Sat visibility
            CitySatVisCompute(cities, step, namedSpacecraftStates, visCityMatrix, visCityIntMatrix, extrapDate);


            //Compute interSat Visibility edge
            InterSatVisCompute(step, namedSpacecraftStates, visSatMatrix, visSatIntMatrix, extrapDate);


            namedSpacecraftStates.clear();
        }
        //TODO: return something that can be printed

    }
    static String DisplayBar(double i)
    {
        StringBuilder sb = new StringBuilder();

        double x = i / 2;
        sb.append("|");
        for (double k = 0; k < 50; k++)
            sb.append(((x <= k) ? " " : "="));
        sb.append("|");

        return sb.toString();
    }
    private static void CitySatVisCompute(ArrayList<TopocentricFrame> cities, double step, ArrayList<NamedSpacecraftState> namedSpacecraftStates, boolean[][] visCityMatrix, Vector<VisibilityInteval>[][] visCityIntMatrix, AbsoluteDate extrapDate) {
        int x = 0;
        for(TopocentricFrame city : cities){
            int y=0;
            for(NamedSpacecraftState inner : namedSpacecraftStates) {
                double degree = FastMath.toDegrees(city.getElevation(inner.spacecraftState.getPVCoordinates().getPosition(), inner.spacecraftState.getFrame(), inner.spacecraftState.getDate()));
                if(degree>SimValues.minSatElevation) {
                    String name = String.format("%s->%s", city.getName(), inner.name);
                    String name_backwards = String.format("%s->%s", inner.name, city.getName());

                    //Was it visible before
                    if (!visCityMatrix[x][y]) {
                        // -->
                        visCityIntMatrix[x][y].add(new VisibilityInteval(name, extrapDate, extrapDate, new ArrayList<>()));
                        // <--
                        visCityIntMatrix[x + cities.size()][y].add(new VisibilityInteval(name_backwards, extrapDate, extrapDate, new ArrayList<>()));
                        visCityMatrix[x][y] = true;
                    }
                    double distance = city.getRange(inner.spacecraftState.getPVCoordinates().getPosition(), inner.spacecraftState.getFrame(), inner.spacecraftState.getDate());
                    double heightAboveSea = distance * FastMath.sin(FastMath.toRadians(degree));

                    // -->
                    visCityIntMatrix[x][y].lastElement().transmittance.add(SimValues.calc.get().calculateTransmitanceCity(degree, heightAboveSea, 0) * step);
                    // <--
                    visCityIntMatrix[x + cities.size()][y].lastElement().transmittance.add(SimValues.calc.get().calculateTransmitanceCity(degree, heightAboveSea, 2) * step);
                } else if (visCityMatrix[x][y]){
                    visCityMatrix[x][y] = false;
                    // -->
                    visCityIntMatrix[x][y].lastElement().end = extrapDate;
                    // <--
                    visCityIntMatrix[x + cities.size()][y].lastElement().end = extrapDate;
                }
                y++;
            }
            x++;
        }
    }

    private static void InterSatVisCompute(double step, ArrayList<NamedSpacecraftState> namedSpacecraftStates, boolean[][] visMatrix, Vector<VisibilityInteval>[][] visIntMatrix, AbsoluteDate extrapDate) {
        int x = 0;
        for(NamedSpacecraftState outer : namedSpacecraftStates){
            int y=0;
            for(NamedSpacecraftState inner : namedSpacecraftStates){
                if(outer==inner) continue;
                if(checkInterSatVis(inner.spacecraftState,outer.spacecraftState)){
                    //Visible
                    String name = String.format("%s->%s",outer.name,inner.name);
                    //Was it visible before
                    if(!visMatrix[x][y]){
                        visIntMatrix[x][y].add(new VisibilityInteval(name, extrapDate, extrapDate,new ArrayList<>()));
                        visMatrix[x][y] = true;
                    }
                    Vector3D posOuter = outer.spacecraftState.getPVCoordinates().getPosition();
                    Vector3D posInner = inner.spacecraftState.getPVCoordinates().getPosition();
                    double distance = posInner.distance(posOuter);
                    visIntMatrix[x][y].lastElement().transmittance.add(SimValues.calc.get().calculateTransmitanceSat(distance) * step);

                } else {
                    //Not visible
                    if(visMatrix[x][y]){
                        visIntMatrix[x][y].lastElement().end = extrapDate.shiftedBy(-step);
                        visMatrix[x][y] = false;
                    }


                }

                y++;
            }
            x++;
        }
    }

    public static boolean checkInterSatVis(SpacecraftState inner, SpacecraftState outer){
        //Position in J2000 in meters
        Vector3D pos_outer = outer.getOrbit().getPVCoordinates().getPosition();
        Vector3D pos_inner = inner.getOrbit().getPVCoordinates().getPosition();
        Vector3D earthCore = new Vector3D(0, 0, 0);
        Vector3D closestP = getClosestP(pos_inner, pos_outer, earthCore);

        double tmp = closestP.distance(earthCore);

        return tmp > ((6371 + SimValues.minSatElevation) * 1000);
    }
    //https://math.stackexchange.com/questions/2193720/find-a-point-on-a-line-segment-which-is-the-closest-to-other-point-not-on-the-li
    private static Vector3D getClosestP(Vector3D A, Vector3D B, Vector3D P) {
        Vector3D v = B.subtract(A);
        Vector3D u = A.subtract(P);
        double t = -1 * (v.dotProduct(u) / v.dotProduct(v));
        if (t < 0 || 1 < t) {
            t = 0;
            Vector3D a1 = A.scalarMultiply(1 - t).add(B.scalarMultiply(t)).subtract(P); // (1−t)A+tB−P
            double g1 = FastMath.sqrt(a1.getX() * a1.getX() + a1.getY() * a1.getY() + a1.getZ() * a1.getZ());
            t = 1;
            Vector3D a2 = A.scalarMultiply(1 - t).add(B.scalarMultiply(t)).subtract(P); // (1−t)A+tB−P
            double g2 = FastMath.sqrt(a2.getX() * a2.getX() + a2.getY() * a2.getY() + a2.getZ() * a2.getZ());
            return g1 < g2 ? A : B;

        }
        return A.scalarMultiply(1 - t).add(B.scalarMultiply(t)); //(1−t)A+tB
    }

}
