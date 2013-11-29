package uva.verspeek.hangman.highscore;

import uva.verspeek.hangman.R;
import uva.verspeek.hangman.R.id;
import uva.verspeek.hangman.R.layout;
import uva.verspeek.hangman.gameplay.MainActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This is demo code to accompany the Mobiletuts+ tutorial series:
 * - Android SDK: Create an Arithmetic Game
 * 
 * Sue Smith
 * July 2013
 *
 */

public class HighScoreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);

		//get text view
		TextView scoreView = (TextView)findViewById(R.id.high_scores_list);
		//get shared prefs
		SharedPreferences scorePrefs = getSharedPreferences(MainActivity.GAME_PREFS, 0);
		//get scores
		String[] savedScores = scorePrefs.getString("highScores", "").split("\\|");
		//build string
		StringBuilder scoreBuild = new StringBuilder("");
		for(String score : savedScores){
			scoreBuild.append(score+"\n------------------------------------\n");
		}
		//display scores
		scoreView.setText(scoreBuild.toString());
		Button buttonNewGame = (Button) findViewById(R.id.buttonNewGame);
		buttonNewGame.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent gameIntent = new Intent(HighScoreActivity.this,
						MainActivity.class);
				gameIntent.putExtra("newgame", "newgame");
				HighScoreActivity.this.startActivity(gameIntent);
				HighScoreActivity.this.finish();
			}

		});
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
			Intent gameIntent = new Intent(HighScoreActivity.this,
					MainActivity.class);
			gameIntent.putExtra("newgame", "newgame");
			HighScoreActivity.this.startActivity(gameIntent);
			HighScoreActivity.this.finish();
            return true;
        }
        return false;
    }

}
