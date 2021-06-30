package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZscoreAnomalyDetector implements TimeSeriesAnomalyDetector{
    String[] features;
    float[] tX;
    float[][] zScores;
    public ZscoreAnomalyDetector(){}

    @Override
    public void learnNormal(TimeSeries ts) {
    	zScores = new float[ts.getNumOfFeatures()][ts.data[0].length];
        this.setFeatures(ts.features);
        float[][] data=ts.getData();;
        int num_of_features= ts.getNumOfFeatures();
        int num_of_rows=ts.getNumOfRows();
        this.tX = new float[num_of_features];
        for(int i=0;i<num_of_features;i++){
            float sum=data[i][0];
            float[] prev=new float[1];
            prev[0]=sum;
            float avg;
            float dev;
            float z;
            this.tX[i]=0;
            for (int j=1;j<num_of_rows;j++){
                avg=sum/j;
                dev=(float) Math.sqrt(StatLib.var2(prev,avg));
                if(dev==0)
                    z=0;
                else {
                    z=Math.abs(data[i][j]-avg)/dev;
                    zScores[i][j] = z;
                }
                if(z>this.tX[i])
                    this.tX[i]=z;
                sum+=data[i][j];
                prev= Arrays.copyOf(prev,j+1);
                prev[j]=data[i][j];
            }
        }
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        List<AnomalyReport> anomalyReports = new ArrayList<>();
        float[][] data=ts.getData();
        int num_of_features= ts.getNumOfFeatures();
        int num_of_rows=ts.getNumOfRows();
        for(int i=0;i<num_of_features;i++){
            float sum=data[i][0];
            float[] prev=new float[1];
            prev[0]=sum;
            float avg;
            float dev;
            float z;
            for (int j=1;j<num_of_rows;j++){
                avg=sum/j;
                dev=(float) Math.sqrt(StatLib.var2(prev,avg));
                if(dev==0)
                    z=0;
                else {
                    z=Math.abs(data[i][j]-avg)/dev;
                }
                if(z>this.tX[i])
                    anomalyReports.add(new AnomalyReport(this.features[i],j+1));
                sum+=data[i][j];
                prev= Arrays.copyOf(prev,j+1);
                prev[j]=data[i][j];
            }
        }
        return anomalyReports;
    }

	@Override
	public GraphStruct display(String colName) {
	    TimeSeries test = new TimeSeries();
	    test.setFeatures(features);
		GraphStruct ret = new GraphStruct();
		int index = StatLib.whichIndex(colName, test);
		float curTX = tX[index];
		ret.setThreshold(curTX);
		ret.setzScores(zScores[index]);
		ret.setStr("Z,"+colName);
		return ret;
	}
	

    public String[] getFeatures() { return features; }

    public void setFeatures(String[] features) { this.features = features; }

    public float[] gettX() { return tX; }

    public void settX(float[] tX) { this.tX = tX; }

}
