package net.qwpx.snake;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HighScoreActivity extends ListActivity {
	class HighScorePair implements Comparable {
		private int score;
		private String name;
		
		public HighScorePair(int score, String name) {
			this.score = score;
			this.name = name;
		}

		@Override
		public int compareTo(Object another) {
			HighScorePair o = (HighScorePair) another;			
			return o.score - this.score;
		}
		
		public String toString() {
			return String.format("%d - %s", score, name);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  try {
		  Scanner s = new Scanner(openFileInput("high_scores_snake"));
		  List<HighScorePair> scores = new ArrayList<HighScoreActivity.HighScorePair>(); 
		  try {
			  while(true) {
				  int score = Integer.parseInt(s.nextLine());
				  String name = s.nextLine();
				  scores.add(new HighScorePair(score, name));
			  }
		  } catch(NoSuchElementException e) {}
		  Collections.sort(scores);
		  setListAdapter(new ArrayAdapter<HighScorePair>(this, R.layout.score_item, scores));
	  } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
