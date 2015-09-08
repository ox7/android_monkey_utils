package love.juhe.androidmonkey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.view.Display;

public class Monkey {

	private Instrumentation inst;
	private Display display;
	private PackageManager pm;
	private String testPackage;

	private MonkeySourceRandom mEventSource;

	/** Categories we are allowed to launch **/
	private ArrayList<String> mMainCategories = new ArrayList<String>();
	/** Applications we can switch to. */
	private ArrayList<ComponentName> mMainApps = new ArrayList<ComponentName>();

	private long mThrottle = 0;
	private long throttle = 0;
	private boolean mRandomizeThrottle = false;
	private int mVerbose = 1;

	private int mEventCount = 100;

	private boolean isError = false;
	private File eventLog;
	private File eventDir;
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd--HH:mm:ss");

	public Monkey(Display display, String testPackage, Instrumentation inst,
			PackageManager mPm, int eventCount, long throttle) {
		this.display = display;
		this.inst = inst;
		this.pm = mPm;
		this.testPackage = testPackage;
		this.mEventCount = eventCount;
		this.throttle = throttle;
		init();
//		initLogFiles();
	}
	
	public void setEventCount(int eventCount) {
		this.mEventCount = eventCount;
	}
	
	public void setEventThrottle(long throttle) {
		this.throttle = throttle;
	}
	
	public void onFire() {
		if (mEventCount <= 0) {
			return;
		}
		int eventCounter = 0;
//		initEventLogFile();
		while (!isError) {
			MonkeyEvent ev = mEventSource.getNextEvent();
			if (ev != null) {
				try {
					Thread.sleep(throttle);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!(ev instanceof MonkeyThrottleEvent)) {
//					recordEvent(ev.getEventInfo());
					eventCounter++;
					if (eventCounter == mEventCount) {
						break;
					}
				}
				ev.fireEvent(inst);
			}
		}
	}

	private void initEventLogFile() {
		String logName = DATE_FORMATTER.format(new Date()) + ".log";
		eventLog = new File(eventDir, logName);
		if (!eventLog.exists()) {
			try {
				eventLog.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void recordEvent(JSONObject json) {
		if (json == null) {
			return;
		}
		if (eventLog != null && eventLog.isFile() && eventLog.canWrite()) {
			FileWriter fw = null;
			try {
				fw = new FileWriter(eventLog, true);
				fw.write(json.toString());
				fw.write(System.getProperty("line.separator"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (fw != null) {
						fw.close();
					}
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}
	}

	private void initLogFiles() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File mDir = new File(Environment.getExternalStorageDirectory(),
					"monkey_log");
			if (!mDir.exists()) {
				mDir.mkdirs();
			}
			eventDir = new File(mDir, "event_log");
			if (!eventDir.exists()) {
				eventDir.mkdirs();
			}
		}

	}

	/**
	 * Fire next random event
	 */
	public void nextRandomEvent() {
		MonkeyEvent ev = mEventSource.getNextEvent();
		// System.out.println("Firing Monkey Event:" + ev.toString());
		if (ev != null) {
			ev.fireEvent(inst);
		}

	}

	/**
	 * Initiate the monkey
	 */
	private void init() {
		Random mRandom = new SecureRandom();
		mRandom.setSeed(-1);

		getMainApps();

		mEventSource = new MonkeySourceRandom(mRandom, mMainApps, mThrottle,
				mRandomizeThrottle, display);
		mEventSource.setVerbose(mVerbose);
		mEventSource.validate();

		// start a random activity
		// mEventSource.generateActivity();
	}

	/**
	 * Extract the activities under test
	 * 
	 * @return
	 */
	private boolean getMainApps() {

		mMainCategories.add(Intent.CATEGORY_LAUNCHER);
		mMainCategories.add(Intent.CATEGORY_MONKEY);
		final int N = mMainCategories.size();
		for (int i = 0; i < N; i++) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			String category = mMainCategories.get(i);
			if (category.length() > 0) {
				intent.addCategory(category);
			}
			List<ResolveInfo> mainApps = pm.queryIntentActivities(intent, 0);
			if (mainApps == null || mainApps.size() == 0) {
				MonkeyLog.l("// Warning: no activities found for category "
						+ category);
				continue;
			}
			if (mVerbose >= 2) { // very verbose
				MonkeyLog.l("// Selecting main activities from category "
						+ category);
			}
			final int NA = mainApps.size();
			for (int a = 0; a < NA; a++) {
				ResolveInfo r = mainApps.get(a);
				String packageName = r.activityInfo.applicationInfo.packageName;
				if (packageName.equals(testPackage)) {
					mMainApps.add(new ComponentName(packageName,
							r.activityInfo.name));

					MonkeyLog.l("Mokey: " + r.activityInfo.name);
				}
			}
		}

		if (mMainApps.size() == 0) {
			MonkeyLog.l("** No activities found to run, monkey aborted.");
			return false;
		}

		return true;
	}
}
