/*
 * Copyright (C) 2010 The Android Open Source Project
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

import android.app.Instrumentation;
import android.view.InputDevice;
import android.view.MotionEvent;

/**
 * monkey touch event
 */
public class MonkeyTouchEvent extends MonkeyMotionEvent {
	public MonkeyTouchEvent(int action) {
		super(MonkeyEvent.EVENT_TYPE_TOUCH, InputDevice.SOURCE_TOUCHSCREEN,
				action);
	}

	@Override
	protected String getTypeLabel() {
		return "Touch";
	}

	
	@Override
	public int fireEvent(Instrumentation testRuner) {
		MotionEvent event = getEvent();
//		System.out.println(":Touching Key (" + getTypeLabel() + "): ");
		printInfo();
		try {
			testRuner.sendPointerSync(event);
		} catch (Exception e) {
			MonkeyLog.l(":Touching rejected ");
			return MonkeyEvent.INJECT_FAIL;
		} finally {
			event.recycle();
		}
 
		// testRuner.clickOnScreen(event.getRawX(), event.getRawY());

		return MonkeyEvent.INJECT_SUCCESS;
	}
}
