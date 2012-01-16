package net.qwpx.snake;

import java.util.LinkedList;
import java.util.Queue;

import android.webkit.DownloadListener;

import net.qwpx.snake.Cell.CellState;

public class Snake {
	public enum SnakeAdvanceResult {
		NOTHING, DEAD, ATE_FOOD, ATE_SPECIAL
	}
	
	public enum SnakeDirection {
		LEFT, RIGHT, UP, DOWN, CONTINUE
	}
	
	private SnakeDirection direction;
	public void setDirection(SnakeDirection newDirection) {
		if(this.changingDirection) return;
		if((this.direction == SnakeDirection.UP && newDirection != SnakeDirection.DOWN)
			|| (this.direction == SnakeDirection.DOWN && newDirection != SnakeDirection.UP)
			|| (this.direction == SnakeDirection.LEFT && newDirection != SnakeDirection.RIGHT)
			|| (this.direction == SnakeDirection.RIGHT && newDirection != SnakeDirection.LEFT)) {
			this.direction = newDirection;
			this.changingDirection = true;
		}
	}

	private Queue<Cell> cells;
	private long lastMove ;	
	private int speedLevel;
	private int speed;
	private int length;
	
	private boolean changingDirection;
	public void setSpeedLevel(int speedLevel) {
		this.speedLevel = speedLevel;
		this.speed = (int) Math.floor(300*Math.exp(-speedLevel*0.03));
	}
	

	private int headX;
	private int headY;
	
	public Snake() {
		cells = new LinkedList<Cell>();
		direction = SnakeDirection.RIGHT;
		lastMove = 0;
		length = 3;
		changingDirection = false;
	}
		
	public void putSnake(Cell[][] board, int x, int y) {
		Cell c;
		for(int i = 0; i < 3; i++) {
			c = board[x][y+i];
			c.setState(CellState.SNAKE);
			cells.add(c);
		}
		headX = x; headY = y+2;
	}

	public SnakeAdvanceResult advance(Cell[][] board, int height, int width) {
		this.changingDirection = false;
		long now = System.currentTimeMillis();
		long offset = now - lastMove;
		if(offset > speed) {
			int nextX = headX, nextY = headY;
			switch(direction) {
			case LEFT: nextY = (nextY + width - 1) % width; break;
			case RIGHT: nextY = (nextY + 1) % width; break;
			case UP: nextX = (nextX + height - 1) % height; break;
			case DOWN: nextX = (nextX + 1) % height; break;
			}
			System.err.println(String.format("head: (%d, %d), next: (%d, %d)", headX, headY, nextX, nextY));
			Cell next = board[nextX][nextY];
			CellState s = next.getState();
			if(s == CellState.SNAKE 
				|| s == CellState.WALL) {
				return SnakeAdvanceResult.DEAD;
			}
			if(s == CellState.EMPTY 
				|| s == CellState.FOOD 
				|| s == CellState.SPECIAL) {	
				next.setState(Cell.CellState.SNAKE);
				cells.add(next);
			}
			if(s == CellState.EMPTY) {
				Cell c = cells.poll();
				c.setState(CellState.EMPTY);
			} else {
				length++;
			}			
			headX = nextX; headY = nextY;
			lastMove = now;
			
			if(s == CellState.FOOD) {
				setSpeedLevel(speedLevel+1);
				return SnakeAdvanceResult.ATE_FOOD;
			} else if(s == CellState.SPECIAL) {
				setSpeedLevel(speedLevel+1);
				return SnakeAdvanceResult.ATE_SPECIAL;
			} else {
				return SnakeAdvanceResult.NOTHING;
			}
		}
		return SnakeAdvanceResult.NOTHING;
	}
}
