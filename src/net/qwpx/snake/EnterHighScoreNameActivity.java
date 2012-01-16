package net.qwpx.snake;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EnterHighScoreNameActivity extends Activity {
    private int score;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_score_layout);
        score = getIntent().getIntExtra("score", 0);
        TextView tv = (TextView) findViewById(R.id.score_textview);
        tv.setText(String.format("%d", score));
    }
    
    @Override
    public void onSaveInstanceState(Bundle out) {
    	super.onSaveInstanceState(out);
    }
    
    public void onSaveScoreClick(View v) {
		EditText ed = (EditText) findViewById(R.id.scoreEditText);
    	Intent i = getIntent();
    	i.putExtra("score", score);
    	i.putExtra("name", ed.getText().toString());
		setResult(RESULT_OK, i);
		finish();
    }
}
