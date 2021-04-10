package test;
import java.lang.Math;

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
	public static float dev(Point p,Line l){ return Math.abs(l.f(p.x)-p.y); }

	// convert two arrays to an array of points
	public static Point[] arrToPoints(float[]x, float[]y)
	{
		Point[] points = new Point[x.length];
		for (int i=0; i<x.length;i++)
			points[i] = new Point(x[i],y[i]);
		return points;
	}
	// calculate the standard deviation
 	public static float standardDeviation(float[] x){ return (float) Math.pow(var(x),0.5);	}

	// calculate the normalized value
	public static float normalization(float x,float avX,float sDevX){
		return Math.abs(x-avX)/sDevX;
	}

	//return normalized values array for given array
	public static float[] normalizationArr(float[] x){
		float sDevX = standardDeviation(x);
		float[] avgArr = new float[x.length];
		float[] res = new float[x.length];
		avgArr[0] = 0;
		res[0] = 0;
		float sum =x[0];
		for (int i =1;i<x.length;i++) {
			avgArr[i] = sum/i;
			sum+=x[i];
		}
		for(int i=1;i<x.length;i++){
			res[i] = normalization(x[i],avgArr[i],sDevX);
		}
		return res;
	}

	//return the Max value of an array
	public static float getMax(float[] x){
		float max =0;
		for(int i=0;i<x.length-1;i++){
			if(max<x[i])
				max = x[i];
		}
		return max;
	}


}
