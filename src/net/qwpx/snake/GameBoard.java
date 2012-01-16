package net.qwpx.snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.qwpx.snake.Cell.CellState;
import net.qwpx.snake.Snake.SnakeDirection;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

public class GameBoard implements Parcelable {
	class GameLost extends Exception {}
	private int height;
	private int width;
	
	private Snake snake;

	private Random random;

	private long specialTime;
	private Cell specialCell;
	
	private Cell[][] board;
	
	public Cell[][] getCells() {
		return board;
	}

	private int score;
	public int getScore() {
		return score;
	}

	private int eatenFood;
	
	public GameBoard(int width, int height) {
		random = new Random();
		this.height = height;
		this.width = width;
		board = new Cell[height][];
		for(int i = 0; i < height; i++) {
			board[i] = new Cell[width];
			for(int j = 0; j < width; j++) {
				board[i][j] = new Cell(i, j);
			}
		}
		snake = new Snake();
		snake.setSpeedLevel(0);
		snake.putSnake(board, height/2, (width*3)/10);
		putFood();
		score = 0; 
		eatenFood = 0;
		specialTime = 0;
		specialCell = null;
	}
	
	public void draw(Canvas c) {
		int canvasWidth = c.getWidth();
		int canvasHeight = c.getHeight();
		int cellWidth = canvasWidth/width;
		int cellHeight = canvasHeight/height;
		for(Cell[] row: board) {
			for(Cell cell: row) {
				cell.draw(c, cellHeight, cellWidth); 
			}
		}
	}

	private List<Cell> getEmptyCells() {
		List<Cell> emptyCells = new ArrayList<Cell>(width*height);
		for(Cell[] row: board) {
			for(Cell c: row) {
				if(c.getState() == CellState.EMPTY) {
					emptyCells.add(c);
				}
			}
		}
		return emptyCells;
	}
	
	public void putFood() {
		List<Cell> empty = getEmptyCells();
		if(empty.size() == 0) return; //wtf
		Cell c = empty.get(random.nextInt(empty.size()));
		c.setState(CellState.FOOD);
	}
	
	public void putSpecial() {
		List<Cell> empty = getEmptyCells();
		if(empty.size() == 0) return; //wtf
		Cell c = empty.get(random.nextInt(empty.size()));
		c.setState(CellState.SPECIAL);
		specialCell = c;
		specialTime = System.currentTimeMillis();
	}

	private void maybeClearSpecial() {
		long now = System.currentTimeMillis();
		if(specialCell != null && now - specialTime > 5*1000) {
			specialCell.setState(CellState.EMPTY);
			specialCell = null;
		}
	}

	public void advance() throws GameLost {
		maybeClearSpecial();
		switch(snake.advance(board, height, width)) {
		case DEAD:
			throw new GameLost();
		case ATE_FOOD:
			eatenFood += 1;
			score += 10;
			putFood();
			if(eatenFood % 5 == 0) {
				putSpecial();
			}
			break;
		case ATE_SPECIAL:
			score += (int) (100 - (2*(System.currentTimeMillis() - specialTime)/100));
			specialCell = null;
			break;
		case NOTHING:
			// nothing
			break;
		}
	}
	

	public void setSnakeDirection(SnakeDirection direction) {
		snake.setDirection(direction);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	}
}
