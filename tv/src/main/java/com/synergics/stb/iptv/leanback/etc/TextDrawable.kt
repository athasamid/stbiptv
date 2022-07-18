package com.synergics.stb.iptv.leanback.etc

import android.graphics.*
import android.graphics.drawable.Drawable

class TextDrawable(private val text: String) : Drawable() {
    private val paint: Paint = Paint()

    override fun draw(canvas: Canvas) {
        canvas.drawText(text, 0F, 0F, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    private val opacity: Int = PixelFormat.TRANSLUCENT

    override fun getOpacity(): Int = opacity

    init {
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.isAntiAlias = true
        paint.isFakeBoldText = true
        paint.setShadowLayer(6f, 0F, 0F, Color.BLACK)
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.LEFT
    }
}