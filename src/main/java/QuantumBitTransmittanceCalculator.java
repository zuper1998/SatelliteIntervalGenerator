import accessories.DefaultValues;
import enums.Season;
import enums.Weather;
import protocols.BB84;

import java.io.Serial;
import java.io.Serializable;

public class QuantumBitTransmittanceCalculator implements Serializable {
    BB84 calc = new BB84();
    @Serial
    private static final long serialVersionUID = 1L;

    public QuantumBitTransmittanceCalculator(){
        //DefaultValues.absorptionAndScatteringPath = "C:\\Users\\Narcano\\IdeaProjects\\OrekitTest\\src\\com\\company\\accessories\\asv_860.csv";
        DefaultValues.absorptionAndScatteringPath ="src/main/Data/asv_860.csv";
        //DefaultValues.groundSpaceChannelLength = 0; // Should be in KMs IDK what it does


        DefaultValues.quantumEfficiencyOfDetector = 0.7;  //WARNING
        DefaultValues.season = Season.summer;
        DefaultValues.weather = Weather.clear;
        calc.setEfficiencyOfQuantumOperationsByBob(DefaultValues.efficiencyOfQuantumOperationsByBob);
        calc.setFrequencyOfLaserFiring(DefaultValues.frequencyOfLaserFiring);
        calc.setProbabilityOfPolarizationMeasurementError(DefaultValues.probabilityOfPolarizationMeasurementError);
        calc.setTotalNoise(DefaultValues.totalNoise);
        calc.setNumberOfDetectors(DefaultValues.numberOfDetectors);
        calc.setMeanPhotonNumberOfSignal(DefaultValues.meanPhotonNumberOfSignal);
        calc.setQuantumEfficiencyOfDetector(DefaultValues.quantumEfficiencyOfDetector);
        calc.setWaveLength(DefaultValues.waveLength);
        calc.setZenithAngle(DefaultValues.zenithAngle);
        calc.setWindSpeed(DefaultValues.windSpeed);
        calc.setApertureDiameter(DefaultValues.apertureDiameter);
        calc.setMirrorDiameter(DefaultValues.mirrorDiameter);
        calc.setTargetingAngularError(DefaultValues.targetingAngularError);
        calc.setHeightAboveSeaLevel(DefaultValues.heightAboveSeaLevel);
        calc.setWeather(DefaultValues.weather);
        calc.setClimate(DefaultValues.climate);
        calc.setSeason(DefaultValues.season);

        try {
            calc.readFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public double  calculateTransmitanceCity(double elevation,double height_above_sea,int dir){
        calc.setDirection(dir);

        double zenithAngle = 90-elevation;
        double heightAboveSeaLevel = height_above_sea/1000;

        calc.setZenithAngle(zenithAngle);
        calc.setHeightAboveSeaLevel(heightAboveSeaLevel);

        calc.setOpticalDistance(heightAboveSeaLevel, zenithAngle);
        calc.setQber(); // if distance over sea is above 2000 it gets funky xd

        return calc.getTransmittance();
    }


    //Since its sat->sat it doesn't need a lot of parameters
    public double  calculateTransmitanceSat(double distance){
        calc.setDirection(1);

        calc.setSpaceSpaceChannelLength(distance/1000);

        calc.setQber(); // if distance over sea is above 2000 it gets funky xd

       return calc.getTransmittance();
    }
}
