package com.flayone.trapezoidbuttons

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


/**
 * Created by liyayu on 2018/11/9.
 * 不规则双梯形按钮  形状如下
 *  ______________ ___________
 * |             //           |
 * |   button1  //   button2  |
 * |___________//_____________|
 */
class IrregularButton : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private var mStokePaint: Paint = Paint()
    //按钮描边内侧画笔（用来画未选中stoke描边以内的背景色）
    private var mLeftInnerPath: Path = Path()
    private var mRightInnerPath: Path = Path()
    //按钮背景色路径（用来画stoke描边效果，或者选中效果)
    private var mLeftBackGroundPath: Path = Path()
    private var mRightBackGroundPath: Path = Path()
    //左侧选中背景画笔
    private var mLeftSelectedBackGroundPaint: Paint = Paint()
    //右侧选中背景画笔
    private var mRightSelectedBackGroundPaint: Paint = Paint()
    //未选中背景画笔
    private var mUnselectedBackGroundPaint: Paint = Paint()
    // 画字体的画笔
    private var textPaint: Paint = Paint()
    private var textPaintSelected: Paint = Paint()

    private var shortWidth = 80f
    private var longWidth = 100f
    private var widthCaps = 20f
    private var caps = 8f
    private var stoke = 4f

    private var totalWidth = 188f
    private var totalHeight = 30f
    private var mTextSize = 20f
    private var sharpeStokeWidth = 0f//梯形尖角处描边的x方向值，需计算来保证斜边宽度一致
    private var wideStokeWidth = 0f//梯形广角处描边的x方向值，需计算来保证斜边宽度一致
    private var mLeftText = "左侧"
    private var mRightText = "右侧"
    private var textDeviationSize = 10f //字体位置偏移量

    private var isLeftSelect = true //左侧选中与否
    private var canSelect = true//是否可以选则
    private var selectListener: BaseBooleanListener? = null
    private lateinit var ta: TypedArray
    //抗锯齿设置
    private val mSetfil = PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG)

    private fun init(context: Context, attrs: AttributeSet?) {
        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.IrregularButton)
            widthCaps = ta.getDimension(R.styleable.IrregularButton_widthCaps, 10f)
            caps = ta.getDimension(R.styleable.IrregularButton_caps, 2f)
            stoke = ta.getDimension(R.styleable.IrregularButton_stoke, 1f)
            isLeftSelect = ta.getBoolean(R.styleable.IrregularButton_isLeftSelect, true)
            mTextSize = ta.getDimension(R.styleable.IrregularButton_buttonTextSize, 14f)
            mLeftText = ta.getString(R.styleable.IrregularButton_leftText)
            mRightText = ta.getString(R.styleable.IrregularButton_rightText)
        } catch (e: Exception) {
            Log.e("IrregularButton", "获取资源失败")
        } finally {
            ta.recycle()
        }
        textDeviationSize = widthCaps / 6 //根据梯形上下边长度差修改字体偏差距离
        mStokePaint.run {
            isAntiAlias = true
            color = resources.getColor(R.color.gray2)
            style = Paint.Style.FILL_AND_STROKE
        }
        mLeftSelectedBackGroundPaint.run {
            isAntiAlias = true
            color = resources.getColor(R.color.orange)
            style = Paint.Style.FILL_AND_STROKE
        }
        mRightSelectedBackGroundPaint.run {
            isAntiAlias = true
            color = resources.getColor(R.color.blue_button)
            style = Paint.Style.FILL_AND_STROKE
        }

        mUnselectedBackGroundPaint.run {
            isAntiAlias = true
            color = resources.getColor(R.color.gray)
            style = Paint.Style.FILL_AND_STROKE
        }

        textPaint.run {
            color = resources.getColor(R.color.black_text)
            textSize = mTextSize
        }
        textPaintSelected.run {
            color = resources.getColor(R.color.white)
            textSize = mTextSize
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        totalWidth = measuredWidth.toFloat()
        totalHeight = measuredHeight.toFloat()
        /**
         * 实现描边的重要部分！！！这里保证了斜边宽度和其他边一致
         * 设控件高度为 totalHeight = A ,梯形宽度差 widthCaps =B, 描边宽度 stoke = x，
         * 那么想要计算 尖角处描边的x方向值sharpeStokeWidth = r 可以分两段计算分别是r1 r2
         * 直角长边为 l = sqrt(A*A + B*B);r1 = (B*x)/A ; r2 = (l * x)/A,
         * 所以 r = r1+r2 = ((B + l)*x)/A  wideStokeWidth = s = (A * x)/(B + l)
         */
        val l = Math.sqrt((totalHeight * totalHeight).toDouble() + (widthCaps * widthCaps).toDouble())
        sharpeStokeWidth = (((widthCaps + l) * stoke) / totalHeight).toFloat()
        wideStokeWidth = (totalHeight * stoke) / (l + widthCaps).toFloat()

        //根据控件宽度和缝隙大小计算按钮上下梯形宽度
        longWidth = (totalWidth - caps + widthCaps) / 2
        shortWidth = (totalWidth - caps - widthCaps) / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawFilter = mSetfil
        mLeftBackGroundPath.run {
            moveTo(0f, 0f)
            lineTo(longWidth, 0f)
            lineTo(shortWidth, totalHeight)
            lineTo(0f, totalHeight)
            close()
        }
        mLeftInnerPath.run {
            moveTo(stoke, stoke)
            lineTo(longWidth - sharpeStokeWidth, stoke)
            lineTo(shortWidth - wideStokeWidth, totalHeight - stoke)
            lineTo(stoke, totalHeight - stoke)
            close()
        }
        mRightBackGroundPath.run {
            moveTo(longWidth + caps, 0f)
            lineTo(shortWidth + longWidth + caps, 0f)
            lineTo(shortWidth + longWidth + caps, totalHeight)
            lineTo(shortWidth + caps, totalHeight)
            close()
        }
        mRightInnerPath.run {
            moveTo(longWidth + caps + wideStokeWidth, stoke)
            lineTo(shortWidth + longWidth + caps - stoke, stoke)
            lineTo(shortWidth + longWidth + caps - stoke, totalHeight - stoke)
            lineTo(shortWidth + caps + sharpeStokeWidth, totalHeight - stoke)
            close()
        }
        //设置抗锯齿
        mStokePaint.isAntiAlias = true
        mLeftSelectedBackGroundPaint.isAntiAlias = true
        mRightSelectedBackGroundPaint.isAntiAlias = true
        mUnselectedBackGroundPaint.isAntiAlias = true
        textPaint.isAntiAlias = true
        textPaintSelected.isAntiAlias = true

        val fm = textPaint.fontMetrics
        val textHeight = fm.descent + Math.abs(fm.ascent)
        val leftTextPaint: Paint
        val rightTextPaint: Paint

        if (isLeftSelect) { //如果左侧选中
            canvas.drawPath(mLeftBackGroundPath, mLeftSelectedBackGroundPaint) //画出左侧选中
            canvas.drawPath(mRightBackGroundPath, mStokePaint) // 画出右侧描边用背景
            canvas.drawPath(mRightInnerPath, mUnselectedBackGroundPaint) //画出右侧内部未选中背景

            leftTextPaint = textPaintSelected
            rightTextPaint = textPaint
        } else { //同理翻转
            canvas.drawPath(mRightBackGroundPath, mRightSelectedBackGroundPaint)
            canvas.drawPath(mLeftBackGroundPath, mStokePaint)
            canvas.drawPath(mLeftInnerPath, mUnselectedBackGroundPaint)


            leftTextPaint = textPaint
            rightTextPaint = textPaintSelected
        }
        //画出左右按钮字体，居中再向中偏移textDeviationSize个像素
        canvas.drawText(mLeftText, shortWidth / 2 - textPaint.measureText(mLeftText) / 2 + textDeviationSize, height / 2 + textHeight / 4, leftTextPaint)
        canvas.drawText(mRightText, shortWidth / 2 + longWidth + caps - textPaint.measureText(mRightText) / 2 - textDeviationSize, height / 2 + textHeight / 4, rightTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (canSelect) {// 是否可以做选择操作
            val x = event.x
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val isLeftContains = isRegionContainPoint(mLeftBackGroundPath, x, y)
                    val isRightContains = isRegionContainPoint(mRightBackGroundPath, x, y)
                    if (isLeftContains) {
                        selectListener?.isLeftClick(true)
                    }
                    if (isRightContains) {
                        selectListener?.isLeftClick(false)
                    }
                    if (isLeftSelect != isLeftContains && isLeftSelect == isRightContains) {//只有状态不同时才去刷新
                        setLeftSelected(!isLeftSelect)
                    }
                }
            }
        }
        return true
    }

    //判断点是否在闭合path中
    private fun isRegionContainPoint(path: Path, x: Float, y: Float): Boolean {
        val rectF = RectF()
        val mRegion = Region()
        path.computeBounds(rectF, true)
        mRegion.setPath(path, Region(rectF.left.toInt(), rectF.top.toInt(),
                rectF.right.toInt(), rectF.bottom.toInt()))
        return mRegion.contains(x.toInt(), y.toInt())
    }

    fun setLeftSelected(b: Boolean) {
        isLeftSelect = b
        invalidate()//刷新
    }

    fun setOnSelectedListener(listener: BaseBooleanListener) {
        selectListener = listener
    }

    interface BaseBooleanListener {
        fun isLeftClick(i: Boolean)
    }
}