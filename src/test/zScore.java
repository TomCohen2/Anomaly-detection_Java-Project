package test;

import java.util.ArrayList;
import java.util.List;

public class zScore implements TimeSeriesAnomalyDetector{
	float[] zScores;
	@Override
	public void learnNormal(TimeSeries ts) {
		zScores = new float[ts.getNumOfFeatures()];
		for(int i=0;i<ts.getNumOfFeatures();i++) {
		for(int j=0;j<ts.data[0].length;j++)
			System.out.println(ts.data[i][j] + ",");
		System.out.println("\n");
		}
		for(int i=0;i<ts.getNumOfFeatures();i++) {
			zScores[i] = findTx(ts.data[i]);
			System.out.println("Testing zScores[i] = " + zScores[i]);
		}
	}

	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> alarms = null;
		
		for(int i=0;i<ts.getNumOfFeatures();i++) {
			for(int j=0;j<ts.data[i].length;j++) {
				if (ts.data[i][j]>zScores[i]) {
					if(alarms == null)
						alarms = new ArrayList<AnomalyReport>();
					alarms.add(new AnomalyReport("Feature number " + i,j));
				}
						
			}		
		}
		return alarms;
	}
	
	public float findTx(float[]arr) {
		float[] tempArr = new float[arr.length];
		for (int x=0;x<arr.length;x++) {
			tempArr[x] = zFunc(x, arr);
		}
		return StatLib.findMax(tempArr);
	}
	public float zFunc(int x, float[]arr) {
		if(x==0) return 0;
		if(x==1) return arr[1];
		float[] tempArr = new float[x];
		for(int i=0;i<x;i++) {
			tempArr[i] = arr[i];
		}
		float mean = StatLib.avg(tempArr);
		float sd = (float) Math.abs(Math.sqrt(StatLib.var(tempArr)));
		return (arr[x]-mean)/sd;
	}

}
