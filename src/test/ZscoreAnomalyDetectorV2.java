package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZscoreAnomalyDetectorV2 implements TimeSeriesAnomalyDetector{
	float[][] TXtest;
	float[][] zScores;
	float[][] means;
	float[][] stddev;
	public float[][] getMeans() {
		return means;
	}

	public float[][] getStddev() {
		return stddev;
	}

	@Override
	public void learnNormal(TimeSeries ts) {
	//	System.out.println("LEARN NORMAL   " + ts.getNumOfFeatures() + " " + ts.data[0].length);
		zScores = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds zScore for each element in each feature.
		means = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds means for each feature
		stddev = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds standard deviation for each feature
		TXtest = new float[ts.getNumOfFeatures()][ts.data[0].length];
		for(int i=0;i<ts.getNumOfFeatures();i++) {
			calcMeans(means[i], ts.data[i]);
			calcStdDev(stddev[i],ts.data[i],means[i]);
			//extractData(means[i], stddev[i], ts.data[i]);
		///	means[i] = StatLib.avg(ts.data[i]); // Calculating means for each feature.
		///	stddev[i] = (float)Math.abs(Math.sqrt(StatLib.var(ts.data[i]))); // Calculating standard deviation for each feature.
			//TX[i] = StatLib.findTx(ts.data[i], i);
			for(int j=0;j<ts.data[0].length;j++) {
				//System.out.println(ts.data[i][j]);// Displaying data (testing)
				zScores[i][j] = zFunc(ts.data[i][j], means[i][j], stddev[i][j]);
			//	System.out.println("TESTING " +zScores[i][j]);
				checkWhatIsCurrentMax(zScores[i][j], i, j);
			}
			System.out.println("test");
			//System.out.println("test");//fillTxValues(zScores);
		}
//		for(int i=0;i<ts.getNumOfFeatures();i++) {
//			System.out.println("Feature " + i + ":");
//			for(int j=0;j<ts.data[0].length;j++){
//				System.out.println(zScores[i][j]);
//				
//			}
//			System.out.println("");
//		}
		
		
		
//		for(int i=0;i<ts.getNumOfFeatures();i++) {
//			TX[i] = StatLib.findMax(zScores[i]);
//			//System.out.println("Testing zScores[i] = " + zScores[i]);
//		}
	}

	public float[][] getTXtest() {
		return TXtest;
	}

	public float[][] getzScores() {
		return zScores;
	}

	private void extractData(float[] means, float[] stdDev, float[] data) {
		for(int i=0; i<data.length;i++){
			
		}
		
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

	private float singleStdDev(float[] data, int j) {
		if (j==0) return 0;
		float[] temp = new float[j];
		for(int i=0;i<j;i++) {
			temp[i] = data[i];
		}
		return (float)Math.sqrt(StatLib.var(temp));
	}

	private void calcMeans(float[] means, float[] data) {
		means[0] = data[0];
		for(int i=1;i<means.length;i++) {
				means[i] = (means[i-1] + data[i])/(i+1);
//				means[j] = singleMean(data, j);
				//means[i] = StatLib.avg(Arrays.copyOfRange(data, 0, i));
			}
				
		}

	private float singleMean(float[] data, int j) {
		if (j==0) return 0;
		float[] temp = new float[j];
		for(int i=0;i<j;i++) {
			temp[i] = data[i];
		}
		return StatLib.avg(temp);
	}

	private void checkWhatIsCurrentMax(float f, int i, int j) {
		if(j==0) {
			TXtest[i][j] = f;
			//System.out.println("True = new val is bigger, False = old val is bigger.");
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
		List<AnomalyReport> alarms = new ArrayList<>();
		//float[][] zScores2 = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds zScore for each element in each feature.
		float[][] means = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds means for each feature
		float[][] stddev = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds standard deviation for each feature
		for(int i=0;i<ts.getNumOfFeatures();i++) {
			calcMeans(means[i], ts.data[i]);
			calcStdDev(stddev[i],ts.data[i],means[i]);
			//extractData(means[i], stddev[i], ts.data[i]);
		///	means[i] = StatLib.avg(ts.data[i]); // Calculating means for each feature.
		///	stddev[i] = (float)Math.abs(Math.sqrt(StatLib.var(ts.data[i]))); // Calculating standard deviation for each feature.
			//TX[i] = StatLib.findTx(ts.data[i], i);
			for(int j=0;j<ts.data[0].length;j++) {
				//System.out.println(ts.data[i][j]);// Displaying data (testing)1
				if(zFunc(ts.data[i][j], means[i][j], stddev[i][j])>TXtest[i][j]){
					alarms.add(new AnomalyReport("Feature number " + i,j));
					
				}
			//	System.out.println("TESTING " +zScores[i][j]);
			}
		}
		return alarms;
	}
//	public List<AnomalyReport> detect(TimeSeries ts) {
//		//System.out.println("DETECT   " + ts.getNumOfFeatures() + " " + ts.data[0].length);
//		List<AnomalyReport> alarms = new ArrayList<>();
//		float[][] zScores = new float[ts.getNumOfFeatures()][ts.data[0].length]; // Holds zScore for each element in each feature.
//		float[] means = new float[ts.getNumOfFeatures()]; // Holds means for each feature
//		float[] stddev = new float[ts.getNumOfFeatures()]; // Holds standard deviation for each feature
//		for(int i=0;i<ts.getNumOfFeatures();i++) {
//			means[i] = StatLib.avg(ts.data[i]); // Calculating means for each feature.
//			stddev[i] = (float)Math.abs(Math.sqrt(StatLib.var(ts.data[i]))); // Calculating standard deviation for each feature.
//			for(int j=0;j<ts.data[0].length;j++) {
//				zScores[i][j] = zFunc(ts.data[i][j], means[i], stddev[i]);
//				if(checkAnomaly(zScores[i][j], i, j)) {
//					if(alarms == null)
//						alarms = new ArrayList<AnomalyReport>();
//					alarms.add(new AnomalyReport("Feature number " + i,j));
//				}
//			}
//			
//		}
//		return alarms;
//	}
	
	private boolean checkAnomaly(float f, int i, int j) {
		if(j==0)
			return false;
		else 
			if(TXtest[i][j-1]<f)
					return true;
		return false;
	}

	public void fillTxValues(float[][]zScores) {
		for (int i=0;i<zScores[0].length;i++) {
			for(int j=0;j<zScores[0].length;j++) {
				//TXtest[i][j] = StatLib.findMax(zScores[i], i);
			}
		}
	}
	
//	public float findTx(float[]arr, int index) {
//		float[] tempArr = new float[arr.length];
//		for (int x=0;x<limit;x++) {
//			tempArr[x] = arr[x];
//		}
//		return StatLib.findMax(tempArr);
//	}
	
	public float zFunc(float x, float avg, float sd) {
		if(sd == 0) return 0;
		return Math.abs((x-avg)/sd);
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
