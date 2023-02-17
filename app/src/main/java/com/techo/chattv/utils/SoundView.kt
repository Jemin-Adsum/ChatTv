package com.techo.chattv.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.techo.chattv.R

class SoundView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var paint: Paint? = null
    private var emptyrectf: RectF? = null
    private val fillrectf: RectF? = null
    private var currentprogess = 0
    var maxprogess = 100
        private set
    private var soundProgressChangeListner: SoundProgressChangeListner? = null
    private var noofpart = 0f
    private var viewbackgroundcolor: Int
    private var mRoundedCorners: Int
    private var progesscolor: Int
    private var currentblockprogesscolor = 0
    private var stepcolor = 0
    var progress: Int = 0
        get() = currentprogess

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.style = Paint.Style.FILL
        paint!!.color = Color.RED
    }

    fun setOnsoundProgressChangeListner(soundProgressChangeListner: SoundProgressChangeListner?) {
        this.soundProgressChangeListner = soundProgressChangeListner
    }

    override fun onDraw(canvas: Canvas) {
        if (emptyrectf == null) emptyrectf = RectF(0F, 0F, width.toFloat(), height.toFloat())
        val clipPath = Path()
        clipPath.addRoundRect(
            emptyrectf!!,
            mRoundedCorners.toFloat(),
            mRoundedCorners.toFloat(),
            Path.Direction.CW
        )
        canvas.clipPath(clipPath)
        paint!!.color = viewbackgroundcolor
        canvas.drawRoundRect(
            emptyrectf!!, mRoundedCorners.toFloat(), mRoundedCorners.toFloat(),
            paint!!
        )
        paint!!.color = progesscolor
        val currentprog = currentprogess * height / maxprogess
        canvas.drawRoundRect(
            0f, height.toFloat(), width.toFloat(), (height - currentprog).toFloat(), 0f, 0f,
            paint!!
        )
        super.onDraw(canvas)
    }

    @JvmName("setProgress1")
    fun setProgress(currentprogess: Int) {
        this.currentprogess = currentprogess
        invalidate()
        soundProgressChangeListner!!.onchange(this.currentprogess)
    }

    fun setMaxprogress(maxprogess: Int) {
        this.maxprogess = maxprogess
        invalidate()
    }

    init {
        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.SoundView)
        mRoundedCorners = ta.getDimensionPixelSize(R.styleable.SoundView_cornerRadius, 0)
        currentprogess = ta.getInteger(R.styleable.SoundView_progress, 0)
        maxprogess = ta.getInteger(R.styleable.SoundView_maxprogress, 100)
        viewbackgroundcolor = ta.getColor(R.styleable.SoundView_viewbackgroundcolor, Color.GRAY)
        progesscolor = ta.getColor(R.styleable.SoundView_progesscolor, Color.parseColor("#FBC429"))
    }
}