/*
 * Copyright 2016 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.priyesh.chroma

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import me.priyesh.chroma.internal.ChannelView
import me.priyesh.chroma.internal.ColorView

class ChromaView : LinearLayout {

  var currentColor: Int
  val colorModel: ColorModel

  constructor(initialColor: Int, colorModel: ColorModel, context: Context) : super(context) {
    this.currentColor = initialColor
    this.colorModel = colorModel
    init()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ChromaView, 0, 0)

    try {
      this.currentColor = typedArray.getColor(R.styleable.ChromaView_initialColor, Color.GRAY)
      this.colorModel = ColorModel.valueOf(
          typedArray.getString(R.styleable.ChromaView_colorMode)
              ?: ColorModel.RGB.toString())
    } finally {
      typedArray.recycle()
    }

    init()
  }

  private fun init() {
    orientation = VERTICAL
    clipToPadding = false

    val colorView = ColorView(currentColor, context)
    addView(colorView)

    val channelViews = colorModel.channels.map { ChannelView(it, currentColor, context) }

    val seekbarChangeListener: () -> Unit = {
      currentColor = colorModel.evaluateColor(channelViews.map { it.channel })
      colorView.setColor(currentColor)
    }

    channelViews.forEach { it ->
      addView(it)

      val layoutParams = it.layoutParams as LinearLayout.LayoutParams
      layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.channel_view_margin_top)
      layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.channel_view_margin_bottom)

      it.registerListener(seekbarChangeListener)
    }
  }
}