package test;
import java.lang.Math;
import java.util.*;

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
	// calculate the normalized value
	public static float normalization(float x,float avX,float sDevX){
		return Math.abs(x-avX)/sDevX;
	}

	//Returns the index of feature s in TimeSeries ts.
	public static int whichIndex(String s, TimeSeries ts) {
		for (int i=0;i<ts.getNumOfFeatures();i++) {
			if (ts.features[i].equals(s))
				return i;
		}
		return -1;
	}


	//return normalized values array for given array
	public static float[] normalizationArr(float[] x) {
		float sDevX = standardDeviation(x);
		float[] avgArr = new float[x.length];
		float[] res = new float[x.length];
		avgArr[0] = 0;
		res[0] = 0;
		float sum = x[0];
		for (int i = 1; i < x.length; i++) {
			avgArr[i] = sum / i;
			sum += x[i];
		}
		for (int i = 1; i < x.length; i++) {
			res[i] = normalization(x[i], avgArr[i], sDevX);
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

	//welzl implemet
	public static Set<Point> welzl(Point [] p,Point[] r){

	}
	public static CircleDuSoleil makeCircle(List<Point> points) {
		// Clone list to preserve the caller's data, randomize order
		List<Point> shuffled = new ArrayList<>(points);
		Collections.shuffle(shuffled, new Random());
		// Progressively add points to circle or recompute circle
		CircleDuSoleil c = null;
		for (int i = 0; i < shuffled.size(); i++) {
			Point p = shuffled.get(i);
			if (c == null || !c.contains(p))
				c = makeCircleOnePoint(shuffled.subList(0, i + 1), p);
		}
		return c;
	}
	static CircleDuSoleil makeDiameter(Point a, Point b) {
		Point c = new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
		return new CircleDuSoleil(c, Math.max(c.distance(a), c.distance(b)));
	}

	static CircleDuSoleil makeCircumcircle(Point a, Point b, Point c) {
		// Mathematical algorithm from Wikipedia: Circumscribed circle
		float ox = (Math.min(Math.min(a.x, b.x), c.x) + Math.max(Math.max(a.x, b.x), c.x)) / 2;
		float oy = (Math.min(Math.min(a.y, b.y), c.y) + Math.max(Math.max(a.y, b.y), c.y)) / 2;
		float ax = a.x - ox,  ay = a.y - oy;
		float bx = b.x - ox,  by = b.y - oy;
		float cx = c.x - ox,  cy = c.y - oy;
		float d = (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) * 2;
		if (d == 0)
			return null;
		float x = ((ax*ax + ay*ay) * (by - cy) + (bx*bx + by*by) * (cy - ay) + (cx*cx + cy*cy) * (ay - by)) / d;
		float y = ((ax*ax + ay*ay) * (cx - bx) + (bx*bx + by*by) * (ax - cx) + (cx*cx + cy*cy) * (bx - ax)) / d;
		Point p = new Point(ox + x, oy + y);
		float r = Math.max(Math.max(p.distance(a), p.distance(b)), p.distance(c));
		return new CircleDuSoleil(p, r);
	}

	// Two boundary points known
	private static CircleDuSoleil makeCircleTwoPoints(List<Point> points, Point p, Point q) {
		CircleDuSoleil circ = makeDiameter(p, q);
		CircleDuSoleil left  = null;
		CircleDuSoleil right = null;

		// For each point not in the two-point circle
		Point pq = q.subtract(p);
		for (Point r : points) {
			if (circ.contains(r))
				continue;

			// Form a circumcircle and classify it on left or right side
			float cross = pq.cross(r.subtract(p));
			CircleDuSoleil c = makeCircumcircle(p, q, r);
			if (c == null)
				continue;
			else if (cross > 0 && (left == null || pq.cross(c.getCenter().subtract(p)) > pq.cross(left.getCenter().subtract(p))))
				left = c;
			else if (cross < 0 && (right == null || pq.cross(c.getCenter().subtract(p)) < pq.cross(right.getCenter().subtract(p))))
				right = c;
		}

		// Select which circle to return
		if (left == null && right == null)
			return circ;
		else if (left == null)
			return right;
		else if (right == null)
			return left;
		else
			return left.getRadius() <= right.getRadius() ? left : right;
	}

	// One boundary point known
	private static CircleDuSoleil makeCircleOnePoint(List<Point> points, Point p) {
		CircleDuSoleil c = new CircleDuSoleil(p, 0);
		for (int i = 0; i < points.size(); i++) {
			Point q = points.get(i);
			if (!c.contains(q)) {
				if (c.getRadius() == 0)
					c = makeDiameter(p, q);
				else
					c = makeCircleTwoPoints(points.subList(0, i + 1), p, q);
			}
		}
		return c;
	}
}