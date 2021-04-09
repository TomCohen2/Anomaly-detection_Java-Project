package test;
import java.lang.Math;
import java.util.Arrays;

public class StatLib {

	// simple average
	public static float avg(float[] x){
		float sum = 0;
		int count=0;
		for (float y : x)
		{
			sum = sum + y;
			++count;
		}
		return sum/count;
	}
	
	// returns the variance of X and Y
	public static float var(float[] x){
		int len=x.length;
		float sum=0;
		float avg = avg(x);
		for (float y:x)
			sum += y*y;
		return sum/len - avg*avg;
	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		int lenX = x.length;
		float avgX = avg(x);
		float avgY = avg(y);
		float sum=0;
		for(int i=0;i<lenX;i++)
		{
			sum += (x[i]-avgX)*(y[i]-avgY);
		}
		return sum/lenX;
	}

	public static Line linear_reg(float[] x, float[] y){
		float a = cov(x,y)/var(x);
		float b = avg(y)-(a*avg(x));
		Line l = new Line(a,b);
		return l;
	}
	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		float cov = cov(x,y);
		return  (float)Math.round((cov/(Math.sqrt(var(x))*Math.sqrt(var(y)))));
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		float[] x = new float[points.length];
		float[] y = new float[points.length];
		for (int i=0;i<points.length;i++)
		{
			x[i] = points[i].x;
			y[i] = points[i].y;
		}
		float a = cov(x,y)/var(x);
		float b = avg(y)-(a*avg(x));
		Line l = new Line(a,b);
		return l;
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		Line l = linear_reg(points);
		return Math.abs(l.f(p.x)-p.y);
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		return Math.abs(l.f(p.x)-p.y);
	}
	
	public static Point[] arrToPoints(float[]x, float[]y)
	{
		Point[] points = new Point[x.length];
		for (int i=0; i<x.length;i++)
			points[i] = new Point(x[i],y[i]);
		return points;
	}


	//Returns max value in array
	public static float findMax(float[] arr)
	{
		float[] temp = arr.clone();
		Arrays.sort(temp);
		return temp[arr.length-1];
	}
	
	
	//Returns an array of dev value of each point to a line.
	public static float[] devArr(Point[] points, Line l)
	{
		float[] arr = new float[points.length];
		for(int i=0;i<points.length;i++)
			arr[i] = StatLib.dev(points[i], l);
		return arr;
	}
	
	//Returns the index of the maximum value of an array.
	public static int maxAt(float[] array){
		int maxAt = 0;
		for (int i = 0; i < array.length; i++) 
		    maxAt = array[i] > array[maxAt] ? i : maxAt;
		  return maxAt;
		}
	
	
	//Returns the index of feature s in TimeSeries ts.
	public static int whichIndex(String s, TimeSeries ts) {
				for (int i=0;i<ts.getNumOfFeatures();i++) {
				    if (ts.features[i].equals(s))
				        return i;
				    }
				return -1;
	}

}
