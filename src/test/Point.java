package test;

public class Point {
	public final float x,y;
	public Point(float x, float y) {
		this.x=x;
		this.y=y;
	}

	@Override
	public String toString() {
		return "Point{" +
				"x=" + x +
				", y=" + y +
				'}';
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	public Point subtract(Point p) {
		return new Point(x - p.x, y - p.y);
	}
	// Signed area / determinant thing
	public float cross(Point p) {
		return x * p.y - y * p.x;
	}
	public float distance(Point p) {
		return (float) Math.hypot(x - p.x, y - p.y);
	}
}
