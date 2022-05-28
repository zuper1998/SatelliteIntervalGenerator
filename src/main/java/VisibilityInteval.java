import org.orekit.time.AbsoluteDate;

import java.util.ArrayList;

public class VisibilityInteval {
    public String name;
    public AbsoluteDate start;
    public AbsoluteDate end;
    public ArrayList<Double> transmittance;
    VisibilityInteval(String name, AbsoluteDate start, AbsoluteDate end,ArrayList<Double> transmittance){
        this.name = name;
        this.start= start;
        this.end = end;
        this.transmittance = transmittance;
    }
    VisibilityInteval(){
        name="";
        transmittance=new ArrayList<Double>();
    }
}
