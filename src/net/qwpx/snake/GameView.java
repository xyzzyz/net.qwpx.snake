package net.qwpx.snake;

import java.util.Random;

import net.qwpx.snake.GameBoard.GameLost;
import net.qwpx.snake.Snake.SnakeDirection;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	class QWPXGameThread extends Thread {
		private boolean mRun = false;

		private SurfaceHolder mSurfaceHolder;
		private Handler mHandler;

		private int mCanvasWidth;
		private int mCanvasHeight;

		private Paint mBackgroundPaint;
		private Paint mAlphaPaint;
		
		private Random mRandom;

		private GameBoard mBoard;

		private GameActivity mGameActivity;
		

		public QWPXGameThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mHandler = handler;
			mContext = context;

			Resources res = context.getResources();
		
			mBackgroundPaint = new Paint();
			mBackgroundPaint.setARGB(255, 0, 0, 0);

			mAlphaPaint = new Paint();
			mAlphaPaint.setARGB(55, 255, 255, 255);
			
			mRandom = new Random();
			
			mBoard = new GameBoard(40, 20);
		}

		@Override
		public void run() {
			while (mRun) {
				Canvas c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						doDrawGame(c);
						doUpdateState();
					}
				} finally {
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}


		private void doDrawGame(Canvas c) {
			c.drawRect(0, 0, mCanvasWidth, mCanvasHeight, mBackgroundPaint);
			mBoard.draw(c);
			c.drawRect(0, 0, (2*mCanvasWidth)/10, mCanvasHeight, mAlphaPaint);
			c.drawRect((8*mCanvasWidth)/10, 0, mCanvasWidth, mCanvasHeight, mAlphaPaint);
			c.drawRect((2*mCanvasWidth)/10 + 2, 0, (8*mCanvasWidth)/10 - 2, (3*mCanvasHeight)/10, mAlphaPaint);
			c.drawRect((2*mCanvasWidth)/10 + 2, (7*mCanvasHeight)/10, (8*mCanvasWidth)/10 - 2, mCanvasHeight, mAlphaPaint);
		}

		private void doUpdateState() {
			try {
				mBoard.advance();
			} catch (GameLost e) {
				Intent i = new Intent(mGameActivity.getApplicationContext(),
						EnterHighScoreNameActivity.class);
				i.putExtra("score", mBoard.getScore());
				mGameActivity.startActivityForResult(i,
						GameActivity.HIGH_SCORE_REQUEST);
				mRun = false;
			}
		}
		
		public void setSnakeDirection(SnakeDirection d) {
			mBoard.setSnakeDirection(d);
		}
		
		public boolean touchEvent(MotionEvent ev) {
			synchronized (mSurfaceHolder) {
				int x = (int) ev.getX(), y = (int) ev.getY();
				if (ev.getAction() == MotionEvent.ACTION_DOWN
					|| ev.getAction() == MotionEvent.ACTION_MOVE) {
					if(x < (2*mCanvasWidth)/10) {
						setSnakeDirection(SnakeDirection.LEFT);
					} else if(x > (8 * mCanvasWidth)/10) { 
						setSnakeDirection(SnakeDirection.RIGHT);
					} else if(y < (3*mCanvasHeight)/10) {
						setSnakeDirection(SnakeDirection.UP);
					} else if(y > (7*mCanvasHeight)/10) {
						setSnakeDirection(SnakeDirection.DOWN);
					}
				}
				return true;
			}
		}

		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder) {
				mCanvasWidth = width;
				mCanvasHeight = height;
			}
		}

		public void setRunning(boolean b) {
			mRun = b;
		}


		public void restoreState(Bundle savedInstanceState) {
			synchronized (mSurfaceHolder) {
			}
		}

		public Bundle saveState(Bundle outState) {
			synchronized (mSurfaceHolder) {
				if(outState != null) {
				}
			}
			return outState;
		}

		public void pause() {
			// TODO Auto-generated method stub
			
		}

		public void setGameActivity(GameActivity gameActivity) {
			mGameActivity = gameActivity;
		}

		
	}

	private Context mContext;
	private QWPXGameThread thread;
	private Bundle mRestoreBundle;
	private GameActivity mGameActivity;
	
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		
		setFocusable(true); // make sure we get key events
	}

	public QWPXGameThread getThread() {
		return thread;
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return thread.touchEvent(e);
	}

	public void setSnakeDirection(SnakeDirection d) {
		if(thread != null)
			thread.setSnakeDirection(d);
	}
	
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause(); 
    }

	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		thread.setSurfaceSize(width, height);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new QWPXGameThread(holder, mContext, new Handler() {
			@Override
			public void handleMessage(Message m) {
			}
		});
		thread.setRunning(true);
		thread.setGameActivity(mGameActivity);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public void restoreInstanceState(Bundle savedInstanceState) {
		thread.restoreState(savedInstanceState);		
	}
	
	public void saveInstanceState(Bundle out) {
		thread.saveState(out);
	}

	public void setGameActivity(GameActivity gameActivity) {
		mGameActivity = gameActivity;
		
	}

}
