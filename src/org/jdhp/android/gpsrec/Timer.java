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

public class Timer {

	private long startTime;
	
	private final StringBuilder timerString = new StringBuilder();
	
	public Timer() {
		this.startTime = 0;
	}
	
	public void restart() {
		this.startTime = System.currentTimeMillis();
	}

	public int getElapsedTime() {
		return (int) (this.startTime == 0 ? 0 : System.currentTimeMillis() - this.startTime);
	}

	public String toString() {
		int elapsed_time = getElapsedTime();
    	
    	int hour = (int) ((elapsed_time / (60 * 60 * 1000)) % 24);
    	int min  = (int) ((elapsed_time / (60 * 1000)) % 60);
    	int sec  = (int) ((elapsed_time / 1000) % 60);
    	
    	timerString.setLength(0); // clear the stringBuilder
    	
    	timerString.append(hour<10 ? "0" + hour : String.valueOf(hour));
    	timerString.append(":");
    	timerString.append(min<10 ? "0" + min : String.valueOf(min));
    	timerString.append(":");
    	timerString.append(sec<10? "0" + sec : String.valueOf(sec));
    	
    	return timerString.toString();
	}
}
