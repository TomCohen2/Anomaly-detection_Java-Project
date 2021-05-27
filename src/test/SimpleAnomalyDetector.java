package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	private List<CorrelatedFeatures> corList;	
	private float corOffset=0.9f;
	private static float threshOffsetMul=0.5f;
	
	public float getCorOffset() {
		return corOffset;
	}


	public List<CorrelatedFeatures> getCorList() {
		return corList;
	}


	public void setCorList(List<CorrelatedFeatures> corList) {
		this.corList = corList;
	}


	public void setCorOffset(float corOffset) {
		this.corOffset = corOffset;
	}


	@Override
	public void learnNormal(TimeSeries ts) { 
		float[] maxArr = new float[ts.getNumOfFeatures()];
		int maxIndex=0;

		for (int i=0;i<ts.getNumOfFeatures();i++) //checking if any two features have correlation.
		{
		//	for (int k=0; k<maxArr.length;k++) // initializing maxArr
			//	maxArr[k]=0;
			for (int j=i+1;j<ts.getNumOfFeatures();j++)
			{
					if(corList == null)
						corList = new ArrayList<CorrelatedFeatures>();
					maxArr[j] = Math.abs(StatLib.pearson(ts.data[i], ts.data[j]));
			}
			maxIndex = StatLib.maxAt(maxArr);
			Point[] points = StatLib.arrToPoints(ts.data[i], ts.data[maxIndex]);
			Line l = StatLib.linear_reg(ts.data[i],ts.data[maxIndex]);
			float[] devArr = StatLib.devArr(points, l);
			float maxDev = StatLib.findMax(devArr);
			
			if (maxArr[maxIndex]>=corOffset)
				corList.add(new CorrelatedFeatures(ts.features[i],ts.features[maxIndex],maxArr[maxIndex],l,maxDev));
		}
	}

	
	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> alarms = null;
		
		for(CorrelatedFeatures c : corList )
		{ 
			float threshOffset = c.threshold*threshOffsetMul;
			String str = c.feature1 + "<->" + c.feature2;
			Point [] points = StatLib.arrToPoints(ts.data[StatLib.whichIndex(c.feature1,ts)], ts.data[StatLib.whichIndex(c.feature2,ts)]);
			for(int i=0;i<points.length;i++)
			{
				if (StatLib.dev(points[i], c.lin_reg)>c.threshold+threshOffset)
				{
					if(alarms == null) 
						alarms = new ArrayList<AnomalyReport>();
					alarms.add(new AnomalyReport("LR " + str, i+1));
				}
			}
			
		}
		return alarms;
	}
	
	
	
	public List<CorrelatedFeatures> getNormalModel(){
		return corList;
	}
}
