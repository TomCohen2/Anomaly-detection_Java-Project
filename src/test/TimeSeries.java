package test;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class TimeSeries {
	public String[] features;
	public float[][] data;
	public int numOfRows;
	public int numOfFeatures;
	
	public TimeSeries()
	{
		features = null;
		data = null;
		numOfRows=0;
		numOfFeatures=0;
	}

	public TimeSeries(String csvFileName) {
		Scanner sc = null;
		try
		{
			sc = new Scanner(new FileReader(csvFileName));  
		
		
		}
		catch (IOException ex)
		{
			
		}
		setFeatures(sc.nextLine().split(","));
		
		sc.useDelimiter(",");
		while(sc.hasNextLine())
			addRow(sc.nextLine());
		sc.close();
		
	}
	
	public int getNumOfFeatures() {
		return numOfFeatures;
	}

	public void setNumOfFeatures(int numOfFeatures) {
		this.numOfFeatures = numOfFeatures;
	}
	
	public String[] getFeatures() {
		return features;
	}

	public void setFeatures(String[] str) {
		setNumOfFeatures(str.length);
		this.features = new String[numOfFeatures];
		for(int i=0;i<features.length;i++)
			this.features[i] = str[i];
	}

	public float[][] getData() {
		return data;
	}

	public void setData(float[][] data) {
		this.data = data;
	}

	public int getNumOfRows() {
		return numOfRows;
	}

	public void setNumOfRows(int numOfRows) {
		this.numOfRows = numOfRows;
	}
	
	public void addRow(String newRow)
	{
		float[][] tempData = new float[getNumOfFeatures()][getNumOfRows()+1];
		String temp[] = newRow.split(",");
		if(data != null)
		{
			for(int i=0;i<getNumOfFeatures();i++)
				for(int j=0;j<getNumOfRows();j++)
					tempData[i][j] =  data[i][j];
		}
		for(int i=0;i<getNumOfFeatures();i++)
			tempData[i][getNumOfRows()] = Float.parseFloat(temp[i]);
		data = tempData;
		setNumOfRows(getNumOfRows()+1);
		
	}
	public void addFeature(float[] data,String name) {
		if (this.numOfFeatures == 0) {
			this.features = new String[1];
			this.features[0] = name;
			this.data = new float[1][];
			this.numOfFeatures = 1;
			this.numOfRows = data.length;
			this.data[0] = new float[this.numOfRows];
			for (int i = 0; i < this.numOfRows; i++) {
				this.data[0][i] = data[i];
			}
		} else {
			this.features = Arrays.copyOf(this.features, this.numOfFeatures + 1);
			this.features[this.numOfFeatures] = name;
			this.data = Arrays.copyOf(this.data, this.numOfFeatures + 1);
			this.data[this.numOfFeatures] = new float[this.numOfRows];
			for (int i = 0; i < this.numOfRows; i++)
				this.data[this.numOfFeatures][i] = data[i];
			this.numOfFeatures++;
		}
	}
	
}
	
