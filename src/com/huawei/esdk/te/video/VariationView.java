package com.huawei.esdk.te.video;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.huawei.esdk.te.call.CallLogic;
import com.huawei.esdk.te.call.CallConstants.CallStatus;
import com.huawei.esdk.te.data.Constants;
import com.huawei.reader.ReaderChangeListener;
import com.huawei.service.eSpaceService;
import com.huawei.voip.CallManager;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * render缩放界面
 */
public class VariationView extends RelativeLayout
		implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener, Runnable {
	private static final String TAG = VariationView.class.getSimpleName();
	/**
	 * 边距
	 */
	private static final int FLING_MARGIN = 100;

	/**
	 * 最小缩放值
	 */
	private static final float MIN_SCALE = 1.0f;

	/**
	 * 最大缩放值
	 */
	private static final float MAX_SCALE = 3f;

	/**
	 * 滚动持续时长
	 */
	private static final int SCROLL_DURATION = 400;

	/**
	 * 手势移动为斜的
	 */
	private static final int MOVING_DIAGONALLY = 0;

	/**
	 * 手势移动向左
	 */
	private static final int MOVING_LEFT = 1;

	/**
	 * 手势移动向右
	 */
	private static final int MOVING_RIGHT = 2;

	/**
	 * 手势移动向上
	 */
	private static final int MOVING_UP = 3;

	/**
	 * 手势移动向下
	 */
	private static final int MOVING_DOWN = 4;

	/**
	 * 点击分发事件
	 */
	private List<ReaderChangeListener> readerChangeListenerList;

	/**
	 * 手势检测
	 */
	private GestureDetector gestureDetector;

	/**
	 * 缩放检测
	 */
	private ScaleGestureDetector scaleGestureDetector;

	/**
	 * 滚动
	 */
	private Scroller scroller;

	/**
	 * x轴方向滚动值
	 */
	private int scrollX = 0;

	/**
	 * y轴方向滚动值
	 */
	private int scrollY = 0;

	/**
	 * 上一次x轴方向滚动值
	 */
	private int scrollLastX = 0;

	/**
	 * 上一次y轴方向滚动值
	 */
	private int scrollLastY = 0;

	/**
	 * 是否禁止滚动
	 */
	private boolean isScrollDisabled;

	/**
	 * 是否正在缩放中
	 */
	private boolean isScaling;

	/**
	 * 缩放比例
	 */
	private float curScale = 1.0f;

	/**
	 * 对应render的 当前大小
	 */
	private Rect renderRect = new Rect(0, 0, 0, 0);

	/**
	 * 原始render的大小
	 */
	private Point srcBound = new Point(0, 0);

	/**
	 * 用来控制视图变化
	 */
	private Point borderBound = new Point(0, 0);

	/**
	 * 当前render移动量X
	 */
	private float curTransX;

	/**
	 * 当前render移动量Y
	 */
	private float curTransY;

	/**
	 * 手指点击的位置X
	 */
	private int clickX;

	/**
	 * 手指点击的位置Y
	 */
	private int clickY;

	/**
	 * 控制render移动缩放量
	 * 
	 * @author cWX176935
	 *
	 */
	private static class VariationParam {
		protected float transX;
		protected float transY;
		protected float scale;

		protected VariationParam(float scaleV, float transXV, float transYV) {
			scale = scaleV;
			transX = transXV;
			transY = transYV;
		}
	}

	/**
	 * 构造方法
	 */
	public VariationView(Context context) {
		this(context, null);
	}

	public VariationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public VariationView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * 初始化
	 */
	private void init(Context context) {
		curScale = 1.0f;
		gestureDetector = new GestureDetector(context, this);
		scaleGestureDetector = new ScaleGestureDetector(context, this);
		scroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (0 != getMeasuredWidth()) {
			if (getMeasuredWidth() == srcBound.x) {
				return;
			}
			srcBound = new Point(getMeasuredWidth(), getMeasuredHeight());
			renderRect = new Rect(borderBound.x, borderBound.y, borderBound.x + srcBound.x, borderBound.y + srcBound.y);
		}
	}

	/**
	 * @param param
	 */
	private void changeRender(VariationParam param) {
		if (CallLogic.getInstance().getVoipStatus() == CallStatus.STATUS_CLOSE) {
			Log.d(TAG, "call has close.");
			return;
		}
		if (null == getChildAt(0)) {
			Log.d(TAG, "render is null.");
			return;
		}
		CallManager callManager = eSpaceService.getService().callManager;
		if (null == callManager) {
			Log.e(TAG, "callManager is Null");
			return;
		}
		if (getChildAt(0) == VideoHandler.getIns().getRemoteCallView()) {
			callManager.getTupManager().controlRemoteRender(param.scale, param.transX, param.transY, false);
		} else if (getChildAt(0) == VideoHandler.getIns().getRemoteBfcpView()) {
			callManager.getTupManager().controlRemoteRender(param.scale, param.transX, param.transY, true);
		}
	}

	/**
	 * UI线程run方法 主要用于 缓慢停止滑动
	 */
	@Override
	public void run() {
		// 滚动还未结束则继续滚动
		if (!scroller.isFinished()) {
			scroller.computeScrollOffset();
			int currX = scroller.getCurrX();
			int currY = scroller.getCurrY();
			scrollX += currX - scrollLastX;
			scrollY += currY - scrollLastY;
			scrollLastX = currX;
			scrollLastY = currY;
			requestLayoutRenderDelay();
			post(this);
		}
	}

	/**
	 * 手指按下时调用事件
	 * 
	 * @param e
	 *            事件
	 * @return 事件被消耗返回true，否则false
	 */
	@Override
	public boolean onDown(MotionEvent e) {
		// 用户按下手指则强行停止滚动
		scroller.forceFinished(true);
		return true;
	}

	/**
	 * 手势按下，迅速移动后松开调用的事件
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (isScrollDisabled) {
			return true;
		}
		// 滚动边界
		Rect bounds = getScrollBounds(renderRect);

		scrollLastX = 0;
		scrollLastY = 0;

		// 页面被拖离边界时平滑弹回边界内
		Rect expandedBounds = new Rect(bounds);
		expandedBounds.inset(-FLING_MARGIN, -FLING_MARGIN);
		if (withinBoundsInDirectionOfTravel(bounds, velocityX, velocityY) && expandedBounds.contains(0, 0)) {
			scroller.fling(0, 0, parseFloatToInt(velocityX), parseFloatToInt(velocityY), bounds.left, bounds.right, bounds.top, bounds.bottom);
			post(this);
		}

		return true;
	}

	/**
	 * 滚动事件
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (isScrollDisabled) {
			return true;
		}

		scrollX -= distanceX;
		scrollY -= distanceY;
		requestLayoutRenderDelay();
		// 滑动事件
		if (null != renderRect && (renderRect.left > borderBound.x || renderRect.right < srcBound.x + borderBound.x)) {
			setReaderMoveListener(e1, e2, distanceX);
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 设置缩放事件
		scaleGestureDetector.onTouchEvent(event);

		// 缩放时忽视手势滑动事件
		if (!isScaling) {
			gestureDetector.onTouchEvent(event);
		}

		int action = event.getActionMasked();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			clickX = parseFloatToInt(event.getX());
			clickY = parseFloatToInt(event.getY());
			break;
		// case MotionEvent.ACTION_MOVE:
		// break;
		case MotionEvent.ACTION_UP:
			isScrollDisabled = false;
			// end add by cwx176935 reason: 每一次down动作发生时菜单栏就显示，感觉不好
			if (scroller.isFinished()) {
				slideView(renderRect);
			}

			float moveX = event.getX() - clickX;
			float moveY = event.getY() - clickY;
			if (Math.abs(moveY) <= 10 && Math.abs(moveX) <= 10) {
				// 在手势抬起的时候判断，如果不是放大或滑动，此事件当做点击
				if (!isScaling) {
					// 此时当做点击事件
					setReaderClickListener();
				}
			}

			if (null != renderRect && (renderRect.left > borderBound.x || renderRect.right < srcBound.x + borderBound.x)) {
				setReaderMoveListener(event, event, 0);
			}
			break;
		default:
			break;
		}

		return true;
	}

	/**
	 * 位置校准
	 */
	private Point getCorrection(Rect bounds) {
		return new Point(Math.min(Math.max(0, bounds.left), bounds.right), Math.min(Math.max(0, bounds.top), bounds.bottom));
	}

	/**
	 * 对视图进行滑动
	 */
	private void slideView(Rect rect) {
		// 校准
		Point correction = getCorrection(getScrollBounds(rect));
		// 开始滚动
		scrollLastX = 0;
		scrollLastY = 0;
		scroller.startScroll(0, 0, correction.x, correction.y, SCROLL_DURATION);
		post(this);
	}

	/**
	 * 获取滚动界限
	 */
	private Rect getScrollBounds(Rect rect) {
		return getScrollBounds(rect.left + scrollX, rect.top + scrollY, rect.left + rect.width() + scrollX, rect.top + rect.height() + scrollY);
	}

	/**
	 * 获取滚动界限
	 */
	private Rect getScrollBounds(int left, int top, int right, int bottom) {
		int minX = getWidth() - right + borderBound.x;
		int maxX = borderBound.x - left;
		int minY = getHeight() - bottom + borderBound.y;
		int maxY = borderBound.y - top;
		if (minX > maxX) {
			minX = (minX + maxX) / 2;
			maxX = (minX + maxX) / 2;
		}
		if (minY > maxY) {
			minY = (minY + maxY) / 2;
			maxY = (minY + maxY) / 2;
		}

		return new Rect(minX, minY, maxX, maxY);
	}

	/**
	 * 单指抬起时的事件
	 * 
	 * @param e
	 *            事件
	 * @return 事件被消耗返回true，否则false
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/**
	 * 缩放事件
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		if (renderRect == null) {
			return true;
		}

		// 缩放不能超出最大最小的限制范围
		float preScale = curScale;
		curScale = Math.min(Math.max(curScale * detector.getScaleFactor(), MIN_SCALE), MAX_SCALE);
		float factor = curScale / preScale;
		int focusX = parseFloatToInt(detector.getFocusX()) - (scrollX + renderRect.centerX()) + borderBound.x;
		int focusY = parseFloatToInt(detector.getFocusY()) - (scrollY + renderRect.centerY()) + borderBound.y;

		scrollX += parseFloatToInt(focusX - focusX * factor);
		scrollY += parseFloatToInt(focusY - focusY * factor);

		scaleRender();
		requestLayoutRenderDelay();
		return true;
	}

	/**
	 * 缩放矩形
	 */
	private void scaleRender() {
		/********************* beign缩放矩形 *********************************/
		/* 将原始矩形缩放后 按中心点位置平移 */
		int scaledWidth = parseFloatToInt(srcBound.x * curScale);
		int scaledHeight = parseFloatToInt(srcBound.y * curScale);
		int centerX = renderRect.centerX();
		int centerY = renderRect.centerY();
		renderRect.set(0, 0, scaledWidth, scaledHeight);
		renderRect.offset(centerX - renderRect.centerX(), centerY - renderRect.centerY());
		/*********************** end缩放矩形 *******************************/
	}

	private void requestLayoutRenderDelay() {
		// postDelayed(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		requestLayoutRender();
		// }
		// }, 10);
	}

	/**
	 * 更新内部视图
	 */
	private void requestLayoutRender() {
		View childView = this.getChildAt(0);
		if (null == childView || 0 == getMeasuredWidth()) {
			return;
		}
		renderRect.offset(scrollX, scrollY);
		/* 平移公式: 当前移动距离*当前缩放比率*2/当前矩形大小 */
		float fTransX = (scrollX * curScale * 2 / renderRect.width());
		float fTransY = (scrollY * curScale * 2 / renderRect.height());
		curTransX += fTransX;
		curTransY -= fTransY;
		final VariationParam param = new VariationParam(curScale, curTransX, curTransY);
		changeRender(param);
		scrollX = 0;
		scrollY = 0;
	}

	/**
	 * 重置数据
	 */
	public void resetData() {
		scrollX = 0;
		scrollY = 0;
		scrollLastX = 0;
		scrollLastY = 0;
		isScrollDisabled = false;
		isScaling = false;
		curTransX = 0;
		curTransY = 0;
		curScale = 1f;
		if (null != srcBound) {
			renderRect.set(borderBound.x, borderBound.y, borderBound.x + srcBound.x, borderBound.y + srcBound.y);
		}
		requestLayoutRender();
	}

	/**
	 * 
	 * @param left
	 * @param top
	 */
	public void setRenderRect(int left, int top) {
		if (null == renderRect || null == srcBound) {
			Log.d(TAG, "the render rect is null.");
			return;
		}
		renderRect.left = left;
		renderRect.top = top;
		if (0 == left || 0 == top) {
			srcBound.x += borderBound.x;
			srcBound.y += borderBound.y;
		} else {
			srcBound.x -= left;
			srcBound.y -= top;
		}
		borderBound.x = left;
		borderBound.y = top;
		renderRect.right = left + srcBound.x;
		renderRect.bottom = top + srcBound.y;
		requestLayoutRenderDelay();
	}

	/**
	 * 缩放开始时事件
	 * 
	 * @param detector
	 *            缩放检测器
	 * @return 事件被消耗返回true，否则false
	 */
	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		isScaling = true;
		isScrollDisabled = true;
		scrollX = 0;
		scrollY = 0;
		return true;
	}

	/**
	 * 缩放结束时事件
	 *
	 * @param detector
	 *            缩放检测器
	 */
	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		isScaling = false;
		isScrollDisabled = false;
		post(this);
	}

	/**
	 * 是否在界限范围内拖动
	 *
	 * @param bounds
	 *            边界
	 * @param velocityX
	 *            x轴速度
	 * @param velocityY
	 *            y轴速度
	 */
	private boolean withinBoundsInDirectionOfTravel(Rect bounds, float velocityX, float velocityY) {
		switch (getMovingDerection(velocityX, velocityY)) {
		case MOVING_DIAGONALLY:
			return bounds.contains(0, 0);
		case MOVING_LEFT:
			return bounds.left <= 0;
		case MOVING_RIGHT:
			return bounds.right >= 0;
		case MOVING_UP:
			return bounds.top <= 0;
		case MOVING_DOWN:
			return bounds.bottom >= 0;
		default:
			throw new NoSuchElementException();
		}
	}

	/**
	 * 获取手势移动方向
	 * 
	 * @param velocityX
	 *            X轴移动速度
	 * @param velocityY
	 *            Y轴移动速度
	 */
	private int getMovingDerection(float velocityX, float velocityY) {
		if (Math.abs(velocityX) > 2 * Math.abs(velocityY)) {
			return velocityX > 0 ? MOVING_RIGHT : MOVING_LEFT;
		} else if (Math.abs(velocityY) > 2 * Math.abs(velocityX)) {
			return velocityY > 0 ? MOVING_DOWN : MOVING_UP;
		} else {
			return MOVING_DIAGONALLY;
		}
	}

	private int parseFloatToInt(float value) {
		return Math.round(value);
	}

	private void setReaderClickListener() {
		if (readerChangeListenerList == null || readerChangeListenerList.size() < 1) {
			return;
		}
		int size = readerChangeListenerList.size();
		for (int i = 0; i < size; i++) {
			readerChangeListenerList.get(i).onReaderClick();
		}
		// for (ReaderChangeListener readerChangeListener :
		// readerChangeListenerList)
		// {
		// readerChangeListener.onReaderClick();
		// }
	}

	private void setReaderMoveListener(MotionEvent e1, MotionEvent e2, float distanceX) {
		if (readerChangeListenerList == null || readerChangeListenerList.size() < 1) {
			return;
		}
		int size = readerChangeListenerList.size();
		for (int i = 0; i < size; i++) {
			readerChangeListenerList.get(i).onReaderMove(e1, e2, distanceX);
		}
		// for (ReaderChangeListener readerChangeListener :
		// readerChangeListenerList)
		// {
		// readerChangeListener.onReaderMove(e1, e2, distanceX);
		// }
	}

	public void regReaderChangeListener(ReaderChangeListener readerChangeListener) {
		if (readerChangeListenerList == null) {
			readerChangeListenerList = new ArrayList<ReaderChangeListener>(10);
		}

		readerChangeListenerList.add(readerChangeListener);
	}

	public void unregReaderChangeListener(ReaderChangeListener readerChangeListener) {
		if (readerChangeListenerList == null || readerChangeListener == null) {
			return;
		}

		readerChangeListenerList.remove(readerChangeListener);
	}

	@Override
	public void addView(View child) {
		// TODO Auto-generated method stub
		super.addView(child);
		if (null == srcBound && 0 != getMeasuredWidth()) {
			srcBound = new Point(getMeasuredWidth(), getMeasuredHeight());
			renderRect = new Rect(0, 0, srcBound.x, srcBound.y);
		}
		resetData();
	}

	@Override
	public void removeView(View view) {
		resetData();
		super.removeView(view);
	}

	@Override
	public void removeViewAt(int index) {
		resetData();
		super.removeViewAt(index);
	}

	@Override
	public void removeAllViews() {
		resetData();
		super.removeAllViews();
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public void onLongPress(MotionEvent e) {

	}
}
