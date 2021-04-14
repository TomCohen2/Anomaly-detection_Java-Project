package test;

import java.util.ArrayList;
import java.util.List;

public class ZscoreAnomalyDetectorV2 implements TimeSeriesAnomalyDetector{
    public float[] standardDevArr;
    public float[] txArr;

    float[][] TXtest;
    float[][] zScores;
    float[][] means;
    float[][] stddev;
    float[][] testMeans;
    float[][] testStddev;
    float[][] testzScores;

    @Override
    public void learnNormal(TimeSeries ts) {
        standardDevArr = new float[ts.numOfFeatures];
        txArr = new float[ts.numOfFeatures];

        for(int i=0;i<ts.numOfFeatures;i++) {
            standardDevArr[i] = StatLib.standardDeviation(ts.getData()[i]);
            txArr[i] = StatLib.getMax(StatLib.normalizationArr(ts.getData()[i]));
        }
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        ArrayList<AnomalyReport> res = new ArrayList<>();
        float[] sumArrForAvg = new float[ts.numOfFeatures];
        for(int i=0;i< ts.numOfRows;i++){
            for(int j=0;j< ts.numOfFeatures;j++){
                sumArrForAvg[j] += ts.getData()[i][j];
                if(txArr[j]<StatLib.normalization(ts.getData()[i][j],sumArrForAvg[j],standardDevArr[j])){
                    res.add(new AnomalyReport(ts.features[j],i+1));
                }
            }
        }
        return res;
    }
    public float[][] getTXtest() {
        return TXtest;
    }

    public float[][] getzScores() {
        return zScores;
    }

    public float[][] getTestMeans() {
        return testMeans;
    }

    public float[][] getTestStddev() {
        return testStddev;
    }

    public float[][] getTestzScores() {
        return testzScores;
    }

    public float[][] getMeans() {
        return means;
    }

    public float[][] getStddev() {
        return stddev;
    }
}