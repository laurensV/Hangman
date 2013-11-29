package uva.verspeek.hangman.gameplay;

import java.util.ArrayList;

import uva.verspeek.hangman.R;
import uva.verspeek.hangman.animation.Animation;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GamePlay {
	protected MainActivity context;

	public GamePlay(Context context) {
		this.context = (MainActivity) context;
	}

	public void win() {

		GridView keyboard = (GridView) context.findViewById(R.id.grid);
		keyboard.setOnItemClickListener(null);

		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("You Win");
		alert.setMessage("You made "
				+ getMistakes()
				+ " mistakes.\nScore: "
				+ context.cls.getScore(context.randomWord, context.maxMoves,
						getMistakes()) + "\nPlease enter your name");

		// Set an EditText view to get user input
		final EditText name = new EditText(context);
		int maxLength = 10;
		name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				maxLength) });
		alert.setView(name);
		alert.setCancelable(false);

		alert.setPositiveButton("Submit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String user = "" + name.getText();
						if (user == "")
							user = "Anonymous";
						context.cls.setHighScore(user, context.cls.getScore(
								context.randomWord, context.maxMoves,
								getMistakes()), context.randomWord,
								context.gamePrefs);
						context.cls.startHighscore(context);
						return;
					}
				});

		alert.show();
	}

	public void lose() {
		RelativeLayout layout = (RelativeLayout) context
				.findViewById(R.id.layout);
		for (int i = 0; i < layout.getChildCount(); i++) {

			View child = layout.getChildAt(i);
			child.setEnabled(false);
		}
		GridView keyboard = (GridView) context.findViewById(R.id.grid);
		for (int i = 0; i < keyboard.getChildCount(); i++) {

			View child = keyboard.getChildAt(i);
			child.setEnabled(false);
		}

		Toast.makeText(context.getApplicationContext(),
				"You lose! The word was " + context.randomWord,
				Toast.LENGTH_LONG).show();

		Runnable r = new Runnable() {
			@Override
			public void run() {
				context.cls.startHighscore(context);
			}
		};

		Handler h = new Handler();

		// delay time in miliseconds. (before going to highscores, so you can
		// see dead hangman animation)
		h.postDelayed(r, 3500);

	}

	/* Get number of mistakes made */
	public int getMistakes() {
		int mistakes = 0;
		for (int i = 0; i < context.guessedLetters.size(); i++) {
			if (!context.randomWord.contains(context.guessedLetters.get(i)))
				mistakes++;
		}
		return mistakes;
	}

	public void newGame() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		context.maxMoves = settings.getInt("maxMoves", 10);
		context.wordLength = settings.getInt("wordLength", 9);
		context.randomWord = context.clw.getWord(context.wordLength,
				context.words).toUpperCase();
		context.moves = 0;
		context.guessedLetters = new ArrayList<String>();
		TextView movesLeft = (TextView) context.findViewById(R.id.moves);
		movesLeft.setText("Moves left: " + (context.maxMoves - context.moves));
		context.populateButtons();
		showLetters();
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

	public void newGuess(String letter) {
		context.guessedLetters.add(letter);
		context.moves = getMistakes();
		TextView movesLeft = (TextView) context.findViewById(R.id.moves);
		movesLeft.setText("Moves left: " + (context.maxMoves - context.moves));
		showLetters();
	}

	public void showLetters() {
		String displayWord = context.randomWord;
		String noDuplicates = removeDuplicateChars(context.randomWord);
		boolean win = true;
		for (int i = 0; i < noDuplicates.length(); i++) {
			Log.d("letter", "" + noDuplicates.charAt(i));
			if (!context.guessedLetters.contains("" + noDuplicates.charAt(i))) {
				displayWord = displayWord.replace("" + noDuplicates.charAt(i),
						"_ ");
				win = false;
			} else {
				displayWord = displayWord.replace("" + noDuplicates.charAt(i),
						"" + noDuplicates.charAt(i) + " ");
			}
		}
		TextView t = (TextView) context.findViewById(R.id.word);
		t.setText(displayWord);

		/* get the correct hangman animation */
		int numberHangmans = 9;
		int frame = (int) (((float) (numberHangmans) / (context.maxMoves + 1.0)) * (context.moves));
		Log.d("FRAMES",
				""
						+ (((float) (numberHangmans) / (context.maxMoves + 1.0)) * (context.moves)));
		numberHangmans--;

		// make sure the last animation is always displayed on the last move
		if (frame == numberHangmans && context.moves != context.maxMoves)
			frame = numberHangmans - 1;
		if (frame > numberHangmans)
			frame = numberHangmans;
		if (context.moves == context.maxMoves && frame != numberHangmans) {
			frame = numberHangmans;
		}
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("hangmanid" + frame, "drawable",
				context.getPackageName());

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				resourceId);

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
			if (context.firstmove) {
				context.cls.startHighscore(context);
			} else {
				win();
			}
		} else if (context.moves == context.maxMoves) {
			if (context.firstmove) {
				context.cls.startHighscore(context);
			} else {
				lose();
			}
		}

		context.firstmove = false;
	}
}
