import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hipparchus.analysis.differentiation.UnivariateDerivative1;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.hipparchus.util.FastMath;
import org.hipparchus.util.MathUtils;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.InertialProvider;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.NewtonianAttraction;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.ICGEMFormatReader;
import org.orekit.forces.maneuvers.Maneuver;
import org.orekit.forces.maneuvers.propulsion.BasicConstantThrustPropulsionModel;
import org.orekit.forces.maneuvers.propulsion.PropulsionModel;
import org.orekit.forces.maneuvers.trigger.DateBasedManeuverTriggers;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.AdditionalStateProvider;
import org.orekit.propagation.MatricesHarvester;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.PropagatorsParallelizer;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.propagation.sampling.MultiSatStepHandler;
import org.orekit.propagation.sampling.OrekitStepInterpolator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateComponents;
import org.orekit.time.TimeComponents;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import java.util.List;

public class StepHandler implements MultiSatStepHandler {

    boolean forward;
    double samplingStep;
    AbsoluteDate next;
    @Override
    public void init(List<SpacecraftState> states0, AbsoluteDate t) {
        MultiSatStepHandler.super.init(states0, t);
        final AbsoluteDate t0 = states0.get(0).getDate();
        if (t.isAfterOrEqualTo(t0)) {
            forward = true;
            next    = t0.shiftedBy(samplingStep);
        } else {
            forward = false;
            next    = t0.shiftedBy(-samplingStep);
        }

    }

    @Override
    public void handleStep(List<OrekitStepInterpolator> interpolators) {

        final AbsoluteDate previousDate = interpolators.get(0).getPreviousState().getDate();
        final AbsoluteDate currentDate  = interpolators.get(0).getCurrentState().getDate();
        while ( forward && (next.isAfter(previousDate)  && next.isBeforeOrEqualTo(currentDate)) ||
                !forward && (next.isBefore(previousDate) && next.isAfterOrEqualTo(currentDate))) {


            next = next.shiftedBy(forward ? samplingStep : -samplingStep);
        }
    }

    @Override
    public void finish(List<SpacecraftState> finalStates) {
        MultiSatStepHandler.super.finish(finalStates);
    }




}

