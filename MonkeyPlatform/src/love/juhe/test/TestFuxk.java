package love.juhe.test;

import love.juhe.androidmonkey.Monkey;
import love.juhe.androidmonkey.MonkeyConfig;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.view.Display;
import android.view.WindowManager;


public class TestFuxk extends InstrumentationTestCase {

	Instrumentation inst;
	Monkey monkey;

	public void testFuxk() {

		monkey.onFire();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		inst = getInstrumentation();
		
		Display display = ((WindowManager) inst.getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		monkey = new Monkey(display, "com.autonavi.minimap", inst, inst
				.getContext().getPackageManager(), MonkeyConfig.eventCount,
				MonkeyConfig.eventThrottle);

		Intent i = new Intent();
		i.setClassName("com.example.testapp",
				"com.example.testapp.MainActivity");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		inst.startActivitySync(i);
	}

}
