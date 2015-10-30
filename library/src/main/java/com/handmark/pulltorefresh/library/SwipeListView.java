
package com.handmark.pulltorefresh.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class SwipeListView extends ListView {
    private Boolean mIsHorizontal;

    private View mPreItemView;

    private View mCurrentItemView;

    private float mFirstX;

    private float mFirstY;

    private int mRightViewWidth = 300;

    // private boolean mIsInAnimation = false;
    private final int mDuration = 100;

    private final int mDurationStep = 10;

    private boolean mIsShown;

    /**
     * ?????????璁?footer or Header Swipe
     */
    private boolean mIsFooterCanSwipe = false;

    private boolean mIsHeaderCanSwipe = false;

    public SwipeListView(Context context) {
        super(context);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * return true, deliver to listView. return false, deliver to child. if
     * move, return true
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsHorizontal = null;
                mFirstX = lastX;
                mFirstY = lastY;
                int motionPosition = pointToPosition((int)mFirstX, (int)mFirstY);

                if (motionPosition >= 0) {
                    View currentItemView = getChildAt(motionPosition - getFirstVisiblePosition());
                    mPreItemView = mCurrentItemView;
                    mCurrentItemView = currentItemView;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;

                if (Math.abs(dx) >= 5 && Math.abs(dy) >= 5) {
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsShown && (mPreItemView != mCurrentItemView || isHitCurItemLeft(lastX))) {
                    System.out.println("1---> hiddenRight");
                    /**
                     * ?????典??锛?
                     * <p>
                     * 涓?涓?Item?????宠竟甯?灞?宸茬????剧ず锛?
                     * <p>
                     * 杩???跺????瑰?讳换???涓?涓?item, ??ｄ????ｄ釜??宠竟甯?灞???剧ず???item????????跺?宠竟甯?灞?
                     */
                    hiddenRight(mPreItemView);
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private boolean isHitCurItemLeft(float x) {
        return x < getWidth() - mRightViewWidth;
    }

    /**
     * @param dx
     * @param dy
     * @return judge if can judge scroll direction
     */
    private boolean judgeScrollDirection(float dx, float dy) {
        boolean canJudge = true;

        if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
            mIsHorizontal = true;
        } else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
            mIsHorizontal = false;
        } else {
            canJudge = false;
        }

        return canJudge;
    }

    /**
     * @param posX
     * @param posY
     * @return judge if can footer judge
     */
    private boolean judgeFooterView(float posX, float posY) {
        // if footer can swipe
        if (mIsFooterCanSwipe) {
            return true;
        }
        // footer cannot swipe
        int selectPos = pointToPosition((int)posX, (int)posY);
        if (selectPos >= (getCount() - getFooterViewsCount())) {
            // is footer ,can not swipe
            return false;
        }
        // not footer can swipe
        return true;
    }

    /**
     * @param posX
     * @param posY
     * @return judge if can judge scroll direction
     */
    private boolean judgeHeaderView(float posX, float posY) {
        // if header can swipe
        if (mIsHeaderCanSwipe) {
            return true;
        }
        // header cannot swipe
        int selectPos = pointToPosition((int)posX, (int)posY);
        if (selectPos >= 0 && selectPos < getHeaderViewsCount()) {
            // is header ,can not swipe
            return false;
        }
        // not header can swipe
        return true;
    }

    /**
     * return false, can't move any direction. return true, cant't move
     * vertical, can move horizontal. return super.onTouchEvent(ev), can move
     * both.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float lastX = ev.getX();
        float lastY = ev.getY();
        // test footer and header
        if (!judgeFooterView(mFirstX, mFirstY) || !judgeHeaderView(mFirstX, mFirstY)) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = lastX - mFirstX;
                float dy = lastY - mFirstY;
                // confirm is scroll direction
                if (mIsHorizontal == null) {
                    if (!judgeScrollDirection(dx, dy)) {
                        break;
                    }
                }

                if (mIsHorizontal) {
                    if (mIsShown && mPreItemView != mCurrentItemView) {
                        /**
                         * ?????典??锛?
                         * <p>
                         * 涓?涓?Item?????宠竟甯?灞?宸茬????剧ず锛?
                         * <p>
                         * 杩???跺??宸???虫????ㄥ??澶?涓?涓?item,??ｄ釜??宠竟甯?灞???剧ず???item????????跺?宠竟甯?灞?
                         * <p>
                         * ???宸?婊???ㄥ??瑙????璇ユ????碉???????虫????ㄨ??浼?瑙?????????典??
                         */
                        hiddenRight(mPreItemView);
                    }

                    if (mIsShown && mPreItemView == mCurrentItemView) {
                        dx = dx - mRightViewWidth;
                    }

                    // can't move beyond boundary
                    if (dx < 0 && dx > -mRightViewWidth) {
                        mCurrentItemView.scrollTo((int)(-dx), 0);
                    }

                    return true;
                } else {
                    if (mIsShown) {
                        /**
                         * ?????典??锛?
                         * <p>
                         * 涓?涓?Item?????宠竟甯?灞?宸茬????剧ず锛?
                         * <p>
                         * 杩???跺??涓?涓?婊????ListView,??ｄ????ｄ釜??宠竟甯?灞???剧ず???item????????跺?宠竟甯?灞?
                         */
                        hiddenRight(mPreItemView);
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                clearPressedState();
                if (mIsShown) {
                    /**
                     * ?????靛??锛?
                     * <p>
                     * 涓?涓?Item?????宠竟甯?灞?宸茬????剧ず锛?
                     * <p>
                     * 杩???跺??宸???虫????ㄥ?????涓?涓?item,??ｄ釜??宠竟甯?灞???剧ず???item????????跺?宠竟甯?灞?
                     */
                    hiddenRight(mPreItemView);
                }

                if (mIsHorizontal != null && mIsHorizontal) {
                    if (mFirstX - lastX > mRightViewWidth / 2) {
                        showRight(mCurrentItemView);
                    } else {
                        /**
                         * ?????典??锛?
                         * <p>
                         * ?????虫????ㄤ??涓?item,涓?婊???ㄧ??璺?绂昏??杩?浜???宠竟View???瀹藉害???涓????锛???????涔????
                         */
                        hiddenRight(mCurrentItemView);
                    }
                    MotionEvent obtain = MotionEvent.obtain(ev);
				    obtain.setAction(MotionEvent.ACTION_CANCEL);
				    super.onTouchEvent(obtain);
				    return true;
                }

                break;
        }

        return super.onTouchEvent(ev);
    }

    private void clearPressedState() {
        // TODO current item is still has background, issue
        mCurrentItemView.setPressed(false);
        setPressed(false);
        refreshDrawableState();
        // invalidate();
    }

    private void showRight(View view) {
        Message msg = new MoveHandler().obtainMessage();
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = mRightViewWidth;
        msg.sendToTarget();

        mIsShown = true;
    }

    private void hiddenRight(View view) {
        if (mCurrentItemView == null) {
            return;
        }
        Message msg = new MoveHandler().obtainMessage();//
        msg.obj = view;
        msg.arg1 = view.getScrollX();
        msg.arg2 = 0;

        msg.sendToTarget();

        mIsShown = false;
    }

    /**
     * show or hide right layout animation
     */
    @SuppressLint("HandlerLeak")
    class MoveHandler extends Handler {
        int stepX = 0;

        int fromX;

        int toX;

        View view;

        private boolean mIsInAnimation = false;

        private void animatioOver() {
            mIsInAnimation = false;
            stepX = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (stepX == 0) {
                if (mIsInAnimation) {
                    return;
                }
                mIsInAnimation = true;
                view = (View)msg.obj;
                fromX = msg.arg1;
                toX = msg.arg2;
                stepX = (int)((toX - fromX) * mDurationStep * 1.0 / mDuration);
                if (stepX < 0 && stepX > -1) {
                    stepX = -1;
                } else if (stepX > 0 && stepX < 1) {
                    stepX = 1;
                }
                if (Math.abs(toX - fromX) < 10) {
                    view.scrollTo(toX, 0);
                    animatioOver();
                    return;
                }
            }

            fromX += stepX;
            boolean isLastStep = (stepX > 0 && fromX > toX) || (stepX < 0 && fromX < toX);
            if (isLastStep) {
                fromX = toX;
            }

            view.scrollTo(fromX, 0);
            invalidate();

            if (!isLastStep) {
                this.sendEmptyMessageDelayed(0, mDurationStep);
            } else {
                animatioOver();
            }
        }
    }

    public int getRightViewWidth() {
        return mRightViewWidth;
    }

    public void setRightViewWidth(int mRightViewWidth) {
        this.mRightViewWidth = mRightViewWidth;
    }

    /**
     * 璁剧疆list??????????????藉??swipe
     * 
     * @param canSwipe
     */
    public void setFooterViewCanSwipe(boolean canSwipe) {
        mIsFooterCanSwipe = canSwipe;
    }

    /**
     * 璁剧疆list???澶存???????藉??swipe
     * 
     * @param canSwipe
     */
    public void setHeaderViewCanSwipe(boolean canSwipe) {
        mIsHeaderCanSwipe = canSwipe;
    }

    /**
     * 璁剧疆 footer and header can swipe
     * 
     * @param footer
     * @param header
     */
    public void setFooterAndHeaderCanSwipe(boolean footer, boolean header) {
        mIsHeaderCanSwipe = header;
        mIsFooterCanSwipe = footer;
    }
}
