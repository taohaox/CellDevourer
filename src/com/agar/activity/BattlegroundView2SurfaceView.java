package com.agar.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.agar.domain.Ball;
import com.agar.domain.MyBall;
import com.agar.socket.SocketControl;
import com.agar.socket.SocketControl.GetMsgHandler;
import com.agar.util.Code;
import com.alibaba.fastjson.JSONObject;

public class BattlegroundView2SurfaceView extends SurfaceView implements android.view.SurfaceHolder.Callback ,GetMsgHandler{

	
	private static final int BUTTON_SIZE = 65;
	public static  int BUTTON_X ;//视角锁定按钮的 x坐标
	public static  int BUTTON_Y = BUTTON_SIZE; //视角锁定按钮的 y坐标
	private List<Ball> balls = new ArrayList<Ball>();
	private Paint paint = new Paint();
	
	private MyBall mBall; // 我的小球
	private double screenWidth; // 屏幕宽度
	private double screenHeight; // 屏幕高度
			
	//从服务器获取的游戏参数
	public static int GAME_WIDTH = 0; // 游戏界面大小
	public static int GAME_HEIGHT = 0;
	private static int GAME_REFRESH_RATE;  //界面按每 GAME_REFRESH_RATE 毫秒刷新   
	
	
	/**屏幕左上角的 x*/
	private double screenX; 
	/**屏幕左上角的 y*/
	private double screenY;

	private double startX;
	private double startY;
	
	private double onclickX;  //点击的xy
	private double onclickY;
	private Timer timer;     
	private TimerTask task;
	private String lock;   //锁定的图标
	private String search;  //解锁的图标
	private Typeface typeface;
	private boolean isLock = false; //是否锁定视角
	
	public static boolean isLife = true;  //游戏是否存在   为false后接受不到服务器数据
	
	private SocketControl control = SocketControl.getInstance();
	private Context context;
	
	private Map<Long,MyBall> mballs = new ConcurrentHashMap<>();
	
	public static final int SHOW_DIALOG = 0;
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_DIALOG:
				new AlertDialog.Builder(getContext()).setTitle("提示").setMessage("游戏结束")
				.setPositiveButton("重玩", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					isLife = true;
					sendGameInfo(Code.GAME_STATUS_START);
				}
				} ).setNegativeButton("退出", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);  
				}
			}).show();
				break;
			default:
				break;
			}
			
		}
	};

	@SuppressLint("ClickableViewAccessibility")
	public BattlegroundView2SurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		getHolder().addCallback(this);
		this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mBall==null){
					return true;
				}
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
                    		sendGameInfo(Code.GAME_CHANGEV);
                    	}
                    }
                }  
                return true;  
			}
		});
		
	}
	/**
	 * 得到屏幕宽高
	 * @param context
	 */
	public void getScreenInfo(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Point p = new Point();
		manager.getDefaultDisplay().getSize(p);
		screenWidth = p.x;
		screenHeight = p.y;
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
				}
				checkPlayerEatBall(canvas, ball);
			}
			playerDraw(canvas);
			checkPlayerEatOtherPlayer();
			drawBoundary(canvas);//画边界
			//锁定视角
			if(isLock){
				initShowRange();
			}
			getHolder().unlockCanvasAndPost(canvas);
		}
	}
	private void checkPlayerEatOtherPlayer() {
		for(Entry<Long,MyBall> e:mballs.entrySet()){
			for(Entry<Long,MyBall> e2:mballs.entrySet()){
				if(e.getValue()!=e2.getValue()){
					MyBall m;
					if((m =e.getValue().checkDevour(e2.getValue()))!=null){
						mballs.remove(m.getId());
						if(m.getId()==mBall.getId()){
							stopTimerTask();
						}
					}
				}
			}
			
			
		}
	}
	/**
	 * 判断玩家是否吞球
	 * @param canvas
	 * @param ball
	 */
	public void checkPlayerEatBall(Canvas canvas, Ball ball) {
		Set<Entry<Long,MyBall>> entrySet = mballs.entrySet();
		for(Entry<Long,MyBall> e:entrySet){
			if(e.getValue().checkDevour(ball)){
				balls.remove(ball);
			}
		}
	}
	/**
	 * 显示玩家
	 * @param canvas
	 * @param ball
	 */
	public void playerDraw(Canvas canvas) {
		Set<Entry<Long,MyBall>> entrySet = mballs.entrySet();
		for(Entry<Long,MyBall> e:entrySet){
			e.getValue().onBallDraw(canvas, getRelativePoint(e.getValue(), screenX, screenY));
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
		if(timer==null){
			timer = new Timer();
			task = new TimerTask() {
				@Override
				public void run() {
					draw();
				}
			};
			timer.schedule(task, 0,GAME_REFRESH_RATE);
		}
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
		isLife = true;
		sendGameInfo(Code.GAME_STATUS_START);
		control.setHandler(this); //监听服务器返回数据的回调
		getScreenInfo(context);//得到屏幕宽高
		//按钮所在是x
		BUTTON_X = (int) (screenWidth-1.2*BUTTON_SIZE);
		//设置图形字体
		lock = getResources().getString(R.string.lock);
		search = getResources().getString(R.string.search);
		typeface = Typeface.createFromAsset(getResources().getAssets(), "fontawesome-webfont.ttf");
		paint.setColor(0xff12aaff);
		paint.setTypeface(typeface);
		paint.setTextSize(BUTTON_SIZE);
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e("123", "surfaceDestroyed");
		stopTimerTask();
		isLife = false;
		sendGameInfo(Code.GAME_STATUS_OVER);
		System.exit(0);
	}
	
	/**
	 * 发送游戏状态给服务器
	 * @param gameStatus
	 */
	public void sendGameInfo(int gameStatus) {
		if(gameStatus==Code.GAME_STATUS_START){
			control.sendMsgToServer("GAME_START",Code.GAME_STATUS_START);
		}else if(gameStatus==Code.GAME_STATUS_OVER){
			control.sendMsgToServer(JSONObject.toJSONString(mBall),Code.GAME_STATUS_OVER);
		}else if(gameStatus == Code.GAME_CHANGEV ){
			control.sendMsgToServer(JSONObject.toJSONString(mBall),Code.GAME_CHANGEV);
		}
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
	/**
	 * 处理服务器返回的数据
	 */
	@Override
	public void handlerMsg(String result) {
//		Result<String> obj = JSONObject.parseObject(result, Result.class);
		JSONObject obj = JSONObject.parseObject(result);
		int status = obj.getInteger("status");
		String data = obj.getString("data");
		if(status==Code.GAME_STATUS_START){//游戏开始  服务器分配账号
			//startTimerTask();
		}else if(status==Code.GAME_STATUS_MY_INFO){ //得到个人的位置坐标
			mBall = JSONObject.parseObject(data,MyBall.class);
			GAME_WIDTH = obj.getInteger("GAME_WIDTH");
			GAME_HEIGHT = obj.getInteger("GAME_HEIGHT");
			GAME_REFRESH_RATE = obj.getInteger("GAME_REFRESH_RATE");
			initShowRange();//球居中显示
			startTimerTask();
		}else if(status==Code.GAME_STATUS_USER_INFO){  //得到所有人的位置坐标
			if(obj.getJSONObject("data").size()==0){
				return;
			}
			for(Entry<String, Object> e: obj.getJSONObject("data").entrySet()){
				if(e.getValue()==null){
					return;
				}
				MyBall otherball = JSONObject.parseObject(e.getValue().toString(), MyBall.class); 
				mballs.put(Long.parseLong(e.getKey()), otherball);
				if(mBall==null||otherball.getId()==mBall.getId()){
					mBall = otherball;
				}
			}
//			initShowRange();//球居中显示
//			startTimerTask();
		}else if(status==Code.GAME_STATUS_BALL_INFO){  //得到所有食物的位置坐标
			balls = JSONObject.parseArray(data,Ball.class);
		}else if(status==Code.GAME_CHANGEV){
			mBall = JSONObject.parseObject(data,MyBall.class);
		}else if(status == Code.GAME_STATUS_OVER){
			stopTimerTask();
			isLife = false;
			Message message = new Message();
			message.what = SHOW_DIALOG;
			handler.sendMessage(message);
		}
	}
}
