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
	
	public void addCor(String feature1 , String feature2 , float cor , Line l , float threshold){
		if(corList == null)
			corList = new ArrayList<CorrelatedFeatures>();

		corList.add(new CorrelatedFeatures(feature1,feature2,cor,l,threshold));
	}
	
	
	public List<CorrelatedFeatures> getNormalModel(){
		return corList;
	}
	
	
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
	
	
	
	@Override
	public GraphStruct display(String colName) {
		GraphStruct ret = null;
		for (CorrelatedFeatures cf : corList) {
			if (cf.feature1.equals(colName) || cf.feature2.equals(colName)) {
				ret = new GraphStruct();
				ret.setL(cf.lin_reg);
				ret.setStr("LR,"+cf.feature1 + "," + cf.feature2);
			}
		}
		//System.out.println("TESTING" + ret.getStr());
		return ret;
		
	}
}
