package net.qwpx.snake;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class QWPXSnake extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void onStartSnakeClick(View v) {
    	startActivity(new Intent(getApplicationContext(), GameActivity.class));
    }
    
    public void onHighScoresClick(View v) {
    	startActivity(new Intent(getApplicationContext(), HighScoreActivity.class));
    }
}