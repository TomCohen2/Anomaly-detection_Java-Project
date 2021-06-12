package test;

public class Circle {
	private static final float MULTIPLICATIVE_EPSILON = 1.00001f;
	public final float radius;
	public final Point center;
	
	public Circle(Point center,float radius) {
		super();
		this.radius = radius;
		this.center = center;
	}

	public float getRadius() {
		return radius;
	}

	public Point getCenter() {
		return center;
	}
	
	public boolean contains(Point p) {
		System.out.println("Distance = " + StatLib.distance(center, p) + " Radius = " + radius + " Difference = " + (radius - StatLib.distance(center, p)));
		return StatLib.distance(center, p) <= radius;
	}
}
