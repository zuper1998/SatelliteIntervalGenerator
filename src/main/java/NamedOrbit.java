import org.orekit.propagation.Propagator;

public class NamedOrbit {
    public String name;
    public Propagator propagator;
    NamedOrbit(String name,Propagator propagator){
        this.name = name;
        this.propagator = propagator;
    }

}
