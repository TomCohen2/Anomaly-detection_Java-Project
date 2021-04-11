package test;

public class AnomalyReport {
	@Override
	public String toString() {
		return "AnomalyReport [description=" + description + ", timeStep=" + timeStep + "]";
	}
	public final String description;
	public final  long timeStep;
	public AnomalyReport(String description, long timeStep){
		this.description=description;
		this.timeStep=timeStep;
	}
}
