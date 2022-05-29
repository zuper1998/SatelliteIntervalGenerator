import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class IntervalGraph {
    HashMap<String, Vector<VisibilityInteval>> nodes = new HashMap<String, Vector<VisibilityInteval>>();


    public IntervalGraph(Vector<VisibilityInteval>[][] visCityIntMatrix, Vector<VisibilityInteval>[][] visSatIntMatrix) {
        //Add all city intervals
        addAllCity(visCityIntMatrix);
        addAllCity(visSatIntMatrix);

    }

    public void Print(String filename){
        FileWriter file = null;
        try {
            if(SimValues.cityData != "src/main/Data/cities.txt"){
                filename = "Test_"+filename;
            }
            file = new FileWriter( String.format("%s.satNetwork",filename ) );
            System.out.printf("Saving to file: %s.satNetwork",filename);
            BufferedWriter writer = new BufferedWriter(file);
            for(HashMap.Entry<String,Vector<VisibilityInteval>> entry : nodes.entrySet()){
                printNode(writer, entry);
            }


            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printNode(BufferedWriter writer, Map.Entry<String, Vector<VisibilityInteval>> entry) throws IOException {
        String start = entry.getKey();
        writer.write( String.format("%s|%d%n",start, entry.getValue().size()));
        for(VisibilityInteval visibilityInteval : entry.getValue()){
            printVisInt(writer, visibilityInteval);

        }
    }

    private void printVisInt(BufferedWriter writer, VisibilityInteval visibilityInteval) throws IOException {
        String end = visibilityInteval.name.split("->")[1];
        StringBuilder transmittance_values = new StringBuilder();
        int i = 0;
        for(double t : visibilityInteval.transmittance){
            transmittance_values.append(t);
            if(++i< visibilityInteval.transmittance.size())
                transmittance_values.append(",");
        }
        writer.write( String.format("%s|%s|%f|%f%n",transmittance_values,end, visibilityInteval.start.durationFrom(SimValues.initialDate), visibilityInteval.end.durationFrom(SimValues.initialDate)) );
    }

    private void addAllCity(Vector<VisibilityInteval>[][] visCityIntMatrix) {
        for (Vector<VisibilityInteval>[] cityIntMatrix : visCityIntMatrix) {
            for (Vector<VisibilityInteval> intMatrix : cityIntMatrix) {
                for (VisibilityInteval current : intMatrix) {
                    addInterval(current);
                }
            }
        }
    }

    private void addInterval(VisibilityInteval current) {
        String start = current.name.split("->")[0];

        nodes.putIfAbsent(start,new Vector<>());

        nodes.get(start).add(current);
    }
}
