package com.agar.domain;

import java.util.List;

import com.agar.activity.BattlegroundView2SurfaceView;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

public class MyBall extends Ball{

	public MyBall(int x, int y, int r, int xv, int yv, int alpha, int red,
			int green, int blue) {
		super(x, y, r, xv, yv, alpha, red, green, blue);
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
	
	public boolean checkDevour(Ball ball){
		double a = ball.getX()-x;
		double b =  ball.getY()-y;
		double s = Math.sqrt(a*a+b*b);
		if(r>s){
			Log.e("123", " size:"+size);
			size += ball.size;
			Log.e("123", " size:"+size);
			r = Math.sqrt((size)/Math.PI);
			Log.e("123", " r:"+r);
			return true;
		}
		return false;
	}
	
}
