package net.qwpx.snake;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Cell {
	public enum CellState {
		EMPTY, SNAKE, FOOD, SPECIAL, WALL
	}
	
	private CellState state;
	
	private static Paint snakePaint;
	private static Paint getSnakePaint() {
		if(snakePaint == null) {
			snakePaint = new Paint();
			snakePaint.setARGB(255, 0, 0, 255);
		}
		return snakePaint;
	}
	
	private static Paint foodPaint;
	private static Paint getFoodPaint() {
		if(foodPaint == null) {
			foodPaint = new Paint();
			foodPaint.setARGB(255, 0, 255, 0);
		}
		return foodPaint;
	}
	
	private static Paint specialPaint;
	private static Paint getSpecialPaint() {
		if(specialPaint == null) {
			specialPaint = new Paint();
			specialPaint.setARGB(255, 255, 0, 0);
		}
		return specialPaint;
	}
	
	
	public CellState getState() {
		return state;
	}

	public void setState(CellState state) {
		this.state = state;
	}

	private int x;
	private int y;
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Cell(int x, int y) {
		this.state = CellState.EMPTY;
		this.x = x;
		this.y = y;
	}

	public void draw(Canvas c, int height, int width) {
		Paint p = null;
		switch(state) {
		case SNAKE: p = getSnakePaint(); break;
		case FOOD: p = getFoodPaint(); break;
		case SPECIAL: p = getSpecialPaint(); break;
		} 
		if(p != null) {
			c.drawRect(y*width, x*height, (y+1)*width, (x+1)*height, p);
		}
	}
}
