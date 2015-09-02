/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.autonavi.androidmonkey;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * monkey activity event
 */
public class MonkeyActivityEvent extends MonkeyEvent {
	private ComponentName mApp;
	long mAlarmTime = 0;

	public MonkeyActivityEvent(ComponentName app) {
		super(EVENT_TYPE_ACTIVITY);
		mApp = app;
	}

	public MonkeyActivityEvent(ComponentName app, long arg) {
		super(EVENT_TYPE_ACTIVITY);
		mApp = app;
		mAlarmTime = arg;
	}

	/**
	 * @return Intent for the new activity
	 */
	private Intent getEvent() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(mApp);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}

	@SuppressLint("NewApi")
	@Override
	public int fireEvent(Instrumentation testRuner) {
		Intent intent = getEvent();
		MonkeyLog.l(":Switch: " + intent.toUri(0));

		if (mAlarmTime != 0) {
			Bundle args = new Bundle();
			args.putLong("alarmTime", mAlarmTime);
			intent.putExtras(args);
		}
		try {
//			IntentFilter filter = new IntentFilter(getEvent().getAction());
//			ActivityMonitor monitor = testRuner.addMonitor(filter, null, true);
			testRuner.startActivitySync(intent);
//			testRuner.waitForMonitorWithTimeout(monitor, 10);

		} catch (SecurityException e) {
			MonkeyLog.l("** Permissions error starting activity "
					+ intent.toUri(0));
			return MonkeyEvent.INJECT_ERROR_SECURITY_EXCEPTION;
		}
		return MonkeyEvent.INJECT_SUCCESS;
	}

}
