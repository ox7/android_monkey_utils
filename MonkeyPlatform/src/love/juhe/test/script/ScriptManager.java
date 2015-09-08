package love.juhe.test.script;

import java.io.File;

public class ScriptManager {

	private static ScriptManager manager;
	
	public static ScriptManager getInstance() {
		if (manager == null) {
			manager = new ScriptManager();
		}
		return manager;
	}
	
	public void onFireScript(File script) {
		
	}
	
	private void loadScript(File script) {
		
	}
}
