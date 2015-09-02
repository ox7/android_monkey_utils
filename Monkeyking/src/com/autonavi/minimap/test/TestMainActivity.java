package com.autonavi.minimap.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.autonavi.androidmonkey.MonkeyLog;
import com.autonavi.androidmonkey.test.R;

/**
 * TODO uncomplete
 * @author roy.zj
 *
 */
public class TestMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_main);

		findViewById(R.id.run_t).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				onRun();
			}
		});
	}

	private void onRun() {
		TestMonkey tm = new TestMonkey();
		try {
			tm.getInstrumentation().start();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		Process pro = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			pro = Runtime
					.getRuntime()
					.exec("am instrument -w com.autonavi.androidmonkey.test/android.test.InstrumentationTestRunner");
			int result = pro.waitFor();
			if (result == 0) {
				is = pro.getInputStream();
			} else {
				is = pro.getErrorStream();
			}
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				MonkeyLog.l(line);
			}
			isr.close();
			br.close();
			is.close();
			pro.getOutputStream().close();
			pro.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
