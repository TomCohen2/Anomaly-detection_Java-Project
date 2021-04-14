package test;

public class CircleDuSoleil {
    private Point center;
    private float radius;
    private static final double epsi = 1 + 1e-14;
    public CircleDuSoleil(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }


    public boolean contains(Point p) {
        return center.distance(p) <= radius * epsi;
    }
}
