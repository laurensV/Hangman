package uva.verspeek.hangman;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	String[] words;
	List<String> guessedLetters;
	String randomWord;
	int moves;
	int maxMoves;
	int wordLength;
	private SharedPreferences gamePrefs;
	public static final String GAME_PREFS = "ArithmeticFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

		Resources res = getResources();
		words = res.getStringArray(R.array.words);
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

		Bundle newGame = getIntent().getExtras();
		if (newGame != null) {
			newGame();
		} else {
			gamePrefs = getSharedPreferences(GAME_PREFS, 0);
			guessedLetters = new ArrayList<String>(Arrays.asList(gamePrefs
					.getString("guessedLetters", "[]").replace("[", "")
					.replace("]", "").replace(" ", "").split(",")));
			// get the prefs object.
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			wordLength = settings.getInt("wordLength", 9);

			randomWord = gamePrefs.getString("word", getWord(words, wordLength)
					.toUpperCase());
			maxMoves = gamePrefs.getInt("moves",
					settings.getInt("maxMoves", 10));
			moves = getScore();


			TextView movesLeft = (TextView) findViewById(R.id.moves);
			movesLeft.setText("Moves left: " + (maxMoves - moves));
			populateButtons();
			showLetters();
		}
	}

	/* Get number of mistakes made */
	private int getScore() {
		int mistakes = 0;
		for (int i = 0; i < guessedLetters.size(); i++) {
			if (!randomWord.contains(guessedLetters.get(i)))
				mistakes++;
		}
		return mistakes;
	}

	public void newGame() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		maxMoves = settings.getInt("maxMoves", 10);
		wordLength = settings.getInt("wordLength", 9);
		randomWord = getWord(words, wordLength).toUpperCase();
		moves = 0;
		guessedLetters = new ArrayList<String>();
		TextView movesLeft = (TextView) findViewById(R.id.moves);
		movesLeft.setText("Moves left: " + (maxMoves - moves));
		populateButtons();
		showLetters();
	}

	private void setHighScore(String name) {
		int exScore = getScore();
		// we have a valid score
		SharedPreferences.Editor scoreEdit = gamePrefs.edit();
		DateFormat dateForm = new SimpleDateFormat("dd-MM-yyyy");
		String dateOutput = dateForm.format(new Date());
		String info = "" + name + " guessed " + randomWord + " on "
				+ dateOutput;
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
				String[] parts = eSc.split(". Mistakes: ");
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
			scoreEdit.putString("highScores", info + ". Mistakes: " + exScore);
			scoreEdit.commit();
		}
	}

	public static String removeDuplicateChars(final String input) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			String currentChar = input.substring(i, i + 1);
			if (result.indexOf(currentChar) < 0) // if not contained
				result.append(currentChar);
		}
		return "" + result;
	}

	private void newGuess(String letter) {
		guessedLetters.add(letter);
		moves = getScore();
		TextView movesLeft = (TextView) findViewById(R.id.moves);
		movesLeft.setText("Moves left: " + (maxMoves - moves));
		showLetters();
	}

	private void showLetters() {
		String displayWord = randomWord;
		String noDuplicates = removeDuplicateChars(randomWord);
		boolean win = true;
		for (int i = 0; i < noDuplicates.length(); i++) {
			Log.d("letter", "" + noDuplicates.charAt(i));
			if (!guessedLetters.contains("" + noDuplicates.charAt(i))) {
				displayWord = displayWord.replace("" + noDuplicates.charAt(i),
						"_ ");
				win = false;
			} else {
				displayWord = displayWord.replace("" + noDuplicates.charAt(i),
						"" + noDuplicates.charAt(i) + " ");
			}
		}
		TextView t = (TextView) findViewById(R.id.word);
		t.setText(displayWord);

		/* get the correct hangman animation */
		int numberHangmans = 9;
		int frame = (int) (((float) (numberHangmans) / (maxMoves + 1.0)) * (moves));
		Log.d("FRAMES", ""
				+ (((float) (numberHangmans) / (maxMoves + 1.0)) * (moves)));
		numberHangmans--;

		// make sure the last animation is always displayed on the last move
		if (frame == numberHangmans && moves != maxMoves)
			frame = numberHangmans - 1;
		if (frame > numberHangmans)
			frame = numberHangmans;
		if (moves == maxMoves && frame != numberHangmans) {
			frame = numberHangmans;
		}
		Resources res = getResources();
		int resourceId = res.getIdentifier("hangmanid" + frame, "drawable",
				getPackageName());

		Bitmap bitmap = BitmapFactory
				.decodeResource(getResources(), resourceId);

		int frameCount = 9;
		if (frame == 5)
			frameCount = 13;
		Animation.hangman.setFramePeriod(200);
		Animation.hangman.setSpriteWidth(bitmap.getWidth() / frameCount);
		Animation.hangman.setSpriteHeight(bitmap.getHeight());
		Animation.hangman.setX(110);
		Animation.hangman.setY(90);
		Animation.hangman.setFrameNr(frameCount);
		Animation.hangman.setCurrentFrame(0);
		Animation.hangman.setSourceRect(new Rect(0, 0, Animation.hangman
				.getSpriteWidth(), Animation.hangman.getSpriteHeight()));
		Animation.hangman.setBitmap(bitmap);

		if (win) {
			win();
		} else if (moves == maxMoves) {
			lose();
		}
	}

	private void win() {
		GridView keyboard = (GridView) findViewById(R.id.grid);
		keyboard.setOnItemClickListener(null);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("You Win");
		alert.setMessage("You made " + getScore()
				+ " mistakes.\nPlease enter your name");

		// Set an EditText view to get user input
		final EditText name = new EditText(this);
		int maxLength = 10;
		name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				maxLength) });
		alert.setView(name);

		alert.setPositiveButton("Submit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = "" + name.getText();
						if (value == "")
							value = "Anonymous";
						setHighScore(value);
						Intent highIntent = new Intent(MainActivity.this,
								HighScoreActivity.class);
						MainActivity.this.startActivity(highIntent);
						MainActivity.this.finish();
						return;
					}
				});

		alert.show();
	}

	private void lose() {
		Toast.makeText(getApplicationContext(),
				"You lose! The word was " + randomWord, Toast.LENGTH_LONG)
				.show();

		Runnable r = new Runnable() {
			@Override
			public void run() {
				Intent highIntent = new Intent(MainActivity.this,
						HighScoreActivity.class);
				MainActivity.this.startActivity(highIntent);
				MainActivity.this.finish();
			}
		};
		GridView keyboard = (GridView) findViewById(R.id.grid);
		keyboard.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
			      
			   } 
			});
		
		Handler h = new Handler();

		// delay time in miliseconds. (before going to highscores, so you can
		// see dead hangman animation)
		h.postDelayed(r, 3500);

	}

	private void populateButtons() {

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
		newGuess((String) selection.getText());
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

	public String getWord(String[] words, int length) {
		String randomWord = "";
		while (randomWord.length() != length) {
			randomWord = words[new Random().nextInt(words.length)];
		}

		return randomWord;
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Store values between instances here
		if (gamePrefs != null){
			SharedPreferences.Editor editor = gamePrefs.edit();
	
			editor.putInt("moves", maxMoves);
			editor.putString("word", randomWord);
			editor.putString("guessedLetters", "" + guessedLetters);
	
			// Commit to storage
			editor.commit();
		}
	}

}
