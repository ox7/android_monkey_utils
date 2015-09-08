package love.juhe.test.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ScriptReader {

	private File script;
	
	public ScriptReader(File script) {
		this.script = script;
	}
	
	public void parser(){
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(script);
			br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				//TODO
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
