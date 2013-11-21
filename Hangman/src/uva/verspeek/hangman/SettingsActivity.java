package uva.verspeek.hangman;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.widget.Button;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		addPreferencesFromResource(R.xml.settings);
		Button buttonNewGame = (Button) findViewById(R.id.buttonNewGame);
		buttonNewGame.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent gameIntent = new Intent(SettingsActivity.this,
						MainActivity.class);
				gameIntent.putExtra("newgame", "newgame");
				SettingsActivity.this.startActivity(gameIntent);
			}

		});
		Button buttonResume = (Button) findViewById(R.id.buttonResume);
		buttonResume.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent gameIntent = new Intent(SettingsActivity.this,
						MainActivity.class);
				SettingsActivity.this.startActivity(gameIntent);
			}

		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return false;
	}
}
