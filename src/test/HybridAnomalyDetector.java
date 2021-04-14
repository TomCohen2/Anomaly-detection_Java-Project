package test;

import java.util.*;

public class HybridAnomalyDetector implements TimeSeriesAnomalyDetector{
    List<CorrelatedFeatures> corList;
    HashMap<Integer,float[]> zScoresMap;
    HashMap<Set<Integer>,CircleDuSoleil> welzlMap;
    @Override
    public void learnNormal(TimeSeries ts) {
        List<CorrelatedFeatures> corList = new ArrayList<>();
        List<Set<Integer>> welzlList = new ArrayList<>();
        int maxIndex=0;

        for (int i=0;i<ts.getNumOfFeatures();i++) //checking if any two features have correlation bigger than 0.95.
        {
            float maxCor =0.0f;
            for (int j=i+1;j<ts.getNumOfFeatures();j++) {
               float temp = Math.abs(StatLib.pearson(ts.data[i], ts.data[j]));
                if(maxCor < temp){
                    maxIndex =j;
                    maxCor = temp;
                }
            }
            if(maxCor>=0.95) {
                corList.add(new CorrelatedFeatures(ts.features[i], ts.features[maxIndex], maxCor,
                        StatLib.linear_reg(ts.getData()[i], ts.getData()[maxIndex]), 0.95f));
            }else if(maxCor<0.5){
                zScoresMap.put(i,StatLib.normalizationArr(ts.getData()[i]));
            }else{
                Point[] p = StatLib.arrToPoints(ts.getData()[i],ts.getData()[maxIndex]);
                List<Point> points = new ArrayList<>();

                for(Point s:p){
                    points.add(s);
                }
                CircleDuSoleil res = StatLib.makeCircle(points);
                Set<Integer> tmp = new HashSet<>();
                tmp.add(i);
                tmp.add(maxIndex);
                welzlMap.put(tmp,res);

            }
        }

    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {

        for (int i = 0; i < ts.numOfFeatures; i++) { //for each feature
            if (zScoresMap.containsKey(i)) {

            } else {
                for (Set s : welzlMap.keySet()) {
                    if (s.contains(i)) {

                    }
                }
            }
//        for(CorrelatedFeatures c:corList){
//            ts.getData()[c.feature1];
//        }

            List<AnomalyReport> stam = new ArrayList<>();
            return stam;
        }
        List<AnomalyReport> stam = new ArrayList<>();
        return stam;
    }
}
