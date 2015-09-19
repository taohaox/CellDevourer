package com.agar.domain;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;

public class MyBall extends Ball{

	public MyBall(int x, int y, int r, int xv, int yv, int alpha, int red,
			int green, int blue) {
		super(x, y, r, xv, yv, alpha, red, green, blue);
	}

	
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
