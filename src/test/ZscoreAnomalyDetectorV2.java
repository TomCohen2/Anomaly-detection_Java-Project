package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZscoreAnomalyDetectorV2 implements TimeSeriesAnomalyDetector{
	float[][] TXtest;
	float[][] zScores;
	float[][] means;
	float[][] stddev;
	float[][] testMeans;
	float[][] testStddev;
	float[][] testzScores;
	
		@Override
	public void learnNormal(TimeSeries ts) {
		zScores = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds zScore for each element in each feature.
		means = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds means for each feature
		stddev = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds standard deviation for each feature
		TXtest = new float[ts.getNumOfFeatures()][ts.data[0].length];
		for(int i=0;i<ts.getNumOfFeatures();i++) {
			calcMeans(means[i], ts.data[i]);
			calcStdDev(stddev[i],ts.data[i],means[i]);
			for(int j=0;j<ts.data[0].length;j++) {
				zScores[i][j] = zFunc2(ts.data[i][j], means[i][j], stddev[i][ts.data[i].length-1]);
				checkWhatIsCurrentMax(zScores[i][j], i, j);
			}
		}
		System.out.println(stddev[24][1573]);
		System.out.println(means[24][1573]);
//		for(int i=0;i<ts.getNumOfFeatures();i++)
//			for(int k=0;k<ts.data.length;k++) {
//				System.out.print(ts.data[i][k]+",");
//			}
//		for(int i=0;i<ts.getNumOfFeatures();i++) {
//			System.out.println("Feature " + i + ":");
//			for(int j=0;j<ts.data[0].length;j++){
//				System.out.println(zScores[i][j]);
//				
//			}
//			System.out.println("");
//		}
	}

	private void calcMeans2(float[] container, float[] data) {
		float sum;
		int j=0;
		for(int i=1;i<container.length;i++) {
			sum=0;
			for(;j<i;j++) {
				sum+=data[j];
			}
			container[j] = sum/j;
		}
		
		
	}

	public float[][] getTXtest() {
		return TXtest;
	}

	public float[][] getzScores() {
		return zScores;
	}

	private void calcStdDev(float[] stdDev, float[] data, float[] means) {
		for(int i=1;i<=stdDev.length;i++) {
				stdDev[i-1] = stdDev(Arrays.copyOfRange(data, 0, i), means[i-1]);
			}
				
		}
		
	private float stdDev(float[]data, float mean) {
		float sum=0;
		for(int i=0;i<data.length;i++) {
			sum += data[i]*data[i];
		}
		sum /= data.length;
		return (float)Math.sqrt(sum-mean*mean);
	}

	private void calcMeans(float[] means, float[] data) {
		means[0] = data[0];
		for(int i=1;i<means.length;i++)
				means[i] = (means[i-1]*(i) + data[i])/(i+1);
		}


	private void checkWhatIsCurrentMax(float f, int i, int j) {
		if(j==0) {
			TXtest[i][j] = f;
			return;
		}
		else {
			if(TXtest[i][j-1] < f) {
				TXtest[i][j] = f;
				
			}
			else
				TXtest[i][j] = TXtest[i][j-1];
		}
	//	System.out.println("f = " + f + " TXtest[i][j-1] = " + TXtest[i][j-1] + " i = " + i + " j = " + j + (TXtest[i][j-1] < f));
	}

	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		testzScores = new float[ts.getNumOfFeatures()][ts.data[0].length];
		List<AnomalyReport> alarms = new ArrayList<>();
		testMeans = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds means for each feature
		testStddev = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds standard deviation for each feature
		for(int i=0;i<ts.getNumOfFeatures();i++) {
			calcMeans(testMeans[i], ts.data[i]);
			calcStdDev(testStddev[i],ts.data[i],testMeans[i]);
			for(int j=0;j<ts.data[0].length;j++) {
				testzScores[i][j] = zFunc2(ts.data[i][j], testMeans[i][j], testStddev[i][ts.data[i].length-1]);
				if(testzScores[i][j]>TXtest[i][j]){
					alarms.add(new AnomalyReport("Feature number " + i + " Zscore = " + testzScores[i][j] + " TX = " + TXtest[i][j] ,j));
				}
			}
		}
//		System.out.println(testStddev[24][2173]);
//		System.out.println(testMeans[24][2173]);
		return alarms;
	}
	


	public void fillTxValues(float[][]zScores) {
		for (int i=0;i<zScores[0].length;i++) {
			for(int j=0;j<zScores[0].length;j++) {
				//TXtest[i][j] = StatLib.findMax(zScores[i], i);
			}
		}
	}
	
	
	public float zFunc2(float x, float avg, float sd) {
		if(sd == 0) return 0;
		return (Math.abs((x-avg)))/sd;
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


