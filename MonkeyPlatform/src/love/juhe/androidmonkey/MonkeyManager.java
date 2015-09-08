package love.juhe.androidmonkey;

import android.app.Instrumentation;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class MonkeyManager {

	private static MonkeyManager manager = null;

	private Monkey monkey;

	public static MonkeyManager getInstance(Instrumentation inst) {
		if (manager == null) {
			manager = new MonkeyManager(inst);
		}
		return manager;
	}

	private MonkeyManager(Instrumentation inst) {
		Display display = ((WindowManager) inst.getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		monkey = new Monkey(display, "com.autonavi.minimap", inst, inst
				.getContext().getPackageManager(), MonkeyConfig.eventCount,
				MonkeyConfig.eventThrottle);
	}
	
	public Monkey getMonkey() {
		return monkey;
	}
}
