package org.jdhp.android.gpsrec;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Jeremie DECOCK
 *
 * See: http://www.math.montana.edu/frankw/ccp/cases/Global-Positioning/spherical-coordinates/learn.htm
 */
public class SphericalPoint {
	
	private double latitude;
	
	private double longitude;
	
	private double altitude;
	
	private Date date;
	
	public SphericalPoint(double _latitude, double _longitude, double _altitude, Date _date) {
		this.latitude = _latitude;
		this.longitude = _longitude;
		this.altitude = _altitude;
		this.date = _date;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public double[] getRadians() {
		// Degrees
		double phi_deg = 0;
		if(this.latitude >= 0.0) {    // NORTH
			phi_deg = 90 - this.latitude;
		} else {                      // SOUTH
			phi_deg = 90 + this.latitude;
		}

		double theta_deg = this.longitude;
		
		// Radians
		double phi_rad = Math.toRadians(phi_deg);
		double theta_rad = Math.toRadians(theta_deg);
		
		double[] radians = {phi_rad, theta_rad};
		
		return radians;
	}
	
	public CartesianPoint toCartesian(SphericalPoint orig) {
		double[] p1_rad = orig.getRadians();
		double[] p2_rad = this.getRadians();
		
		double dist_phi = Math.abs(p2_rad[0] - p1_rad[0]);
		double dist_theta = Math.abs(p2_rad[1] - p1_rad[1]);
		
		double dist_x = dist_phi * EARTH_RADIUS;
		double dist_y = dist_theta * EARTH_RADIUS;
		
		return new CartesianPoint(dist_x, dist_y);
	}
	
	@Override
	public String toString() {
		return "SphericalPoint [latitude=" + this.latitude + ", longitude="
				+ this.longitude + ", altitude=" + this.altitude + ", date=" + this.date + "]";
	}
	
	///////////////////////////////////////////////////////////////////////////

	public static final long EARTH_RADIUS = 6371;  // TODO: KM -> CHANGE THE RADIUS UNIT TO CHANGE ALL UNITS AT ONCE !
	
	// Distances ////
	
	public static double distance(SphericalPoint p1, SphericalPoint p2) { // TODO: consider altitude !!!
		double[] p1_rad = p1.getRadians();
		double[] p2_rad = p2.getRadians();
		
		double dist_phi = Math.abs(p2_rad[0] - p1_rad[0]);
		double dist_theta = Math.abs(p2_rad[1] - p1_rad[1]);
		
		double dist_x = dist_phi * EARTH_RADIUS;
		double dist_y = dist_theta * EARTH_RADIUS;
		
		double dist = Math.sqrt(Math.pow(dist_x, 2) + Math.pow(dist_y, 2));
		
		return dist;
	}
	
	public static double[] distances(List<SphericalPoint> l) {
		double[] distances = null;
		
		if(l.size() > 1) {
			distances = new double[l.size() - 1];
			
			for(int i=0 ; i<l.size() -1 ; i++) {
				distances[i] = SphericalPoint.distance(l.get(i), l.get(i+1));
			}
		} else {
			distances = new double[1];                     // TODO : or new double[0] ?
			distances[0] = 0.0;
		}
		
		return distances;
	}
	
	public static double distance(List<SphericalPoint> l) {
		double[] distances = SphericalPoint.distances(l);

		double sum = 0.0;
		for(double distance : distances) {
			sum += distance;
		}
		
		return sum;
	}
	
	// Durations ////
	
	public static double duration(SphericalPoint p1, SphericalPoint p2) {
		double duration_sec = Math.abs(p2.getDate().getTime() - p1.getDate().getTime()) / 1000.0;
		return duration_sec;
	}
	
	public static double[] durations(List<SphericalPoint> l) {
		double[] durations = null;
		
		if(l.size() > 1) {
			durations = new double[l.size() - 1];
			
			for(int i=0 ; i<l.size()-1 ; i++) {
				durations[i] = SphericalPoint.duration(l.get(i), l.get(i+1));
			}
		} else {
			durations = new double[1];                     // TODO : or new double[0] ?
			durations[0] = 0.0;
		}
		
		return durations;
	}
	
	public static double duration(List<SphericalPoint> l) {
		double duration_sec = 0;
		
		if(l.size() > 1) {
			duration_sec = SphericalPoint.duration(l.get(0), l.get(l.size() - 1));
		}
		
		return duration_sec;
	}
	
	// Speeds ////
	
	public static double speed(SphericalPoint p1, SphericalPoint p2) { // TODO: improve accuracy considering more points...
		double speed = 0.0;
		
		double dist = SphericalPoint.distance(p1, p2);
		double duration_sec = SphericalPoint.duration(p1, p2);
		if(duration_sec > 0.0) {
			speed = dist * 3600.0 / duration_sec;
		}
		
		return speed;
	}
	
	public static double[] speeds(List<SphericalPoint> l) {
		double[] speeds = null;
		
		if(l.size() > 1) {
			speeds = new double[l.size() - 1];
			
			for(int i=0 ; i<l.size()-1 ; i++) {
				speeds[i] = SphericalPoint.speed(l.get(i), l.get(i+1));
			}
		} else {
			speeds = new double[1];                     // TODO : or new double[0] ?
			speeds[0] = 0.0;
		}
		
		return speeds;
	}
	
	public static double meanSpeed(List<SphericalPoint> l) {
		double mean = 0.0;
		
		if(l.size() > 1) {
			double dist = SphericalPoint.distance(l);
			double duration_sec = SphericalPoint.duration(l);
			if(duration_sec > 0.0) {
				mean = dist * 3600.0 / duration_sec;
			}
		}
		
		return mean;
	}

	public static double maxSpeed(List<SphericalPoint> l) {
		double[] speeds = SphericalPoint.speeds(l);

		double max = 0.0;
		for(double speed : speeds) {
			if(speed > max) {
				max = speed;
			}
		}
		
		return max;
	}
	
	public static CartesianPoint[] pointToCartesian(List<SphericalPoint> sph_points, SphericalPoint orig) {
		CartesianPoint[] cart_points = new CartesianPoint[sph_points.size()];
		
		for(int i=0 ; i < sph_points.size() ; i++) {
			cart_points[i] = sph_points.get(i).toCartesian(orig);
		}
		
		return cart_points;
	}
	
}
