/**
 * 
 */
package uva.verspeek.hangman;

import uva.verspeek.hangman.R;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Animation extends SurfaceView implements
SurfaceHolder.Callback {

	private AnimationThread thread;
	public static HangmanAnimated hangman;

	public Animation(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Animation(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Animation(Context context) {
		super(context);
		init();

	}

	public void init() {
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create Hangman and load bitmap
		hangman = new HangmanAnimated(BitmapFactory.decodeResource(
				getResources(), R.drawable.ic_launcher), 10, 50 // initial
				// position
				, 30, 47 // width and height of sprite
				, 5, 5); // FPS and number of frames in the animation
		// create the animation loop thread

		// MAG WEG DENK IK make the AnimationPanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the animation loop
		thread = new AnimationThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d("animation", "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.setRunning(false);
				thread.join();
				thread = null;
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d("animation", "Thread was shut down cleanly");
	}

	public void render(Canvas canvas) {
		if (canvas != null){
			canvas.drawColor(Color.WHITE);
			hangman.draw(canvas);
		}
	}

	/**
	 * This is the animation update method. It iterates through all the objects and
	 * calls their update method if they have one or calls specific engine's
	 * update method.
	 */
	public void update() {
		hangman.update(System.currentTimeMillis());
	}

}
