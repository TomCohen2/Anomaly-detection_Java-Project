package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HybridAnomalyDetector2 implements TimeSeriesAnomalyDetector{
	float HighCorrelation = 0.95f;
	float LowCorrelation = 0.5f;
	TimeSeries zsTS;
	List<CorrelatedFeatures> LR ,Welzl;
	SimpleAnomalyDetector lr;
	ZscoreAnomalyDetectorV2 zsAD;
	Map<CorrelatedFeatures,Circle> circleMap;
	Map<Integer,String> zScoreMap;
	Set<String> zScoreList;
	
	public HybridAnomalyDetector2() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HybridAnomalyDetector2(float highCorrelation, float lowCorrelation) {
		super();
		HighCorrelation = highCorrelation;
		LowCorrelation = lowCorrelation;
	}

	@Override
	public void learnNormal(TimeSeries ts) {
	;
		lr = new SimpleAnomalyDetector();
		zsAD = new ZscoreAnomalyDetectorV2();
		lr.setCorOffset(0);
		lr.learnNormal(ts);
		zScoreList = new HashSet<>();
		Welzl = new ArrayList<>();
		List<CorrelatedFeatures> corList = lr.getCorList();
		LR = new ArrayList<>();
		circleMap = new HashMap<>();
		zScoreMap = new HashMap<>();
		
		for (CorrelatedFeatures c : corList) {
			if(c.corrlation > HighCorrelation+1) { // Linear Regression
				LR.add(c);
				continue;
			}
			else if(c.corrlation < LowCorrelation) { // Z-Score
				zScoreList.add(c.feature1);
				continue;
			}
			else { // Weasle
				Welzl.add(c);
			}
		}
		TimeSeries zsTS = createZscoreTS(ts);
		
		//Z-Score learn normal
		zsAD.learnNormal(zsTS);

		//TODO: Hybrid Implementation
		for (CorrelatedFeatures f : Welzl) {
			System.out.println("TESTING");
			int feature1Index = StatLib.whichIndex(f.feature1,ts);
			int feature2Index = StatLib.whichIndex(f.feature2,ts);
			Point[] pointsArr = StatLib.arrToPoints(ts.getData()[feature1Index],ts.getData()[feature2Index]);
			Circle minCircle = StatLib.findMinCircle(pointsArr);
			circleMap.put(f, minCircle);
		}

	}

	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> totalReports = new ArrayList<>();
		totalReports.addAll(lr.detect(ts));
		
		TimeSeries zsTS = createZscoreTS(ts);
		
		totalReports.addAll(zsAD.detect(zsTS));
		
		//TODO: Hybrid Implementation
		for (CorrelatedFeatures f : Welzl) {
			for(int i=0;i<ts.getNumOfRows();i++) {
				int xIndex = StatLib.whichIndex(f.feature1,ts);
				int yIndex = StatLib.whichIndex(f.feature2,ts);
				Circle circle = circleMap.get(f);
				Point point = new Point(ts.getData()[i][xIndex],ts.getData()[i][yIndex]);
				if(!circle.contains(point)) {
					totalReports.add(new AnomalyReport("Timestep " + i + ": Hybrid detected anomaly point" + point,i));
				}
			}
			
		}
		return totalReports;

	}
	
	private TimeSeries createZscoreTS(TimeSeries ts){
		zsTS = new TimeSeries();
		String[] zScoresFeatures = new String[zScoreList.size()];
		float [][] zScoresData = new float[zScoreList.size()][ts.getNumOfRows()];
		int zScoresIndex = 0;
		for (String feature : zScoreList) { // Creating zscore data matrix
			for (int i=0;i<ts.getNumOfRows();i++) {
				int j = StatLib.whichIndex(feature,ts);
				;
				zScoresData[zScoresIndex][i] = ts.getData()[j][i];
			}
			zScoresFeatures[zScoresIndex] = feature;
			//zScoreMap.put(zScoresIndex++, feature);

		}
		//Initializing Z-Scores TimeSeries
		zsTS.setData(zScoresData);
		zsTS.setFeatures(zScoresFeatures);
		zsTS.setNumOfFeatures(zScoresFeatures.length);
		zsTS.setNumOfRows(ts.getNumOfRows());
		
		return zsTS;
	}

	@Override
	public GraphStruct display(String colName) {
		// TODO Auto-generated method stub
		return null;
	}

}
