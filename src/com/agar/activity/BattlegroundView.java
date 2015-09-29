package com.agar.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.agar.domain.Ball;
import com.agar.domain.MyBall;

public class BattlegroundView extends View implements Runnable {

	private static final int BITMAP_REVISE_HORIZONTAL = 25; //图片修正大小
	private static final int BITMAP_REVISE_VERTICAL = 23; //图片修正大小
	private List<Ball> balls = new ArrayList<Ball>();
	private Paint paint = new Paint();
	private Color color = new Color();
	private final static int GAME_WIDTH = 2000; // 游戏界面大小
	private final static int GAME_HEIGHT = 2000;
	private Random rand = new Random();
	private MyBall mBall; // 我的小球
	private double screenWidth; // 屏幕宽度
	private double screenHeight; // 屏幕高度

	private double screenX; // 屏幕左上角的 x y
	private double screenY;


	private Thread mThread;
	private boolean flag = true; //线程是否运行
	private Context context;
	
	private double startX;
	private double startY;
	
	private double onclickX;
	private double onclickY;
	
	private Bitmap bitmap;

	public BattlegroundView(Context context) {
		super(context);
		showText("create"," BattlegroundView(Context context) ");
	}

	@SuppressWarnings("deprecation")
	public BattlegroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		showText("create"," BattlegroundView(Context context, AttributeSet attrs) ");
		this.context = context;
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		for (int i = 0; i < 500; i++) {
			balls.add(new Ball(rand.nextInt(GAME_WIDTH), rand.nextInt(GAME_HEIGHT), 10, 0,	0, 255, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		}
		// mBall = new Ball(3000, 5000, 20, 1, 1, 255,rand.nextInt(255),
		// rand.nextInt(255), rand.nextInt(255));
		mBall = new MyBall(300, 500, 30, 0, 0, 255, 12, 158, 255);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
		manager.getDefaultDisplay().getSize(p);
		screenWidth = p.x;
		screenHeight = p.y;
		initShowRange();
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				  // TODO Auto-generated method stub  
                switch(event.getAction()){  
                case MotionEvent.ACTION_DOWN:  
                    System.out.println("---action down-----");  
                    showText("起始位置为："+"("+event.getX()+" , "+event.getY()+")");  
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    onclickX = startX;
                    onclickY = startY;
                    break;  
                case MotionEvent.ACTION_MOVE:  
                    System.out.println("---action move-----");  
                    showText("移动中坐标为："+"("+event.getX()+" , "+event.getY()+")"); 
                    screenX -= (int)(event.getX()-startX);
                    screenY -= (int)(event.getY()-startY);
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;  
                case MotionEvent.ACTION_UP:  
                    System.out.println("---action up-----");  
                    showText("最后位置为："+"("+event.getX()+" , "+event.getY()+")");  
                    screenX += (int)(event.getX()-startX);
                    screenY += (int)(event.getY()-startY);
                    
                    if(((event.getX()-onclickX)*(event.getX()-onclickX)+(event.getY()-onclickY)*(event.getY()-onclickY))<20){
                    	showText("点击");
                    	mBall.chengeV(event.getX()+screenX, event.getY()+screenY);
                    }
                }  
                return true;  
			}
		});
		
	}
	public void showText(String msg){
		Log.e("123", msg);
	}
	public void showText(String tag,String msg){
		Log.e(tag, msg);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		
		for (int i = 0; i < balls.size(); i++) {
			Ball ball = balls.get(i);
			if(checkRange(ball)){
				ball.onBallDraw(canvas,	getRelativePoint(ball, screenX, screenY));
				if(mBall.checkDevour(ball)){
					balls.remove(ball);
				}
			}
		}
			
		mBall.onBallDraw(canvas, getRelativePoint(mBall, screenX, screenY));
		if(screenX<0+bitmap.getWidth()){
			if(screenY<GAME_HEIGHT-screenHeight&&screenY>0){
//				for (int i = 0; i < screenHeight; i=i+bitmap.getHeight()-BITMAP_REVISE_VERTICAL) {
//					canvas.drawBitmap(bitmap,(int)(0-screenX), i, paint);
//				}
				canvas.drawLine((int)(0-screenX), 0, (int)(0-screenX), (int)screenHeight, paint);
			}else if(screenY>GAME_HEIGHT-screenHeight&&screenY<GAME_HEIGHT){
//				for (int i = 0; i <GAME_HEIGHT-screenY ; i=i+(bitmap.getHeight()-BITMAP_REVISE_VERTICAL)) {
//					canvas.drawBitmap(bitmap,(int)(0-screenX), i, paint);
//				}
				canvas.drawLine((int)(0-screenX), 0, (int)(0-screenX), (int)(GAME_HEIGHT-screenY), paint);
			}else if(screenY<0&&screenY>-screenHeight){
//				for (int i = (int)(-screenY); i <screenHeight ; i=i+(bitmap.getHeight()-BITMAP_REVISE_VERTICAL)) {
//					canvas.drawBitmap(bitmap,(int)(0-screenX), i, paint);
//				}
				canvas.drawLine((int)(0-screenX), (int)(-screenY), (int)(0-screenX), (int)(screenHeight), paint);
			}
		}else if(screenX>GAME_WIDTH-screenWidth){
			if(screenY<GAME_HEIGHT-screenHeight&&screenY>0){
			/*	for (int i = 0; i < screenHeight; i=i+bitmap.getHeight()-BITMAP_REVISE_VERTICAL) {
					canvas.drawBitmap(bitmap,(int)(screenWidth -(screenX-GAME_WIDTH)), i, paint);
				}*/
				canvas.drawLine((int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(0), (int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(screenHeight), paint);
			}else if(screenY>GAME_HEIGHT-screenHeight&&screenY<GAME_HEIGHT){
//				for (int i = 0; i <GAME_HEIGHT-screenY ; i=i+(bitmap.getHeight()-BITMAP_REVISE_VERTICAL)) {
//					canvas.drawBitmap(bitmap,(int)(screenWidth -(screenX-GAME_WIDTH)), i, paint);
//				}
				canvas.drawLine((int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(0), (int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(GAME_HEIGHT-screenY), paint);
			}else if(screenY<0&&screenY>-screenHeight){
//				for (int i = (int)(-screenY); i <screenHeight ; i=i+(bitmap.getHeight()-BITMAP_REVISE_VERTICAL)) {
//					canvas.drawBitmap(bitmap,(int)(screenWidth -(screenX-GAME_WIDTH)), i, paint);
//				}
				canvas.drawLine((int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(-screenY), (int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(screenHeight), paint);
			}
		}
		
		if(screenY<0+bitmap.getHeight()){
			if(screenX>0&&screenX<GAME_WIDTH-screenWidth){
//				for (int i = 0; i < screenWidth; i=i+bitmap.getHeight()-BITMAP_REVISE_HORIZONTAL) {
//					canvas.drawBitmap(bitmap,i, (int)(0 -screenY), paint);
//				}
				canvas.drawLine((int)(0), (int)(0 -screenY), (int)(screenWidth), (int)(0 -screenY), paint);
			}else if(screenX<0&&screenX>-screenWidth){
//				for (int i = (int)(-screenX)+(bitmap.getWidth()-BITMAP_REVISE_HORIZONTAL); i <screenWidth ; i=i+(bitmap.getWidth()-BITMAP_REVISE_HORIZONTAL)) {
//					canvas.drawBitmap(bitmap,i, (int)(0 -screenY), paint);
//				}
				canvas.drawLine((int)(-screenX), (int)(0 -screenY), (int)(screenWidth), (int)(0 -screenY), paint);
			}else if(screenX>GAME_WIDTH-screenWidth&&screenX<GAME_WIDTH){
//				for (int i = 0; i <screenWidth-(screenX-GAME_WIDTH)-BITMAP_REVISE_HORIZONTAL ; i=i+(bitmap.getWidth()-BITMAP_REVISE_HORIZONTAL)) {
//					canvas.drawBitmap(bitmap,i, (int)(0 -screenY), paint);
//				}
				canvas.drawLine((int)( 0), (int)(0 -screenY), (int)(screenWidth-(screenX-GAME_WIDTH+screenWidth)), (int)(0 -screenY), paint);
			}
		}else if(screenY>GAME_HEIGHT-screenHeight){
			if(screenX>0&&screenX<GAME_WIDTH-screenWidth){
//				for (int i = 0; i < screenWidth; i=i+bitmap.getHeight()-BITMAP_REVISE_HORIZONTAL) {
//					canvas.drawBitmap(bitmap,i, (int)(GAME_HEIGHT-screenY), paint);
//				}
				canvas.drawLine((int)(0), (int)(GAME_HEIGHT-screenY), (int)(screenWidth), (int)(GAME_HEIGHT-screenY), paint);
			}else if(screenX<0&&screenX>-screenWidth){
//				for (int i = (int)(-screenX)+(bitmap.getWidth()-BITMAP_REVISE_HORIZONTAL); i <screenWidth ; i=i+(bitmap.getWidth()-BITMAP_REVISE_HORIZONTAL)) {
//					canvas.drawBitmap(bitmap,i, (int)(GAME_HEIGHT-screenY), paint);
//				}
				canvas.drawLine((int)(-screenX), (int)(GAME_HEIGHT-screenY), (int)(screenWidth), (int)(GAME_HEIGHT-screenY), paint);
			}else if(screenX>GAME_WIDTH-screenWidth&&screenX<GAME_WIDTH){
//				for (int i = 0; i <screenWidth-(screenX-GAME_WIDTH)-BITMAP_REVISE_HORIZONTAL ; i=i+(bitmap.getWidth()-BITMAP_REVISE_HORIZONTAL)) {
//					canvas.drawBitmap(bitmap,i, (int)(GAME_HEIGHT-screenY), paint);
//				}
				canvas.drawLine((int)(0), (int)(GAME_HEIGHT-screenY), (int)(screenWidth-(screenX-GAME_WIDTH+screenWidth)), (int)(GAME_HEIGHT-screenY), paint);
			}
		}
		
		
		
		if (mThread == null) {
			showText("create"," onDraw(Canvas canvas) mThread ");
			mThread = new Thread(this);
			mThread.start();
		}
		

	}
	public boolean checkRange(Ball ball){
		double wid = ball.getX()-screenX;
		double hei = ball.getY()-screenY;
		if(wid<screenWidth&&wid>0&&hei<screenHeight&&hei>0){
			return true;
		}
		return false;
	}

	public void initShowRange() {
		screenX = mBall.getX() - (screenWidth / 2);
		screenY = mBall.getY() - (screenHeight / 2);
	}

	public List<Ball> getBalls() {
		return balls;
	}

	public void setBalls(List<Ball> balls) {
		this.balls = balls;
	}

	public Point getRelativePoint(Ball ball, double screenX, double screenY) {
		return new Point((int)(ball.getX() - screenX),(int)( ball.getY() - screenY));
	}

	@Override
	public void run() {
		while (flag) {
			try {
				Thread.sleep(15);
				postInvalidate();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		flag = false;
		super.onDetachedFromWindow();
	}
}
