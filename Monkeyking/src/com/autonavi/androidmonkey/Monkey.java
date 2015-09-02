package com.autonavi.androidmonkey;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

	private long mThrottle = InstrumentConfig.monkeyThrottle;
	private boolean mRandomizeThrottle = false;
	private int mVerbose = 1;

	

	public Monkey(Display display, String testPackage, Instrumentation inst, PackageManager mPm){
		this.display = display;
		this.inst = inst;
		this.pm = mPm;
		this.testPackage = testPackage;
		
		init();
	}
	
	/**
	 * Fire next random event
	 */
	public void nextRandomEvent() {
		MonkeyEvent ev = mEventSource.getNextEvent();
		//System.out.println("Firing Monkey Event:" + ev.toString());
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
//		 mEventSource.generateActivity();
	}


	/**
	 * Extract the activities under test
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
				if (packageName.equals(testPackage)){
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
