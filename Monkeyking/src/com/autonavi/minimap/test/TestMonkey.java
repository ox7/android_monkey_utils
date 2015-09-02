package com.autonavi.minimap.test;

import com.autonavi.androidmonkey.InstrumentConfig;
import com.autonavi.androidmonkey.Monkey;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.view.Display;
import android.view.WindowManager;

public class TestMonkey extends InstrumentationTestCase {

	Instrumentation inst;

	public void testMonkey() {

		Display display = ((WindowManager) inst.getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();

		Monkey monkey = new Monkey(display, "com.autonavi.minimap", inst, inst
				.getContext().getPackageManager());
		
		for (int i = 0; i < InstrumentConfig.monkeyCount; i++) {
			monkey.nextRandomEvent();
			try {
				Thread.sleep(InstrumentConfig.monkeyThrottle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void runTest() throws Throwable {
		super.runTest();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		inst = getInstrumentation();
		Intent i = new Intent();
		i.setClassName("com.autonavi.minimap",
				"com.autonavi.map.activity.NewMapActivity");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		inst.startActivitySync(i);
	}

}
