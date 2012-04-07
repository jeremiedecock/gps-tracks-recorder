/*
 * Copyright (c) 2011 Jérémie DECOCK <jd.jdhp@gmail.com>,
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

import org.jdhp.android.gpsrec.R;

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
        final TextView timer_label = (TextView) findViewById(R.id.timer);
        final ToggleButton toggle_button = (ToggleButton) findViewById(R.id.toggleButton);
        
        final FileFormat file = new GpxFormat();
        final Timer timer = new Timer();
        
        final DecimalFormat df = new DecimalFormat("#.######");
        
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        // Toggle Button
        toggle_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(locationManager != null) {
	                if(toggle_button.isChecked()) {
	                	// Do record
	                	timer.restart();
	                	timer_label.setText("00:00:00");
	                	
	                	try {
							file.open(GPSRecordController.this); // TODO ??!!
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                } else {
	                	// Do stop
	                	try {
							file.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }
            	}
            }
        });
        
        // GPS Service
        if(locationManager != null) {
	        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, new LocationListener() {
				
				public void onStatusChanged(String provider, int status, Bundle extras) {} // TODO !
				
				public void onProviderEnabled(String provider) {}
				
				public void onProviderDisabled(String provider) {} // TODO !
				
				public void onLocationChanged(Location location) {
					double latitude = location.getLatitude();
					double longitude = location.getLongitude();
					double altitude = location.getAltitude();
					
					location_label.setText(df.format(latitude) + " : " + df.format(longitude));
					
					if(toggle_button.isChecked()) {
						// Timer 
			        	timer_label.setText(timer.toString());
			        	
						// Update the file
			        	try {
							file.append(latitude, longitude, altitude);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
        }
    }
}
