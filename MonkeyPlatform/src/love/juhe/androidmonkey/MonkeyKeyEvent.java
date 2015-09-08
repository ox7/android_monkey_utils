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

package love.juhe.androidmonkey;

import org.json.JSONObject;

import android.app.Instrumentation;
import android.view.KeyEvent;

/**
 * monkey key event
 */
public class MonkeyKeyEvent extends MonkeyEvent {
	private long mDownTime = -1;
	private int mMetaState = -1;
	private int mAction = -1;
	private int mKeyCode = -1;
	private int mScancode = -1;
	private int mRepeatCount = -1;
	private int mDeviceId = -1;
	private long mEventTime = -1;

	private KeyEvent keyEvent = null;

	public MonkeyKeyEvent(int action, int keycode) {
		super(EVENT_TYPE_KEY);
		mAction = action;
		mKeyCode = keycode;
	}

	public MonkeyKeyEvent(KeyEvent e) {
		super(EVENT_TYPE_KEY);
		keyEvent = e;
	}

	public MonkeyKeyEvent(long downTime, long eventTime, int action, int code,
			int repeat, int metaState, int device, int scancode) {
		super(EVENT_TYPE_KEY);

		mAction = action;
		mKeyCode = code;
		mMetaState = metaState;
		mScancode = scancode;
		mRepeatCount = repeat;
		mDeviceId = device;
		mDownTime = downTime;
		mEventTime = eventTime;
	}

	public int getKeyCode() {
		return mKeyCode;
	}

	public int getAction() {
		return mAction;
	}

	public long getDownTime() {
		return mDownTime;
	}

	public long getEventTime() {
		return mEventTime;
	}

	public void setDownTime(long downTime) {
		mDownTime = downTime;
	}

	public void setEventTime(long eventTime) {
		mEventTime = eventTime;
	}

	/**
	 * @return the key event
	 */
	private KeyEvent getEvent() {
		if (keyEvent == null) {
			if (mDeviceId < 0) {
				keyEvent = new KeyEvent(mAction, mKeyCode);
			} else {
				// for scripts
				keyEvent = new KeyEvent(mDownTime, mEventTime, mAction,
						mKeyCode, mRepeatCount, mMetaState, mDeviceId,
						mScancode);
			}
		}
		return keyEvent;
	}

	@Override
	public boolean isThrottlable() {
		return (getAction() == KeyEvent.ACTION_UP);
	}

	@Override
	public int fireEvent(Instrumentation testRuner) {
		String note;
		if (mAction == KeyEvent.ACTION_UP) {
			note = "ACTION_UP";
		} else {
			note = "ACTION_DOWN";
		}

		MonkeyLog.l(":Typing Key (" + note + "): " + mKeyCode
				+ "    // ");
		try {
//			testRuner.sendKeySync(getEvent());
			testRuner.sendKeyDownUpSync(mKeyCode);
		} catch (Exception e) {
			MonkeyLog.l("Failed to send key (" + note + "): " + mKeyCode
					+ "    // ");
			return MonkeyEvent.INJECT_FAIL;
		}

		return MonkeyEvent.INJECT_SUCCESS;
	}

	@Override
	public JSONObject getEventInfo() {
		JSONObject json = new JSONObject();
		try {
			json.put("event_type", "event_key");
			JSONObject params = new JSONObject();
			params.put("e_act", mAction);
			params.put("e_code", mKeyCode);
			json.put("event_params", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

}
