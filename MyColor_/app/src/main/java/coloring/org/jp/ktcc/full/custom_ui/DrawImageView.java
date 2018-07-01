package coloring.org.jp.ktcc.full.custom_ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by anh.trinh on 12/8/2017.
 */

public class DrawImageView extends AppCompatImageView {
    private float x = 0;
    private float y = 0;

    public float getSizeEraser() {
        return sizeEraser;
    }

    public void setSizeEraser(float size) {
        this.sizeEraser = size / (saveScale * firstScale);
    }

    private float sizeEraser;

    Bitmap bitmap;
    Path circlePath;
    Bitmap bp;
    Canvas bitmapCanvas;

    private final Paint paint = new Paint();
    private final Paint eraserPaint = new Paint();
    Matrix matrix;
    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    public boolean isEraser() {
        return isEraser;
    }

    public void setIsEraser(boolean eraser) {
        isEraser = eraser;
    }

    private boolean isEraser = false;
    private boolean isDoing = false;

    public boolean isUsePen() {
        return isUsePen;
    }

    public void setUsePen(boolean usePen) {
        isUsePen = usePen;
    }

    private boolean isUsePen = false;


    public boolean isSemiEraser() {
        return isSemiEraser;
    }

    public void setSemiEraser(boolean semiEraser) {
        isSemiEraser = semiEraser;
    }

    private boolean isSemiEraser = false;


    int mode = NONE;

    // Remember some things for zooming
    PointF last = new PointF();
    PointF start = new PointF();
    float minScale = 0f;
    float maxScale = 3f;
    float[] m;
    int viewWidth, viewHeight;

    static final int CLICK = 3;

    public float getSaveScale() {
        return saveScale;
    }

    public void setSaveScale(float saveScale) {
        this.saveScale = saveScale;
    }

    float saveScale = 1f;
    float firstScale = 1f;
    float redundantYSpace = 1f;
    float redundantXSpace = 1f;
    protected float origWidth, origHeight;

    int oldMeasuredWidth, oldMeasuredHeight;

    ScaleGestureDetector mScaleDetector;

    Context context;

    public interface DrawImageViewListener {
        void onTouchDown(float x, float y);

        void onEraserDone();

        void onSemiAuto(float x, float y);
    }

    DrawImageViewListener mDrawImageViewListener;

    public void setDrawImageViewListener(DrawImageViewListener listener) {
        this.mDrawImageViewListener = listener;
    }


    public DrawImageView(Context context) {
        super(context);
        sharedConstructing(context);
    }

    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public DrawImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructing(context);
    }

    private void sharedConstructing(Context context) {

        super.setClickable(true);

        this.context = context;

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        matrix = new Matrix();

        m = new float[9];

        setImageMatrix(matrix);

        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                PointF curr = new PointF(event.getX(), event.getY());
                if (isUsePen) {
                    if (isEraser || isSemiEraser) {
                        if (isEraser) {
                            startClear(event.getX(), event.getY(), event);


                        } else {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                if (mDrawImageViewListener != null) {
                                    mDrawImageViewListener.onSemiAuto(curr.x, curr.y);
                                }
                            }

                        }
                    }
                } else {
                    mScaleDetector.onTouchEvent(event);
                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            if (mDrawImageViewListener != null) {
                                mDrawImageViewListener.onTouchDown(curr.x, curr.y);
                            }
                            last.set(curr);

                            start.set(last);

                            mode = DRAG;

                            break;

                        case MotionEvent.ACTION_MOVE:

                            if (mode == DRAG) {

                                float deltaX = curr.x - last.x;

                                float deltaY = curr.y - last.y;

                                float fixTransX = getFixDragTrans(deltaX, viewWidth, origWidth * saveScale);

                                float fixTransY = getFixDragTrans(deltaY, viewHeight, origHeight * saveScale);

                                matrix.postTranslate(fixTransX, fixTransY);

                                fixTrans();

                                last.set(curr.x, curr.y);
                            }
                            break;

                        case MotionEvent.ACTION_UP:

                            mode = NONE;

                            int xDiff = (int) Math.abs(curr.x - start.x);

                            int yDiff = (int) Math.abs(curr.y - start.y);

                            if (xDiff < CLICK && yDiff < CLICK)

                                performClick();

                            break;

                        case MotionEvent.ACTION_POINTER_UP:

                            mode = NONE;

                            break;

                    }

                    setImageMatrix(matrix);

                    invalidate();
                }

                return true; // indicate event was handled

                //  }
            }

        });
    }

    private static MotionEvent.PointerCoords[] getPointerCoords(MotionEvent e) {
        int n = e.getPointerCount();
        MotionEvent.PointerCoords[] r = new MotionEvent.PointerCoords[n];
        for (int i = 0; i < n; i++) {
            r[i] = new MotionEvent.PointerCoords();
            e.getPointerCoords(i, r[i]);
        }
        return r;
    }

    public void setMaxZoom(float x) {

        maxScale = x;

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            mode = ZOOM;

            return true;

        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float mScaleFactor = detector.getScaleFactor();

            float origScale = saveScale;

            saveScale *= mScaleFactor;

            if (saveScale > maxScale) {

                saveScale = maxScale;

                mScaleFactor = maxScale / origScale;

            } else if (saveScale < minScale) {

                saveScale = minScale;

                mScaleFactor = minScale / origScale;

            }

            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight)

                matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2, viewHeight / 2);

            else

                matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());

            fixTrans();

            return true;

        }

    }

    void fixTrans() {

        matrix.getValues(m);

        float transX = m[Matrix.MTRANS_X];

        float transY = m[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);

        float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale);

        if (fixTransX != 0 || fixTransY != 0)

            matrix.postTranslate(fixTransX, fixTransY);

    }


    float getFixTrans(float trans, float viewSize, float contentSize) {

        float minTrans, maxTrans;

        if (contentSize <= viewSize) {

            minTrans = 0;

            maxTrans = viewSize - contentSize;

        } else {

            minTrans = viewSize - contentSize;

            maxTrans = 0;

        }

        if (trans < minTrans)

            return -trans + minTrans;

        if (trans > maxTrans)

            return -trans + maxTrans;

        return 0;

    }

    float getFixDragTrans(float delta, float viewSize, float contentSize) {

        if (contentSize <= viewSize) {

            return 0;

        }

        return delta;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        viewWidth = MeasureSpec.getSize(widthMeasureSpec);

        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        //
        // Rescales image on rotation
        //
        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight

                || viewWidth == 0 || viewHeight == 0)

            return;

        oldMeasuredHeight = viewHeight;

        oldMeasuredWidth = viewWidth;

        if (saveScale == 1) {

            //Fit to screen.

            //   float scale;

            Drawable drawable = getDrawable();

            if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)

                return;

            int bmWidth = drawable.getIntrinsicWidth();

            int bmHeight = drawable.getIntrinsicHeight();


            float scaleX = (float) viewWidth / (float) bmWidth;

            float scaleY = (float) viewHeight / (float) bmHeight;

            firstScale = Math.min(scaleX, scaleY);

            matrix.setScale(firstScale, firstScale);

            // Center the image

            redundantYSpace = (float) viewHeight - (firstScale * (float) bmHeight);

            redundantXSpace = (float) viewWidth - (firstScale * (float) bmWidth);

            redundantYSpace /= (float) 2;

            redundantXSpace /= (float) 2;

            matrix.postTranslate(redundantXSpace, redundantYSpace);

            origWidth = viewWidth - 2 * redundantXSpace;

            origHeight = viewHeight - 2 * redundantYSpace;

            setImageMatrix(matrix);

        }

        fixTrans();

    }

    private void initCavan() {
        if (bp == null)
            return;
        setFocusable(true);
        setFocusableInTouchMode(true);
        bitmap = Bitmap.createBitmap(bp.getWidth(), bp.getHeight(), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas();
        bitmapCanvas.setBitmap(bitmap);
        bitmapCanvas.drawColor(Color.TRANSPARENT);
        bitmapCanvas.drawBitmap(bp, 0, 0, null);

        circlePath = new Path();

        // Set eraser paint properties
        eraserPaint.setAlpha(0);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraserPaint.setAntiAlias(true);


    }

    @Override
    public void onDraw(Canvas canvas) {
        if (bitmap != null && bitmapCanvas != null) {
            canvas.drawBitmap(bitmap, matrix, paint);
        }
    }

    public float[] convertPoint(float x, float y) {
        Matrix myMatrix = new Matrix();
        matrix.invert(myMatrix);
        float[] pt = {x, y};
        myMatrix.mapPoints(pt);
        return pt;
    }

    public void startClear(float x, float y, MotionEvent event) {

        float[] pt = convertPoint(x, y);
        this.x = pt[0];
        this.y = pt[1];
        eraserPaint.setStrokeWidth(this.sizeEraser);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                circlePath.moveTo(this.x, this.y);
                bitmapCanvas.drawPath(circlePath, eraserPaint);
                break;
            case MotionEvent.ACTION_MOVE:
                isDoing = true;
                circlePath.lineTo(this.x, this.y);
                bitmapCanvas.drawPath(circlePath, eraserPaint);
                break;
            case MotionEvent.ACTION_UP:
                circlePath.lineTo(this.x, this.y);
                bitmapCanvas.drawPath(circlePath, eraserPaint);
                circlePath.reset();
                if (mDrawImageViewListener != null && isDoing) {
                        mDrawImageViewListener.onEraserDone();
                        isDoing = false;
                    }
                    break;
            default:

        }
        invalidate();
    }

    public void endClear() {
        circlePath.reset();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setBp(Bitmap bp) {
        this.bp = bp;
        initCavan();
    }

    public Bitmap getDrawBitmap() {
        return bitmap;
    }


}
