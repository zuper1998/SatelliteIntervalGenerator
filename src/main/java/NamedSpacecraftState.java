import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;

public class NamedSpacecraftState {
    public String name;
    public SpacecraftState spacecraftState;
    NamedSpacecraftState(String name,SpacecraftState spacecraftState){
        this.name = name;
        this.spacecraftState = spacecraftState;
    }
}
