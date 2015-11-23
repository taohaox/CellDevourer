package com.agar.domain;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

import com.agar.activity.BattlegroundView2SurfaceView;

public class MyBall extends Ball{

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 1L;
	private long id;
	private String ballName = "";
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBallName() {
		return ballName;
	}
	public void setBallName(String ballName) {
		this.ballName = ballName;
	}
	public MyBall(int x, int y, int r, int xv, int yv, int alpha, int red,
			int green, int blue, long id, String ballName) {
		super(x, y, r, xv, yv, alpha, red, green, blue);
		this.id = id;
		this.ballName = ballName;
		
	}
	
	public MyBall() {
		super();
	}
	/**
	 * 改变球的速度 方向和速度 
	 * @param targetX   将要到达的xy
	 * @param targetY
	 */
	public void chengeV(double targetX,double targetY){
		double a = targetX-x;
		double b =  targetY-y;
		double targetV = Math.sqrt(a*a+b*b);
		double c = targetV/v;
		xv = a/c;
		yv =  b/c;
		Log.e("123", "xv:"+xv+" yv:"+yv+" targetV:"+targetV+" c:"+c+" a:"+a+" b:"+b);
	}
	
	public void move(){
		x += xv;
		y += yv;
		if(x+r>BattlegroundView2SurfaceView.GAME_WIDTH){
			x = BattlegroundView2SurfaceView.GAME_WIDTH - r;
			xv = 0;
		}else if(x-r<0){
			x = r;
			xv = 0;
		}
		if(y+r>BattlegroundView2SurfaceView.GAME_HEIGHT){
			y = BattlegroundView2SurfaceView.GAME_HEIGHT - r;
			yv = 0;
		}else if(y-r<0){
			y = r;
			yv = 0;
		}
	}
	@Override
	public void onBallDraw(Canvas canvas, Point relativeScreenPoint) {
		super.onBallDraw(canvas, relativeScreenPoint);
		move();
	}
	/**
	 * 吞噬球
	 * @param ball   球  食物  玩家
	 * @return   是否吞噬
	 */
	public boolean checkDevour(Ball ball){
		double a = ball.getX()-x;
		double b =  ball.getY()-y;
		double s = Math.sqrt(a*a+b*b);
		if(r>s){
			size += ball.size;
			r = Math.sqrt((size)/Math.PI);
			return true;
		}
		return false;
	}
	/**
	 * 检查玩家吞噬or 被吞噬  
	 * @param ball 另一个玩家
	 * @return  被吞噬的玩家
	 */
	public MyBall checkDevour(MyBall mball){
		double a = mball.getX()-x;
		double b =  mball.getY()-y;
		double s = Math.sqrt(a*a+b*b);
		if(r>s||mball.r>s){
			if(size>mball.size){
				size += mball.size;
				r = Math.sqrt((size)/Math.PI);
				return mball;
			}else{
				mball.size += size;
				mball.r = Math.sqrt((mball.size)/Math.PI);
				return this;
			}
		}
		return null;
	}
	
}
