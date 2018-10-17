package com.uxsmobile.materialkalendar.ui.common

import com.uxsmobile.materialkalendar.data.KalendarDay

/**
 * @author   Daniel Manrique Lucas <daniel.manrique@uxsmobile.com>
 * @version  1
 * @since    15/10/2018.
 *
 * Copyright © 2018 UXS Mobile. All rights reserved.
 */
interface DateRangeIndex {

    /**
     * Count of pages displayed between 2 dates.
     */
    val count: Int

    /**
     * Index of the page where the date is displayed.
     */
    fun indexOf(day: KalendarDay): Int

    /**
     * Get the first date at the position within the index.
     */
    fun getItem(position: Int): KalendarDay

}