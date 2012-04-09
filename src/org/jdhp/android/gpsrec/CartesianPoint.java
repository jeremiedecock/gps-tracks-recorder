package org.jdhp.android.gpsrec;

public class CartesianPoint {
	
	private double x;
	
	private double y;
	
	private double z;

	public CartesianPoint(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public CartesianPoint(double x, double y) {
		this(x, y, 0);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "CartesianPoint [x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
	}

}
