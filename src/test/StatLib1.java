package test;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StatLib1 {

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
	
	public static float var2(float[] x, float avg) {
		int len = x.length;
		float sum = 0;
		for (float y : x)
			sum += y * y;
		return sum / len - avg * avg;
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
	
	//Returns max value in array
	public static float findMin(float[] arr) {
		float[]temp = arr.clone();
		Arrays.sort(temp);
		return temp[0];
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

	//Returns the distance between two points
	public static float dist(Point a, Point b) {
		return (float)Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y, 2));
	}
	
	public static Circle findCircleFromThreePoints(Point a, Point b, Point c) {
		Point temp = findCircleCenter(b.x-a.x,b.y-a.y,c.x-a.x,c.y-a.y);
		Point center = new Point(temp.x+a.x,temp.y+a.y);
		return new Circle(center, dist(center,a));
	}
	
	public static Point findCircleCenter(float bx, float by, float cx, float cy) {
		float B = bx*bx + by*by;
		float C = cx*cx + cy*cy;
		float D = bx*cy - by*cx;
		return new Point((cy*B-by*C)/(2*D),(bx*C - cx*B)/(2*D));
	}
	
	public static Circle findCircleFromTwoPoints(Point a, Point b) {
		Point c = new Point((a.x+b.x)/2.0f,(a.y+b.y)/2.0f);
		return new Circle(c, dist(a,b)/2.0f);
	}
	
	public static boolean isValidCircle(Circle c, List<Point> points) {
		for (Point p : points)
			if(!c.contains(p))
				return false;
		return true;
		
	}
	
	public static Circle trivialCircle(List<Point> points) {
		if(points.size()>3) System.out.println("HUGE ERROR");
		if(points.size()==0) return new Circle(new Point(0,0),0);
		else if (points.size()==1) return new Circle(points.get(0),0);
		else if (points.size()==2) return findCircleFromTwoPoints(points.get(0),points.get(1));
		
		for(int i=0;i<3;i++) {
			for(int j=i+1;j<3;j++) {
				Circle c = findCircleFromTwoPoints(points.get(i),points.get(j));
				if(isValidCircle(c, points))
					return c;
			}
		}
		return findCircleFromThreePoints(points.get(0), points.get(1), points.get(2));
	}

	private static Circle welzlHelper(List<Point> pointsList, List<Point> boundries, int size) {
		Random r=new Random();
		System.out.println("Boundries.size = " + boundries.size());
		if(size == 0 || boundries.size() >= 3) return trivialCircle(boundries);
		
		int index = (r.nextInt(1000)%size);
		System.out.println(index + " " + size);
		Point p = pointsList.get(index);
		System.out.println("Point[index] = " + pointsList.get(index) + " Point[size-1] = " + pointsList.get(size-1));
		Collections.swap(pointsList, index, size-1);
		System.out.println("After Swap");
		System.out.println("Point[index] = " + pointsList.get(index) + " Point[size-1] = " + pointsList.get(size-1));
		Circle d = welzlHelper(pointsList,boundries,size-1);
		if(d.contains(p))
			return d;
		
		boundries.add(p);
		
		return welzlHelper(pointsList,boundries,size-1);
	}
	
	public static Circle findMinCircle(Point[]points) {
		List<Point> pointsList = new ArrayList<>();
		List<Point> boundries = new ArrayList<>();
		for(int i=0;i<points.length;i++) {
			if(!pointsList.contains(points[i]))
				pointsList.add(points[i]);
		}
		Collections.shuffle(pointsList);
		return welzlHelper(pointsList, boundries, pointsList.size());
	}


	public static float[] TrimArr(float[] fs, int timeStep) {
		float[] temp = new float[timeStep];
		for(int i=0;i<timeStep;i++) {
			temp[i] = fs[i];
			//System.out.println("temp[i] = " + temp[i] + " fs[i] = " + fs[i]);
		}
		return temp;
	}


	///yaniv
	public static float distance(Point a, Point b) {
		return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}

	public static boolean is_inside(Circle c, Point p) {
		return distance(c.center, p) <= c.radius;
	}

	public static Circle makeCircle(List<Point> points) {
		// Clone list to preserve the caller's data, randomize order
		List<Point> shuffled = new ArrayList<>(points);
		Collections.shuffle(shuffled, new Random());
		// Progressively add points to circle or recompute circle
		Circle c = null;
		for (int i = 0; i < shuffled.size(); i++) {
			Point p = shuffled.get(i);
			if (c == null || !(is_inside(c, p)))
				c = makeCircleOnePoint(shuffled.subList(0, i + 1), p);
		}
		return c;
	}

	public static Point subtract(Point a, Point b) {
		return new Point(a.x - b.x, a.y - b.y);
	}

	public static float cross(Point a, Point b) {
		return (a.x * b.y) - (a.y * b.x);
	}

	// One boundary point known
	private static Circle makeCircleOnePoint(List<Point> points, Point p) {
		Circle c = new Circle(p, 0);
		for (int i = 0; i < points.size(); i++) {
			Point q = points.get(i);
			if (!(is_inside(c, q))) {
				if (c.radius == 0)
					c = makeDiameter(p, q);
				else
					c = makeCircleTwoPoints(points.subList(0, i + 1), p, q);
			}
		}
		return c;
	}

	// Two boundary points known
	private static Circle makeCircleTwoPoints(List<Point> points, Point p, Point q) {
		Circle circ = makeDiameter(p, q);
		Circle left = null;
		Circle right = null;
		// For each point not in the two-point circle
		Point pq = subtract(q, p);
		for (Point r : points) {
			if (is_inside(circ, r))
				continue;
			// Form a circumcircle and classify it on left or right side
			double cross = cross(pq, subtract(r, p));
			Circle c = makeCircumcircle(p, q, r);
			if (c == null)
				continue;
			else if (cross > 0 && (left == null || cross(pq, subtract(c.center, p)) > cross(pq, subtract(left.center, p))))
				left = c;
			else if (cross < 0 && (right == null || cross(pq, subtract(c.center, p)) < cross(pq, subtract(right.center, p))))
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
			return left.radius <= right.radius ? left : right;
	}


	static Circle makeDiameter(Point a, Point b) {
		Point c = new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
		return new Circle(c, Math.max(distance(c, a), distance(c, b)));
	}


	static Circle makeCircumcircle(Point a, Point b, Point c) {
		// Mathematical algorithm from Wikipedia: Circumscribed circle
		float ox = (Math.min(Math.min(a.x, b.x), c.x) + Math.max(Math.max(a.x, b.x), c.x)) / 2;
		float oy = (Math.min(Math.min(a.y, b.y), c.y) + Math.max(Math.max(a.y, b.y), c.y)) / 2;
		float ax = a.x - ox, ay = a.y - oy;
		float bx = b.x - ox, by = b.y - oy;
		float cx = c.x - ox, cy = c.y - oy;
		float d = (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by)) * 2;
		if (d == 0)
			return null;
		float x = ((ax * ax + ay * ay) * (by - cy) + (bx * bx + by * by) * (cy - ay) + (cx * cx + cy * cy) * (ay - by)) / d;
		float y = ((ax * ax + ay * ay) * (cx - bx) + (bx * bx + by * by) * (ax - cx) + (cx * cx + cy * cy) * (bx - ax)) / d;
		Point p = new Point(ox + x, oy + y);
		float r = Math.max(Math.max(distance(p, a), distance(p, b)), distance(p, c));
		return new Circle(p, r);
	}
	
	public static List<Point> arrToList_of_Points(float[] x,float[] y){
		List<Point>points=new ArrayList<>();
		for(int i = 0; i < x.length;i++)
			points.add(new Point(x[i],y[i]));
		return points;
	}



	
}
