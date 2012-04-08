/*
 * Copyright (c) 2011,2012 Jérémie DECOCK <jd.jdhp@gmail.com>,
 * 
 * All right reserved.
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jdhp.android.gpsrec;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GPSRecordController extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final TextView location_label = (TextView) findViewById(R.id.location);
        final TextView distance_label = (TextView) findViewById(R.id.distance);
        final TextView speed_label = (TextView) findViewById(R.id.speed);
        final TextView mean_speed_label = (TextView) findViewById(R.id.mean_speed);
        final TextView max_speed_label = (TextView) findViewById(R.id.max_speed);
        final TextView timer_label = (TextView) findViewById(R.id.timer);
        final ToggleButton toggle_button = (ToggleButton) findViewById(R.id.toggleButton);
        
        final FileFormat file = new GpxFormat();
        final Timer timer = new Timer();
        
        final List<SphericalPoint> pointList = new ArrayList<SphericalPoint>();
        
        Locale locale = new Locale("en", "US");
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
        final DecimalFormat decimalFormat = new DecimalFormat("#.######", decimalFormatSymbols);
        
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        // Toggle Button
        toggle_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(locationManager != null) {
	                if(toggle_button.isChecked()) {
	                	// Do record
	                	pointList.clear();
	                	
	                	distance_label.setText("dist: 0");
	                	speed_label.setText("speed: 0");
	                	mean_speed_label.setText("mean speed: 0");
	                	max_speed_label.setText("max speed: 0");
	                	
	                	timer.restart();
	                	timer_label.setText("00:00:00");
	                	
	                	try {
							file.open(GPSRecordController.this); // TODO ??!!
						} catch (IOException e) {
//							AlertDialog.Builder builder = new AlertDialog.Builder(GPSRecordController.this);
//							builder.setMessage(e.getMessage())
//							       .setCancelable(false);
//							AlertDialog alert = builder.create();
							e.printStackTrace();
						}
	                } else {
	                	// Do stop
	                	try {
							file.close();
						} catch (IOException e) {
//							AlertDialog.Builder builder = new AlertDialog.Builder(GPSRecordController.this);
//							builder.setMessage(e.getMessage())
//							       .setCancelable(false);
//							AlertDialog alert = builder.create();
							e.printStackTrace();
						}
	                }
            	} else {
            		// TODO : display an error dialog
            	}
            }
        });
        
        // GPS Service
        if(locationManager != null) {
        	
//        	// GpsStatusListener
//        	locationManager.addGpsStatusListener(new GpsStatus.Listener() {
//				
//				public void onGpsStatusChanged(int event) {
//					if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
//						GpsStatus gpsStatus = locationManager.getGpsStatus(null);
//						Iterable<GpsSatellite> iterable = gpsStatus.getSatellites();
//						
//						int cpt = 0;
//						for(GpsSatellite satellite : iterable) {
//							System.out.println("- " + satellite.toString());
//							cpt++;
//						}
//						System.out.println(cpt + " satellites found.");
//					}
//				}
//			
//        	});
        	
        	// LocationListener
	        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, new LocationListener() { // TODO
				
				public void onStatusChanged(String provider, int status, Bundle extras) {} // TODO !
				
				public void onProviderEnabled(String provider) {}
				
				public void onProviderDisabled(String provider) {} // TODO !
				
				public void onLocationChanged(Location location) {
					SphericalPoint point = new SphericalPoint(location.getLatitude(), location.getLongitude(), location.getAltitude(), new Date());
					
					location_label.setText(decimalFormat.format(point.getLatitude()) + " : " + decimalFormat.format(point.getLongitude()));
					
					if(toggle_button.isChecked()) {
						pointList.add(point);
						
						// Timer 
			        	timer_label.setText(timer.toString());
			        	
			        	distance_label.setText("dist: " + decimalFormat.format(SphericalPoint.distance(pointList)));
			        	if(pointList.size() > 2) {
			        		double speed = SphericalPoint.speed(pointList.get(pointList.size() - 2), pointList.get(pointList.size() - 1));
			        		speed_label.setText("speed: " + decimalFormat.format(speed));
			        	} else {
			        		speed_label.setText("speed: 0");
			        	}
	                	mean_speed_label.setText("mean speed: " + decimalFormat.format(SphericalPoint.meanSpeed(pointList)));
	                	max_speed_label.setText("max speed: " + decimalFormat.format(SphericalPoint.maxSpeed(pointList)));
			        	
						// Update the file
			        	try {
							file.append(point);
						} catch (IOException e) {
//							AlertDialog.Builder builder = new AlertDialog.Builder(GPSRecordController.this);
//							builder.setMessage(e.getMessage())
//							       .setCancelable(false);
//							AlertDialog alert = builder.create();
							e.printStackTrace();
						}
					}
				}
			});
	        
        }
    }
}
