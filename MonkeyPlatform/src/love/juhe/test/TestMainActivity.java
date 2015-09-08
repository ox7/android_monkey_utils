package love.juhe.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import love.juhe.androidmonkey.MonkeyConfig;
import love.juhe.androidmonkey.MonkeyManager;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * TODO uncomplete
 * 
 * @author roy.zj
 * 
 */
public class TestMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onButtonClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.run_tS:
			onFireTip();
			break;
		case R.id.open_his:
			openScript();
			break;
		default:
			break;
		}
	}

	private void onFireTip() {
		final View v = LayoutInflater.from(this).inflate(
				R.layout.content_params_layout, null);
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setView(v);
		ab.setPositiveButton(R.string.action_run, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (checkParams(v)) {
					Toast.makeText(TestMainActivity.this,  R.string.tip_run, Toast.LENGTH_SHORT).show();
					onRun();
				}else {
					Toast.makeText(TestMainActivity.this, R.string.tip_params_error, Toast.LENGTH_SHORT).show();
				}
				
				dialog.dismiss();
			}
		});
		ab.show();
	}

	private boolean checkParams(View v) {
		EditText countT = (EditText) v.findViewById(R.id.params_count);
		EditText throttleT = (EditText) v.findViewById(R.id.params_throttle);
		String c = countT.getText().toString();
		String t = throttleT.getText().toString();
		if (TextUtils.isEmpty(c) || TextUtils.isEmpty(t)) {
			return false;
		}
		if (!TextUtils.isDigitsOnly(c) || !TextUtils.isDigitsOnly(t)) {
			return false;
		}

		int count = Integer.parseInt(c);
		long throttle = Long.parseLong(t);

		MonkeyConfig.eventCount = count;
		MonkeyConfig.eventThrottle = throttle;
		
		return true;
	}

	private void openScript() {
		Intent i = new Intent(this, ScriptiLogActivity.class);
		startActivity(i);
	}

	private void onRun() {
		Process pro = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			String cmd = "";
			if (android.os.Build.VERSION.SDK_INT >= 17) {
				cmd = "am instrument --user 0 -w love.juhe.test/android.test.InstrumentationTestRunner";
			} else {
				cmd = "am instrument -w love.juhe.test/android.test.InstrumentationTestRunner";
			}
			pro = Runtime.getRuntime().exec(cmd);
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
				Log.i("ttt", line);
				// TODO
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
