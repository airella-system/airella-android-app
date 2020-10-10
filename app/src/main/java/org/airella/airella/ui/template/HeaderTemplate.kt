package org.airella.airella.ui.template

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.header_template.view.*
import org.airella.airella.R

class HeaderTemplate(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    init {
        inflate(context, R.layout.header_template, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.HeaderTemplate)
        image.setImageDrawable(attributes.getDrawable(R.styleable.HeaderTemplate_image))
        title.text = attributes.getString(R.styleable.HeaderTemplate_text)
        attributes.recycle()

    }
}