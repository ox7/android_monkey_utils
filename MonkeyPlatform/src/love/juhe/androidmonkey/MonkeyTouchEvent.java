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

package love.juhe.androidmonkey;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Instrumentation;
import android.util.SparseArray;
import android.view.InputDevice;
import android.view.MotionEvent;

/**
 * monkey touch event
 */
public class MonkeyTouchEvent extends MonkeyMotionEvent {
	MotionEvent event;

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
		event = getEvent();
		// System.out.println(":Touching Key (" + getTypeLabel() + "): ");
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

	@Override
	public JSONObject getEventInfo() {
		JSONObject json = new JSONObject();
		try {
			json.put("event_type", "event_touch");

			JSONObject params = new JSONObject();
			params.put("e_act", event.getAction());
			params.put("e_donw_time", event.getDownTime());
			params.put("e_event_time", event.getEventTime());
			params.put("e_pointer_count", event.getPointerCount());
			params.put("e_meta_state", event.getMetaState());
			params.put("e_x_precision", event.getXPrecision());
			params.put("e_y_precision", event.getYPrecision());
			params.put("e_device_id", event.getDeviceId());
			params.put("e_edge_flag", event.getEdgeFlags());
			params.put("e_source", event.getSource());
			params.put("e_flag", event.getFlags());

			SparseArray<MotionEvent.PointerCoords> pArray = getPointers();

			JSONArray pointers = new JSONArray();

			final int pointerCount = pArray.size();
			for (int i = 0; i < pointerCount; i++) {
				JSONObject point = new JSONObject();
				point.put("p_id", pArray.keyAt(i));
				point.put("p_x", pArray.valueAt(i).x);
				point.put("p_y", pArray.valueAt(i).y);
				point.put("p_pressure", pArray.valueAt(i).pressure);
				point.put("p_size", pArray.valueAt(i).size);
				pointers.put(i, point);
			}
			
			params.put("e_pointers", pointers);
			json.put("event_params", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}
