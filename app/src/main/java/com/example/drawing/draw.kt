package com.example.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class draw(context: Context, attar: AttributeSet) : View(context, attar) {
//الارم
//بيج ديسمل ماث

    private var mDrawpath: CustomPath? = null
    private var mcanvasbit: Bitmap? = null
    private var mDrawingpaint: Paint? = null
    private var mcanvasPaint: Paint? = null
    private var mbrushsize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null

    private val path = ArrayList<CustomPath>()
    private val undopath=ArrayList<CustomPath>()

    init {
        setUPDrawing()
    }
    fun undo()
    {
        if (path.size>0)
        undopath.add(path.removeAt(path.size-1))
        invalidate()

    }

    private fun setUPDrawing() {
        mDrawingpaint = Paint()
        mDrawpath = CustomPath(color, mbrushsize)
        mDrawingpaint!!.color = color
        mDrawingpaint!!.style = Paint.Style.STROKE
        mDrawingpaint!!.strokeJoin = Paint.Join.ROUND
        mDrawingpaint!!.strokeCap = Paint.Cap.ROUND
        mcanvasPaint = Paint(Paint.DITHER_FLAG)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mcanvasbit = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)//256 color
        canvas = Canvas(mcanvasbit!!)


    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mcanvasbit!!, 0f, 0f, mcanvasPaint)
        for (mpath in path) {
            mDrawingpaint!!.strokeWidth = mpath.brushthickness
            mDrawingpaint!!.color = mpath.color
            canvas.drawPath(mpath, mDrawingpaint!!)
        }
        if (!mDrawpath!!.isEmpty) {
            mDrawingpaint!!.strokeWidth  =  mDrawpath!!.brushthickness
            mDrawingpaint!!.color = mDrawpath!!.color
            canvas.drawPath(mDrawpath!!, mDrawingpaint!!)

        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touch1 = event?.x
        val touch2 = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawpath!!.color = color
                mDrawpath!!.brushthickness = mbrushsize
                mDrawpath!!.reset()
                if (touch1 != null) {
                    if (touch2 != null) {
                        mDrawpath!!.moveTo(touch1, touch2)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touch2 != null) {
                    if (touch1 != null) {
                        mDrawpath!!.lineTo(touch1, touch2)
                    }
                }

            }
            MotionEvent.ACTION_UP -> {
                path.add(mDrawpath!!)
                mDrawpath = CustomPath(color, mbrushsize)
            }

            else -> return false


        }
        invalidate()
        return true

    }

    fun brushsize(size: Float) {
        mbrushsize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            size, resources.displayMetrics
        )
        mDrawingpaint!!.strokeWidth = mbrushsize


    }
fun color(newcolor:String){
    color=Color.parseColor(newcolor)
    mDrawingpaint!!.color=color



}

    internal inner class CustomPath(var color: Int, var brushthickness: Float) : Path() {

    }


}