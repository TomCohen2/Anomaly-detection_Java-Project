package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import test.TimeSeriesAnomalyDetector.GraphStruct;

public class HybridAnomalyDetector implements TimeSeriesAnomalyDetector{
    ZscoreAnomalyDetector zad;
    SimpleAnomalyDetector reg;
    TimeSeries wezly;
    List<Circle>welzl;
    Map<String,String> featureMap;
    Map<String,Circle> enclosingCircleMap;
    Map<String,String> whichAlgoToDisplay;
    int[] z_index;
    int[] simp_index;
    int[] welzl_index;
    Circle minCircle;
    
    @Override
    public void learnNormal(TimeSeries ts) {
        reg = new SimpleAnomalyDetector();
        zad = new ZscoreAnomalyDetector();
        float[][] data = ts.getData();
        String[] names = ts.getFeatures();
        int num_of_features = ts.getNumOfFeatures();
        TimeSeries simp = new TimeSeries();
        TimeSeries z = new TimeSeries();
        wezly = new TimeSeries();
        featureMap = new HashMap<>();
        enclosingCircleMap = new HashMap<>();
        whichAlgoToDisplay = new HashMap<>();
        
        for (int i = 0;i < num_of_features;i++) //checking if any two features have correlation.
        {
            float max = -1;
            int maxIndex = 0;
            for (int j = i+1;j < num_of_features;j++)
            {
                float pearson = Math.abs(StatLib.pearson(data[i],data[j]));
                if(pearson > max){
                    max = pearson;
                    maxIndex = j;
                }
            }
//            if (max >= 0.95f){
//                if(simp_index == null)
//                    simp_index = new int[2];
//                else simp_index = Arrays.copyOf(simp_index,simp_index.length + 2);
//                simp_index[simp_index.length - 2] = i;
//                simp_index[simp_index.length - 1] = maxIndex;
//                whichAlgoToDisplay.put(names[i], "LR");
//                
//             //   simp.addFeature(data[i],names[i]);
//            //    simp.addFeature(data[maxIndex],names[maxIndex]);
//                
//                  Point[] points = StatLib.arrToPoints(data[i], data[maxIndex]);
//                  Line l = StatLib.linear_reg(data[i],data[maxIndex]);
//                  float[] devArr = reg.devArr(points, l);
//                  float maxDev = reg.findMax(devArr);
//
//                  reg.addCor(names[i],names[maxIndex],max,l,maxDev);
//          }      
            if(max<0.5){
                if (z_index == null)
                    z_index = new int[1];
                else z_index = Arrays.copyOf(z_index,z_index.length + 1);
                z_index[z_index.length - 1] = i;
                z.addFeature(data[i],names[i]);
                whichAlgoToDisplay.put(names[i],"Z");
            }else {
                if(welzl_index == null)
                    welzl_index = new int[2];
                else welzl_index = Arrays.copyOf(welzl_index,welzl_index.length + 2);
                welzl_index[welzl_index.length - 2] = i;
                welzl_index[welzl_index.length - 1] = maxIndex;
                wezly.addFeature(data[i],names[i]);
                wezly.addFeature(data[maxIndex],names[maxIndex]);
                //System.out.println("TESTTTTTTTTTTT" + names[i] + " " + names[maxIndex]);
                whichAlgoToDisplay.put(names[i], "WELZL");
                featureMap.put(names[i], names[maxIndex]);
            }
        }
    //  if(simp.numOfFeatures>0)
    //      reg.learnNormal(simp);
        if(z.numOfFeatures>0)
            zad.learnNormal(z);
        if(wezly.numOfFeatures>0)
            welzl=welzlLearner(wezly);
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        String[] names = ts.getFeatures();
        float[][] data = ts.getData();
        List<AnomalyReport> anomalyReports = new ArrayList<>();
        if(simp_index!=null) {
            TimeSeries simp = new TimeSeries();
            for (int i : simp_index)
                simp.addFeature(data[i],names[i]);
            anomalyReports.addAll(reg.detect(simp));
        }
        if(z_index!=null) {
            TimeSeries z = new TimeSeries();
            for(int i : z_index)
                z.addFeature(data[i],names[i]);
            anomalyReports.addAll(zad.detect(z));
        }
        if(welzl_index!=null) {
            TimeSeries welzl_ts = new TimeSeries();
            for(int i : welzl_index)
                welzl_ts.addFeature(data[i],names[i]);
            anomalyReports.addAll(welzlDetecter(welzl_ts));
        }
        return anomalyReports;
    }

    private List<Circle> welzlLearner(TimeSeries ts){
        List<Circle>welzlCircle=new ArrayList<>();
        float[][] data = ts.getData();
        int num_of_features = ts.getNumOfFeatures();
        for(int i=0 ; i< num_of_features-1;i += 2){ // why -1?
            //List<Point> points= new ArrayList<>();
            //points.addAll(Arrays.asList(StatLib.arrToPoints(data[i],data[i+1])));
            List<Point> points=StatLib.arrToList_of_Points(data[i],data[i+1]);
            welzlCircle.add(StatLib.makeCircle(points));
           // System.out.println("YANIVVVV" + StatLib.makeCircle(points));
            enclosingCircleMap.put(ts.getFeatures()[i], StatLib.makeCircle(points));
            //System.out.println(ts.getFeatures()[i]);
        }
        return welzlCircle;
    }
    private List<AnomalyReport> welzlDetecter(TimeSeries ts){
        List<AnomalyReport> anomalyReports = new ArrayList<>();
        String[] names = ts.getFeatures();
        float[][] data = ts.getData();
        int num_of_features = ts.getNumOfFeatures();
        int num_of_rows = ts.getNumOfRows();
        int circle = 0;//is_inside(welzl.get(circle),p)
        for(int i = 0;i < num_of_features - 1;i += 2){
            String str ="welzl: " + names[i] + "-" + names[i + 1];
            for(int j = 0;j < num_of_rows;j++){
                Point p = new Point(data[i][j],data[i + 1][j]);
                if(!StatLib.is_inside(welzl.get(circle), p));
                	
                    anomalyReports.add(new AnomalyReport(str,j + 1));
            }
            circle++;
        }
        return anomalyReports;
    }

	@Override
	public GraphStruct display(String colName) {
		for(Map.Entry<String, String> entry : whichAlgoToDisplay.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    //System.out.println("Printing whichAlgoToDisplay map: \n\n");
		    //System.out.println(key + " "+ value);
		}
		GraphStruct ret = new GraphStruct();
		switch(whichAlgoToDisplay.get(colName)) {
		case "LR":
			ret = reg.display(colName);
			break;
		case "Z":
			ret = zad.display(colName);
			break;
		case "WELZL":
			ret.setStr("HYB,"+colName+","+featureMap.get(colName));
			ret.setC(enclosingCircleMap.get(colName));
			//System.out.println("In HYB Display, circle = " + ret.getC() + " enclo = " + enclosingCircleMap.get(colName) + " STR = " + ret.getStr());						
			break;
			}
		
		return ret;
	}

	private void findMinCircle(Circle a) {
		if (a.getRadius()<minCircle.getRadius())
			minCircle = new Circle(a.getCenter(),a.getRadius());
	}
}
