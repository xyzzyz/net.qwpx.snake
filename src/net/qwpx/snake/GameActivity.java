package net.qwpx.snake;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.qwpx.snake.Snake.SnakeDirection;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class GameActivity extends Activity implements SensorEventListener {
	public static final int HIGH_SCORE_REQUEST = 123;
	private GameView mGameView;

	private SensorManager mSensorManager;
	private Sensor mAccelerometerSensor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_layout);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometerSensor = mSensorManager
		.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		mGameView = (GameView) findViewById(R.id.gameview);
		mGameView.setGameActivity(this);
		if (savedInstanceState != null) {
			mGameView.restoreInstanceState(savedInstanceState);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);
		mGameView.saveInstanceState(out);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == HIGH_SCORE_REQUEST) {
			int score = data.getExtras().getInt("score");
			String name = data.getExtras().getString("name");
			FileOutputStream fos;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
				String date = sdf.format(Calendar.getInstance().getTime());
				fos = openFileOutput("high_scores_snake", MODE_APPEND);
				fos.write(String.format("%d\n%s - %s\n", score, name, date)
						.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finish();
		}
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometerSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (mGameView == null) {
			return;
		}
		// float[] o = mSensorManager.getOrientation(R, values)
		float x = event.values[0], y = event.values[1], z = event.values[2] - 9.80f;

		System.out.println(String.format("x = %f, y = %f, z = %f", x, y, z));

		if (x < -3) {
			mGameView.setSnakeDirection(SnakeDirection.UP);
		} else if (x > 3) {
			mGameView.setSnakeDirection(SnakeDirection.DOWN);
		} else if (y < -3) {
			mGameView.setSnakeDirection(SnakeDirection.LEFT);
		} else if (y > 3) {
			mGameView.setSnakeDirection(SnakeDirection.RIGHT);
		}
	}

}

