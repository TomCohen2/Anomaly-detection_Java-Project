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


	public void setCorOffset(float corOffset) {
		this.corOffset = corOffset;
	}


	@Override
	public void learnNormal(TimeSeries ts) { 
		float[] maxArr = new float[ts.getNumOfFeatures()];
		int maxIndex=0;

		for (int i=0;i<ts.getNumOfFeatures();i++) //checking if any two features have correlation.
		{
			for (int k=0; k<maxArr.length;k++) // initializing maxArr
				maxArr[k]=0;
			for (int j=i+1;j<ts.getNumOfFeatures();j++)
			{
					if(corList == null)
						corList = new ArrayList<CorrelatedFeatures>();
					maxArr[j] = Math.abs(StatLib.pearson(ts.data[i], ts.data[j]));
			}
			maxIndex = maxAt(maxArr);
			Point[] points = StatLib.arrToPoints(ts.data[i], ts.data[maxIndex]);
			Line l = StatLib.linear_reg(ts.data[i],ts.data[maxIndex]);
			float[] devArr = devArr(points, l);
			float maxDev = findMax(devArr);
			
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
			String str = c.feature1 + "-" + c.feature2;
			Point [] points = StatLib.arrToPoints(ts.data[whichIndex(c.feature1,ts)], ts.data[whichIndex(c.feature2,ts)]);
			for(int i=0;i<points.length;i++)
			{
				if (StatLib.dev(points[i], c.lin_reg)>c.threshold+threshOffset)
				{
					if(alarms == null) 
						alarms = new ArrayList<AnomalyReport>();
					alarms.add(new AnomalyReport(str, i+1));
				}
			}
			
		}
		return alarms;
	}
	
	
	//Returns max value in array
	public float findMax(float[] arr)
	{
		float[] temp = arr.clone();
		Arrays.sort(temp);
		return temp[arr.length-1];
	}
	
	
	//Returns an array of dev value of each point to a line.
	public float[] devArr(Point[] points, Line l)
	{
		float[] arr = new float[points.length];
		for(int i=0;i<points.length;i++)
			arr[i] = StatLib.dev(points[i], l);
		return arr;
	}
	
	//Returns the index of the maximum value of an array.
	public int maxAt(float[] array){
		int maxAt = 0;
		for (int i = 0; i < array.length; i++) 
		    maxAt = array[i] > array[maxAt] ? i : maxAt;
		  return maxAt;
		}
	
	
	//Returns the index of feature s in TimeSeries ts.
	private int whichIndex(String s, TimeSeries ts) {
				for (int i=0;i<ts.getNumOfFeatures();i++) {
				    if (ts.features[i].equals(s))
				        return i;
				    }
				return -1;
	}
	
	public List<CorrelatedFeatures> getNormalModel(){
		return corList;
	}
}
