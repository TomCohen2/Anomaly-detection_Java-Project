package test;

public class settingsObj {
	int culNumber;
	float maxVal,minVal;
	
	public settingsObj(int culNumber, float minVal, float maxVal) {
		this.culNumber = culNumber;
		this.minVal = minVal;
		this.maxVal = maxVal;
	}
	
	public settingsObj(int culNumber) {
		this.culNumber = culNumber;
		this.minVal = Float.MIN_VALUE;
		this.maxVal = Float.MAX_VALUE;
	}

	public int getCulNumber() {
		return culNumber;
	}

	public void setCulNumber(int culNumber) {
		this.culNumber = culNumber;
	}

	public float getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(float maxVal) {
		this.maxVal = maxVal;
	}

	public float getMinVal() {
		return minVal;
	}

	public void setMinVal(float minVal) {
		this.minVal = minVal;
	}
}
