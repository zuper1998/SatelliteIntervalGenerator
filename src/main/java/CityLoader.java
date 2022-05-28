import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CityLoader {
    public static ArrayList<City> loadCities(String FileName){
        File f = new File(FileName);
        ArrayList<City> cities = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(f));
            String s;
            // Setup: LON|LAT|ALT|NAME
            while ((s = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(s, "|");
                cities.add(new City( Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()),st.nextToken()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return  cities;
    }
}
