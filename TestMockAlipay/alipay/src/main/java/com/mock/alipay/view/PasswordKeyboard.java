package com.mock.alipay.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;


import com.mock.alipay.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by chenjiawei on 16/10/28.
 */

public class PasswordKeyboard extends GridLayout implements View.OnClickListener, View.OnTouchListener {

    public static final String DEL = "删除";

    public static final String DONE = "OK";
    //因为UED是给的是iPhone设计稿,所以是按照等比的思想设置键盘Key的高度和宽度
    private static final int IPHONE = 779;
    //每个键盘Key的宽度,为屏幕宽度的三分之一
    private int keyWidth = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 3;
    //每个键盘Key的高度
    private int keyHeight = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight() * 59 / IPHONE;

    private int screenWidth = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();

    private Paint mPaint;
    //List集合存储Key,方便每次输错都能再次随机数字键盘
    private final List<Button> keyButtons = new ArrayList<>();

    private WorkHandler mWorkHandler;

    private static final int DELETE = 1;
    //WorkHandler 用于处理长按"删除"Key时,执行重复删除操作。
    private static class WorkHandler extends Handler {

        private int index = 0;

        int diffTime = 100;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DELETE:
                    PasswordKeyboard numberKeyBoard = (PasswordKeyboard) msg.obj;
                    numberKeyBoard.handlerClick(DEL);
                    removeMessages(DELETE);
                    Message message = obtainMessage(DELETE);
                    message.obj = numberKeyBoard;
                    if (diffTime > 40) {
                        diffTime = diffTime - index;
                    }
                    sendMessageDelayed(message, diffTime);
                    index++;
                    break;
            }
        }

        public void reset() {
            index = 0;
            diffTime = 100;
        }
    }

    public PasswordKeyboard(Context context) {
        super(context);
        initView();
    }

    public PasswordKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PasswordKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        setMeasuredDimension(screenWidth, keyHeight * 4);
    }

    //重新设置键盘key位置
    public void resetKeyboard() {
        List<String> keyList = randomKeys(10);
        for (int i = 0; i < keyList.size(); i++) {
            keyButtons.get(i).setText(keyList.get(i));
            keyButtons.get(i).setTag(keyList.get(i));
        }
    }

    private void initView() {
        //必须设置调用该方法,不然onDraw方法不执行。如果ViewGroup没有背景,则其onDraw方法不执行
        setWillNotDraw(false);
        if (getChildCount() > 0) {
            keyButtons.clear();
            removeAllViews();
        }
        //获取随机键盘数字的字符串
        List<String> keyList = randomKeys(10);
        //填充键盘Key,用Button来完成Key功能
        for (int i = 0; i < keyList.size(); i++) {
            Button item = new Button(getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(keyWidth, keyHeight);
            item.setLayoutParams(params);
            item.setOnClickListener(this);
            item.setText(keyList.get(i));
            item.setBackgroundDrawable(getResources().getDrawable(R.drawable.key_selector));
            //监听"删除"的长按监听事件,完成重复删除操作
            if (DEL.equals(keyList.get(i))) {
                item.setOnTouchListener(this);
            }
            item.setTag(keyList.get(i));
            addView(item);
            keyButtons.add(item);
        }
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.parseColor("#cccccc"));
            mPaint.setStrokeWidth(1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制分割线
        canvas.drawLine(0, getMeasuredHeight() / 4, getMeasuredWidth(), getMeasuredHeight() / 4, mPaint);
        canvas.drawLine(0, 2 * getMeasuredHeight() / 4, getMeasuredWidth(), 2 * getMeasuredHeight() / 4, mPaint);
        canvas.drawLine(0, 3 * getMeasuredHeight() / 4, getMeasuredWidth(), 3 * getMeasuredHeight() / 4, mPaint);
        canvas.drawLine(getMeasuredWidth() / 3, 0, getMeasuredWidth() / 3, getMeasuredHeight(), mPaint);
        canvas.drawLine(2 * getMeasuredWidth() / 3, 0, 2 * getMeasuredWidth() / 3, getMeasuredHeight(), mPaint);
    }

    @Override
    public void onClick(View v) {
        String character = v.getTag().toString();
        handlerClick(character);
    }

    private void handlerClick(String character) {
        //密码字符输出回调
        if (mListener != null) {
            if (DONE.equals(character)) {
                mListener.onInput(DONE);
            } else if (DEL.equals(character)) {
                mListener.onInput(DEL);
            } else {
                mListener.onInput(character);
            }
        }
    }

    //生产键盘Key随机数字
    private List<String> randomKeys(int no) {
        int[] keys = new int[no];
        for (int i = 0; i < no; i++) {
            keys[i] = i;
        }
        Random random = new Random();
        for (int i = 0; i < no; i++) {
            int p = random.nextInt(no);
            int tmp = keys[i];
            keys[i] = keys[p];
            keys[p] = tmp;
        }
        List<String> keyList = new ArrayList<>();
        for (int key : keys) {
            keyList.add(String.valueOf(key));
        }
        //将空字符串插入到第10个位置,是个无操作的Key
        keyList.add(9, "");
        //将删除字符串插入最后
        keyList.add(DEL);
        return keyList;
    }

    public void setOnPasswordInputListener(OnPasswordInputListener listener) {
        this.mListener = listener;
    }

    private OnPasswordInputListener mListener;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mWorkHandler == null) {
            mWorkHandler = new WorkHandler();
        }
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            Message msg = mWorkHandler.obtainMessage(DELETE);
            msg.obj = this;
            mWorkHandler.sendMessageDelayed(msg, 500);
        } else if (MotionEvent.ACTION_UP == event.getAction()) {
            mWorkHandler.removeMessages(DELETE);
            mWorkHandler.reset();
        } else if (MotionEvent.ACTION_CANCEL == event.getAction()) {
            mWorkHandler.removeMessages(DELETE);
            mWorkHandler.reset();
        } else if (MotionEvent.ACTION_MOVE == event.getAction()) {

        } else {
            //do nothing
        }
        return false;
    }

    public interface OnPasswordInputListener {
        void onInput(String number);
    }
}
