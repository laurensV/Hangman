package uva.verspeek.hangman.gameplay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uva.verspeek.hangman.R;
import uva.verspeek.hangman.animation.Animation;
import uva.verspeek.hangman.highscore.ControlScore;
import uva.verspeek.hangman.settings.SettingsActivity;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	public ArrayList<String> words;
	List<String> guessedLetters;
	String randomWord;
	int moves;
	int maxMoves;
	int wordLength;
	int maxLength;
	boolean firstmove;
	ControlScore cls;
	ControlWords clw;
	GamePlay gameplay;
	public SharedPreferences gamePrefs;
	public static final String GAME_PREFS = "ArithmeticFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		ImageButton settingsButton = (ImageButton) findViewById(R.id.settings);

		settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent SettingsIntent = new Intent(MainActivity.this,
						SettingsActivity.class);
				MainActivity.this.startActivity(SettingsIntent);
				MainActivity.this.finish();
			}
		});
		cls = new ControlScore();
		clw = new ControlWords();
		gameplay = new GamePlay(this);
		gamePrefs = getSharedPreferences(GAME_PREFS, 0);
		firstmove = true;
		try {
			words = clw.populateWords(getAssets().open("words.xmf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bundle newGame = getIntent().getExtras();
		if (newGame != null) {
			gameplay.newGame();
		} else {
			guessedLetters = new ArrayList<String>(Arrays.asList(gamePrefs
					.getString("guessedLetters", "[]").replace("[", "")
					.replace("]", "").replace(" ", "").split(",")));
			// get the prefs object.
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			wordLength = settings.getInt("wordLength", 9);

			randomWord = gamePrefs.getString("word",
					clw.getWord(wordLength, words).toUpperCase());
			maxMoves = gamePrefs.getInt("moves",
					settings.getInt("maxMoves", 10));
			moves = gameplay.getMistakes();

			TextView movesLeft = (TextView) findViewById(R.id.moves);
			movesLeft.setText("Moves left: " + (maxMoves - moves));
			populateButtons();
			gameplay.showLetters();
		}

		if (maxMoves - moves <= 0) {
			cls.startHighscore(MainActivity.this);
		}
	}

	public void populateButtons() {

		GridView keyboard = (GridView) findViewById(R.id.grid);
		/* disable scrolling */
		keyboard.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		});

		/* add buttons to gridview */
		Button cb = null;
		ArrayList<Button> mButtons = new ArrayList<Button>();
		for (char buttonChar = 'A'; buttonChar <= 'Z'; buttonChar++) {
			cb = new Button(this);
			cb.setText("" + buttonChar);
			cb.setPadding(0, 0, 0, 0);
			cb.setId(buttonChar);

			cb.setTextColor(Color.parseColor("white"));
			cb.setTextSize(25);
			cb.setOnClickListener(this);
			cb.setBackgroundColor(Color.parseColor("red"));
			if (guessedLetters.contains("" + buttonChar)) {
				cb.setBackgroundColor(Color.parseColor("#858585"));
				cb.setOnClickListener(null);
			}
			mButtons.add(cb);
		}

		keyboard.setAdapter(new CustomAdapter(mButtons));
	}

	@Override
	public void onClick(View v) {
		Button selection = (Button) v;
		selection.setBackgroundColor(Color.parseColor("#858585"));
		selection.setOnClickListener(null);
		gameplay.newGuess((String) selection.getText());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent SettingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(SettingsIntent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Store values between instances here
		if (gamePrefs != null) {
			SharedPreferences.Editor editor = gamePrefs.edit();

			editor.putInt("moves", maxMoves);
			editor.putString("word", randomWord);
			editor.putString("guessedLetters", "" + guessedLetters);

			// Commit to storage
			editor.commit();
		}
	}

}
