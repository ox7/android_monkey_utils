package love.juhe.test;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author roy.zj
 * 
 */
public class ScriptiLogActivity extends Activity {

	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_script_list);
		context = this;
		init();
	}

	private void init() {
		File[] files = fetchLog();
		if (files != null) {
			initList(files);
		}
	}

	private File[] fetchLog() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File mDir = new File(Environment.getExternalStorageDirectory(),
					"monkey_log");
			if (mDir.exists()) {
				File eventDir = new File(mDir, "event_log");
				if (eventDir.exists() && eventDir.listFiles() != null) {
					return eventDir.listFiles();
				}
			}
		}
		return null;
	}

	private void initList(final File[] files) {
		ListView listv = (ListView) findViewById(R.id.s_list);

		int l = files.length;
		String[] names = new String[l];
		for (int i = 0; i < l; i++) {
			names[i] = files[i].getName();
		}
		listv.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, names));
		
		listv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onFireScript(files[position]);
			}
		});
	}
	
	private void onFireScript(final File f) {
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		ab.setMessage(R.string.action_run_script_test);
		ab.setPositiveButton(R.string.action_run, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO
				Toast.makeText(context, R.string.tip_close, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		ab.show();
	}
}
