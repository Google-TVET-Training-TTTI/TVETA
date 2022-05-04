package com.ttti.voting.ui

import android.content.Context
import androidx.appcompat.widget.AppCompatEditText
import android.graphics.Typeface
import android.util.AttributeSet

/**
 * Created by Martin Mundia.
 */
class MyEditText : AppCompatEditText {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?) : super(context!!) {
        init()
    }

    private fun init() {
        if (!isInEditMode) {
            val tf = Typeface.createFromAsset(context.assets, "Lato-Regular.ttf")
            typeface = tf
        }
    }
}