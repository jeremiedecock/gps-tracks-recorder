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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;

public class GpxFormat implements FileFormat {

	private PrintWriter out;
	
	private final DecimalFormat decimalFormat = new DecimalFormat("#.######", new DecimalFormatSymbols(new Locale("en", "US")));
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private final SimpleDateFormat tzFormat = new SimpleDateFormat("Z");
	
	private final SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS_Z'.gpx'");
	
	private boolean isWritable() {
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if(Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
			externalStorageWriteable = true;
		} else {
			externalStorageWriteable = false;
		}
		
		return externalStorageWriteable;
	}
	
	public void open(Context context) throws IOException {
		if(this.isWritable()) { // TODO : check if out is already open
			String filename = this.fileNameFormat.format(new Date());
			
			File file = new File(context.getExternalFilesDir(null), filename);
			//FileOutputStream fileOutputStream = new FileOutputStream(file);
			FileWriter fileWriter = new FileWriter(file);
			//this.out = new BufferedOutputStream(fileOutputStream);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			this.out = new PrintWriter(bufferedWriter);
			
			StringBuilder str = new StringBuilder();
			str.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n");
			str.append("<gpx xmlns=\"http://www.topografix.com/GPX/1/1\"\n");
			str.append("     creator=\"GPS Track Recorder - http://www.jdhp.org/\"\n");
			str.append("     version=\"1.1\"\n");
			str.append("     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
			str.append("     xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1/gpx.xsd\">\n");
			str.append("\n");
			str.append("	<trk>\n");
			str.append("		<trkseg>\n");
			this.out.println(str.toString());
		} else {
			throw new IOException("Cannot write on external media.");
			// TODO : display an error dialog
		}
	}

	public void close() throws IOException {
		if(this.isWritable()) {
			StringBuilder str = new StringBuilder();
			str.append("		</trkseg>\n");
			str.append("	</trk>\n");
			str.append("</gpx>\n");
			this.out.println(str.toString());
			
			this.out.close();
		} else {
			throw new IOException("Cannot write on external media.");
			// TODO : display an error dialog
		}
	}

	public void append(SphericalPoint point) throws IOException {		
		StringBuilder str = new StringBuilder();
		str.append("			<trkpt ");
		str.append("lat=\"" + this.decimalFormat.format(point.getLatitude()) + "\" ");
		str.append("lon=\"" + this.decimalFormat.format(point.getLongitude()) + "\"");
		str.append(">\n");
		str.append("				<time>");
		str.append(this.dateFormat.format(point.getDate()));
		str.append(this.tzFormat.format(point.getDate()).substring(0, 3)); // workaround to get xs:dateTime compatibility
		str.append(":");                                             // workaround to get xs:dateTime compatibility
		str.append(this.tzFormat.format(point.getDate()).substring(3, 5)); // workaround to get xs:dateTime compatibility
		str.append("</time>\n");
		str.append("			</trkpt>\n");
		this.out.println(str.toString());
	}

}
