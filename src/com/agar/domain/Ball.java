package com.agar.domain;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class Ball {
	private Bitmap bitmap;
	public double x = 0;
	public double y = 0;
	public double r = 10;
	public double v = 5;
	public double xv = 0;
	public double yv = 0;
	public Thread mThread ;
	public int alpha = 255;
	public int red = 0;
	public int green = 0;
	public int blue = 0;
	public Paint paint =  new Paint();
	public Color color = new Color();
	public Random rand = new Random();
	public double size ; //小球的面积
	
	public Ball(int x, int y, int r, int xv, int yv, int alpha, int red,
			int green, int blue) {
		super();
		this.x = x;
		this.y = y;
		this.r = r;
		this.xv = xv;
		this.yv = yv;
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
		size = Math.PI*r*r;
	}
	
	
	public  void onBallDraw(Canvas canvas,Point relativeScreenPoint){
		paint.setAntiAlias(true);
		paint.setColor(color.argb(alpha, red, green, blue));
		canvas.drawCircle(relativeScreenPoint.x, relativeScreenPoint.y, (int)r, paint);
	}
	
	
	/**
	 * 设置随机颜色
	 */
	public void setRandomColor(){
		alpha = rand.nextInt(156)+100;
		red = rand.nextInt(256);
		green = rand.nextInt(256);
		blue = rand.nextInt(256);
	}


	public double getX() {
		return x;
	}


	public void setX(double x) {
		this.x = x;
	}


	public double getY() {
		return y;
	}


	public void setY(double y) {
		this.y = y;
	}

}
