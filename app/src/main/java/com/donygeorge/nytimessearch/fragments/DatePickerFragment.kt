package com.donygeorge.nytimessearch.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment() {

    var mDate : Date? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val c = Calendar.getInstance();
        if (mDate != null) c.time = mDate
        val year = c.get(Calendar.YEAR);
        val month = c.get(Calendar.MONTH);
        val day = c.get(Calendar.DAY_OF_MONTH);

        val listener = getActivity() as DatePickerDialog.OnDateSetListener
        return DatePickerDialog(getActivity(), listener, year, month, day)
    }

    fun setDate(date : Date?) {
        mDate = date
    }
}