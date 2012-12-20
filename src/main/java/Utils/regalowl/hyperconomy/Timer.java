package regalowl.hyperconomy;

public class Timer {
	
	private int timerID;
	private HyperConomy hc;
	private long lastTime;
	private long serverUptime;
	private long frequency;
	
	Timer(long tickFrequency) {
		frequency = tickFrequency;
		lastTime = System.currentTimeMillis();
		hc = HyperConomy.hc;
		startTimer();
	}
	
	
	@SuppressWarnings("deprecation")
	public void startTimer() {
		timerID = hc.getServer().getScheduler().scheduleAsyncRepeatingTask(hc, new Runnable() {
		    public void run() {
		    	long time = System.currentTimeMillis();
		    	serverUptime += (time - lastTime);
		    	lastTime = time;
		    }
		}, frequency, frequency);
	}
	
	public void stopTimer() {
		hc.getServer().getScheduler().cancelTask(timerID);
	}
	
	public long getServerUptime() {
		return serverUptime;
	}
	
}
