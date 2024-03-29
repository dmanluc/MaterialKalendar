package com.uxsmobile.materialkalendar.presentation.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.uxsmobile.library.R
import com.uxsmobile.materialkalendar.app.shouldShowAllDates
import com.uxsmobile.materialkalendar.app.shouldShowDefaultDates
import com.uxsmobile.materialkalendar.app.shouldShowNonCurrentMonths
import com.uxsmobile.materialkalendar.app.shouldShowOutOfCalendarRangeDates
import com.uxsmobile.materialkalendar.data.KalendarDay
import com.uxsmobile.materialkalendar.data.KalendarDayViewData
import com.uxsmobile.materialkalendar.presentation.ui.common.formatter.DateFormatter
import com.uxsmobile.materialkalendar.presentation.ui.common.formatter.KalendarDayDateFormatter
import kotlinx.android.synthetic.main.view_calendar_day.view.dayMovementsBarChart
import kotlinx.android.synthetic.main.view_calendar_day.view.dayNumber

/**
 * @author   Daniel Manrique Lucas <daniel.manrique@uxsmobile.com>
 * @version  1
 * @since    16/10/2018.
 *
 * Copyright © 2018 UXS Mobile. All rights reserved.
 */
internal class KalendarDayView
@JvmOverloads constructor(context: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private val colorResources = listOf(R.color.bar_chart_expenses_type,
                                        R.color.bar_chart_incomes_type,
                                        R.color.bar_chart_expected_type)

    lateinit var day: KalendarDay
        private set

    private val checkedDayTypeface: Typeface
    private val notCheckedDayTypeface: Typeface

    private var formatter: DateFormatter<KalendarDay> = KalendarDayDateFormatter()
    var dayStatus: Triple<Boolean, Boolean, Boolean> = Triple(true, true, false)
        private set
    private var colorPalette = emptyList<Int>()


    init {
        View.inflate(context, R.layout.view_calendar_day, this)

        colorPalette = colorResources.map { ContextCompat.getColor(context, it) }
        checkedDayTypeface = Typeface.createFromAsset(context.applicationContext.assets,
                                                      "fonts/CalibreApp-Semibold.ttf")
        notCheckedDayTypeface = Typeface.createFromAsset(context.applicationContext.assets, "fonts/CalibreApp-Thin.ttf")

        setupBarChart()
    }

    constructor(context: Context, day: KalendarDay) : this(context) {
        setDayNumber(day)
    }

    fun setDayFormatter(formatter: DateFormatter<KalendarDay>) {
        this.formatter = formatter
        setDayNumber(this.day)
    }

    fun setupDayShowingMode(flagsMode: Int, inRange: Boolean, inMonth: Boolean) {
        var dayShouldBeEnabled = inMonth && inRange
        var shouldApplyGrayScaleColorScheme = false

        if (flagsMode.shouldShowAllDates()) {
            dayShouldBeEnabled = true
        } else {
            if (!inMonth && inRange && flagsMode.shouldShowNonCurrentMonths()) dayShouldBeEnabled = true

            if (!inRange && flagsMode.shouldShowOutOfCalendarRangeDates()) dayShouldBeEnabled = dayShouldBeEnabled or inMonth

            if (flagsMode.shouldShowDefaultDates()) dayShouldBeEnabled = dayShouldBeEnabled.or(inMonth && inRange)

            shouldApplyGrayScaleColorScheme = (!inMonth || (inMonth && !inRange)) && dayShouldBeEnabled
            if (shouldApplyGrayScaleColorScheme) {
                dayNumber.setTextColor(Color.GRAY)
            }
        }

        dayStatus = Triple(dayShouldBeEnabled, shouldApplyGrayScaleColorScheme, dayStatus.third)

        visibility = if (dayShouldBeEnabled) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    fun applyBarChartData(dataSet: KalendarDayViewData) {
        dayMovementsBarChart.apply {
            data = BarData().apply {
                addDataSet(BarDataSet(
                        dataSet.barChartValues.mapIndexed { index, value -> BarEntry(index.toFloat(), value) },
                        "").apply {
                    barWidth = .9f
                    colors = if (dayStatus.second) listOf(Color.GRAY, Color.GRAY, Color.GRAY) else colorPalette
                    setDrawValues(false)
                })
            }
            visibility = View.VISIBLE
            animateY(500, Easing.EaseOutBounce)
        }
    }

    private fun setDayNumber(day: KalendarDay) {
        this.day = day

        dayNumber.apply {
            text = formatter.format(this@KalendarDayView.day)
            typeface = if (dayStatus.third) checkedDayTypeface else notCheckedDayTypeface
        }
    }

    fun setCheckedDay(checked: Boolean) {
        dayNumber.apply {
            typeface = if (checked) {
                checkedDayTypeface
            } else {
                notCheckedDayTypeface
            }
        }
        dayStatus = Triple(dayStatus.first, dayStatus.second, checked)
    }

    private fun setupBarChart() {
        dayMovementsBarChart.apply {
            setFitBars(true)
            setDrawBorders(false)
            setDrawGridBackground(false)
            setScaleEnabled(false)
            setTouchEnabled(false)
            isDragEnabled = false
            setPinchZoom(false)
            axisLeft.setDrawLabels(false)
            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.spaceTop = 0f
            axisLeft.axisMaximum = 1f
            axisLeft.granularity = .01f
            axisLeft.axisMinimum = 0f
            axisRight.setDrawLabels(false)
            axisRight.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            xAxis.setDrawLabels(false)
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            legend.isEnabled = false
            description.isEnabled = false
            setViewPortOffsets(0f, 0f, 0f, 0f)
        }
    }

}