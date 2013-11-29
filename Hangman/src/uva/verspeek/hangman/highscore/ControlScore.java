package uva.verspeek.hangman.highscore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import uva.verspeek.hangman.gameplay.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ControlScore {

	public void setHighScore(String name, int exScore, String randomWord, SharedPreferences gamePrefs) {
		// we have a valid score
		SharedPreferences.Editor scoreEdit = gamePrefs.edit();
		DateFormat dateForm = new SimpleDateFormat("dd-MM-yyyy");
		String dateOutput = dateForm.format(new Date());
		String info = "" + name + "(" + randomWord + ")";
		// get existing scores
		String scores = gamePrefs.getString("highScores", "");
		// check for scores
		if (scores.length() > 0) {
			// we have existing scores
			List<Score> scoreStrings = new ArrayList<Score>();
			// split scores
			String[] exScores = scores.split("\\|");
			// add score object for each
			for (String eSc : exScores) {
				String[] parts = eSc.split(" Score: ");
				scoreStrings
						.add(new Score(parts[0], Integer.parseInt(parts[1])));
			}
			// new score
			Score newScore = new Score(info, exScore);
			scoreStrings.add(newScore);
			// sort
			Collections.sort(scoreStrings);
			Log.d("SCORE", "" + scoreStrings);
			// get top ten
			StringBuilder scoreBuild = new StringBuilder("");
			for (int s = 0; s < scoreStrings.size(); s++) {
				if (s >= 10)
					break;
				if (s > 0)
					scoreBuild.append("|");
				scoreBuild.append(scoreStrings.get(s).getScoreText());
			}
			// write to prefs
			scoreEdit.putString("highScores", scoreBuild.toString());
			scoreEdit.commit();

		} else {
			// no existing scores
			Log.d("highscore", "insert");
			scoreEdit.putString("highScores", info + " Score: " + exScore);
			scoreEdit.commit();
		}
	}
	
	public void startHighscore(Activity activity){
		Intent highIntent = new Intent(activity,
				HighScoreActivity.class);
		activity.startActivity(highIntent);
		activity.finish();
	}
	
	public int getScore(String randomWord, int maxMoves, int mistakes) {
		String letters = "ETAOINSHRDLCUMWFGYPBVKJXQZ";
		HashSet<Character> set = new HashSet<Character>();

		for (int i = 0; i < randomWord.length(); i++) {
			char c = randomWord.charAt(i);
			Log.d("char", "" + c);
			set.add(c);
		}

		int unique = set.size();
		int positions = 0;
		for (int i = 0; i < randomWord.length(); i++) {
			char c = randomWord.charAt(i);
			positions += letters.indexOf(c);
		}
		Log.d("positions", "" + positions);
		Log.d("unique", "" + unique);
		Log.d("positions", "" + randomWord.length());
		int score = (randomWord.length() * unique * positions - maxMoves)
				/ (mistakes + 1);
		if (score < 0)
			score = 0;
		return score;
	}
}
