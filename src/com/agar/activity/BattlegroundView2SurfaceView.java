package com.agar.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.agar.domain.Ball;
import com.agar.domain.MyBall;

public class BattlegroundView2SurfaceView extends SurfaceView implements android.view.SurfaceHolder.Callback {

	
	private static final int BUTTON_SIZE = 65;
	public static  int BUTTON_X ;//视角锁定按钮的 x坐标
	public static  int BUTTON_Y = BUTTON_SIZE; //视角锁定按钮的 y坐标
//	private static final int BITMAP_REVISE_HORIZONTAL = 25; //图片修正大小
//	private static final int BITMAP_REVISE_VERTICAL = 23; //图片修正大小
	private List<Ball> balls = new ArrayList<Ball>();
	private Paint paint = new Paint();
	public final static int GAME_WIDTH = 2000; // 游戏界面大小
	public final static int GAME_HEIGHT = 2000;
	private Random rand = new Random();
	private MyBall mBall; // 我的小球
	private double screenWidth; // 屏幕宽度
	private double screenHeight; // 屏幕高度

	private double screenX; // 屏幕左上角的 x y
	private double screenY;

	private double startX;
	private double startY;
	
	private double onclickX;
	private double onclickY;
	
//	private Bitmap bitmap;

	
	private Timer timer;
	private TimerTask task;
	private String lock;   //锁定的图标
	private String search;  //解锁的图标
	private Typeface typeface;
	private boolean isLock = false; //是否锁定视角
	

	@SuppressLint("ClickableViewAccessibility")
	public BattlegroundView2SurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		showText("create"," BattlegroundView(Context context, AttributeSet attrs) ");
		getHolder().addCallback(this);
		for (int i = 0; i < 500; i++) {
			balls.add(new Ball(rand.nextInt(GAME_WIDTH), rand.nextInt(GAME_HEIGHT), 10, 0,	0, 255, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
		}
		mBall = new MyBall(300, 500, 30, 0, 0, 255, 12, 158, 255);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
		manager.getDefaultDisplay().getSize(p);
		screenWidth = p.x;
		screenHeight = p.y;
		//按钮所在是x
		BUTTON_X = (int) (screenWidth-1.2*BUTTON_SIZE);
		initShowRange();
		//设置图形字体
		lock = getResources().getString(R.string.lock);
		search = getResources().getString(R.string.search);
		typeface = Typeface.createFromAsset(getResources().getAssets(), "fontawesome-webfont.ttf");
		paint.setColor(0xff12aaff);
		paint.setTypeface(typeface);
		paint.setTextSize(BUTTON_SIZE);
		
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
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
                    if(!isLock){
                    	screenX -= (int)(event.getX()-startX);
                        screenY -= (int)(event.getY()-startY);
                    }
                    
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    break;  
                case MotionEvent.ACTION_UP:  
                    System.out.println("---action up-----");  
                    showText("最后位置为："+"("+event.getX()+" , "+event.getY()+")");  
                    if(!isLock){
                    	screenX += (int)(event.getX()-startX);
                        screenY += (int)(event.getY()-startY);
                    }
                    if(((event.getX()-onclickX)*(event.getX()-onclickX)+(event.getY()-onclickY)*(event.getY()-onclickY))<20){
                    	showText("点击");
                    	float x = event.getX();
                    	float y = event.getY();
                    	//点击了 改变视角的按钮
                    	if((x -BUTTON_X<BUTTON_SIZE)&&(x -BUTTON_X>0)&&(BUTTON_Y-y)<BUTTON_SIZE&&(BUTTON_Y-y)>0) {
                    		if(isLock){
                    			isLock = false;
                    		}else{
                    			isLock = true;
                    		}
                    	}else{
                    		mBall.chengeV(event.getX()+screenX, event.getY()+screenY);
                    	}
                    	
                    }
                }  
                return true;  
			}
		});
		
	}
	public BattlegroundView2SurfaceView(Context context) {
		this(context,null);
	}
	public void showText(String msg){
		Log.e("123", msg);
	}
	public void showText(String tag,String msg){
		Log.e(tag, msg);
	}
	public void draw() {
		Canvas canvas = getHolder().lockCanvas();
		
		if(canvas!=null){
			canvas.drawColor(Color.WHITE);
			if(isLock){
				canvas.drawText(lock, BUTTON_X, BUTTON_Y, paint);
			}else{
				canvas.drawText(search, BUTTON_X, BUTTON_Y, paint);
			}
			
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
			drawBoundary(canvas);//画边界
			//锁定视角
			if(isLock){
				initShowRange();
			}
			getHolder().unlockCanvasAndPost(canvas);
		}
	}
	/**
	 * 检测球是否越界
	 * @param ball
	 * @return
	 */
	public boolean checkRange(Ball ball){
		double wid = ball.getX()-screenX;
		double hei = ball.getY()-screenY;
		if(wid<screenWidth&&wid>0&&hei<screenHeight&&hei>0){
			return true;
		}
		return false;
	}

	/**
	 * 我的球居中显示
	 */
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

	/**
	 * 计算ball相对于屏幕的圆心坐标
	 * @param ball
	 * @param screenX
	 * @param screenY
	 * @return
	 */
	public Point getRelativePoint(Ball ball, double screenX, double screenY) {
		return new Point((int)(ball.getX() - screenX),(int)( ball.getY() - screenY));
	}

	public void startTimerTask(){
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				draw();
			}
		};
		timer.schedule(task, 15,15);
	}
	public void stopTimerTask(){
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		Log.e("123", "surfaceChanged");
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e("123", "surfaceCreated");
		startTimerTask();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e("123", "surfaceDestroyed");
		stopTimerTask();
	}
	/**
	 * 画边界
	 * @param canvas
	 */
	private void drawBoundary(Canvas canvas) {
		if(screenX<0){
			if(screenY<GAME_HEIGHT-screenHeight&&screenY>0){
				canvas.drawLine((int)(0-screenX), 0, (int)(0-screenX), (int)screenHeight, paint);
			}else if(screenY>GAME_HEIGHT-screenHeight&&screenY<GAME_HEIGHT){
				canvas.drawLine((int)(0-screenX), 0, (int)(0-screenX), (int)(GAME_HEIGHT-screenY), paint);
			}else if(screenY<0&&screenY>-screenHeight){
				canvas.drawLine((int)(0-screenX), (int)(-screenY), (int)(0-screenX), (int)(screenHeight), paint);
			}
		}else if(screenX>GAME_WIDTH-screenWidth){
			if(screenY<GAME_HEIGHT-screenHeight&&screenY>0){
				canvas.drawLine((int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(0), (int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(screenHeight), paint);
			}else if(screenY>GAME_HEIGHT-screenHeight&&screenY<GAME_HEIGHT){
				canvas.drawLine((int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(0), (int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(GAME_HEIGHT-screenY), paint);
			}else if(screenY<0&&screenY>-screenHeight){
				canvas.drawLine((int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(-screenY), (int)(screenWidth -(screenX-GAME_WIDTH+screenWidth)), (int)(screenHeight), paint);
			}
		}
		
		if(screenY<0){
			if(screenX>0&&screenX<GAME_WIDTH-screenWidth){
				canvas.drawLine((int)(0), (int)(0 -screenY), (int)(screenWidth), (int)(0 -screenY), paint);
			}else if(screenX<0&&screenX>-screenWidth){
				canvas.drawLine((int)(-screenX), (int)(0 -screenY), (int)(screenWidth), (int)(0 -screenY), paint);
			}else if(screenX>GAME_WIDTH-screenWidth&&screenX<GAME_WIDTH){
				canvas.drawLine((int)( 0), (int)(0 -screenY), (int)(screenWidth-(screenX-GAME_WIDTH+screenWidth)), (int)(0 -screenY), paint);
			}
		}else if(screenY>GAME_HEIGHT-screenHeight){
			if(screenX>0&&screenX<GAME_WIDTH-screenWidth){
				canvas.drawLine((int)(0), (int)(GAME_HEIGHT-screenY), (int)(screenWidth), (int)(GAME_HEIGHT-screenY), paint);
			}else if(screenX<0&&screenX>-screenWidth){
				canvas.drawLine((int)(-screenX), (int)(GAME_HEIGHT-screenY), (int)(screenWidth), (int)(GAME_HEIGHT-screenY), paint);
			}else if(screenX>GAME_WIDTH-screenWidth&&screenX<GAME_WIDTH){
				canvas.drawLine((int)(0), (int)(GAME_HEIGHT-screenY), (int)(screenWidth-(screenX-GAME_WIDTH+screenWidth)), (int)(GAME_HEIGHT-screenY), paint);
			}
		}
	}
}
