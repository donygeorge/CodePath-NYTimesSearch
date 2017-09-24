package com.donygeorge.nytimessearch.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.DatePicker
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.fragments.DatePickerFragment
import com.donygeorge.nytimessearch.models.Filter
import com.donygeorge.nytimessearch.models.NewsDesk
import com.donygeorge.nytimessearch.models.SortOrder
import kotlinx.android.synthetic.main.activity_filter.*
import org.parceler.Parcels
import java.text.SimpleDateFormat
import java.util.*


class FilterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    var mFilter : Filter? = null
    var mDate : Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        supportActionBar?.title = "Search settings"
        mFilter = Parcels.unwrap<Filter>(intent.getParcelableExtra("filter"))
        mDate = mFilter?.date
        if (mDate == null) {
            // Use last year's date
            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR, -1)
            mDate = cal.time
        }

        setDate(mDate!!)
        setSortOrder(mFilter?.sortOrder)
        setDesks(mFilter?.newsDesks)
        tvDate.setOnClickListener { showDatePickerDialog() }
        btSave.setOnClickListener { onSave() }
    }

    override fun onDateSet(picker : DatePicker?, year : Int, month : Int, day : Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        mDate = c.time
        setDate(mDate!!)
    }

    private fun setDate(date : Date) {
        var dateFormat = SimpleDateFormat("MMM dd, yyyy");
        tvDate.text = dateFormat.format(date)
    }

    private fun setSortOrder(sortOrder : SortOrder?) {
        if (sortOrder == null) return

        spSortOrder.setSelection(sortOrder.ordinal)
    }

    private fun setDesks(desks : List<NewsDesk>?) {
        cbArts.isChecked = false
        cbFashion.isChecked = false
        cbSports.isChecked = false

        if (desks == null) return

        for (desk in desks) {
           when (desk) {
               NewsDesk.ARTS -> cbArts.isChecked = true
               NewsDesk.FASHION_AND_STYLE -> cbFashion.isChecked = true
               NewsDesk.SPORTS -> cbSports.isChecked = true
           }
        }
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePickerFragment()
        newFragment.setDate(mDate)
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private fun onSave() {
        val date = mDate
        val sortOrder = SortOrder.values().get(spSortOrder.selectedItemPosition)
        val desks = mutableListOf<NewsDesk>()
        if (cbArts.isChecked) desks.add(NewsDesk.ARTS)
        if (cbFashion.isChecked) desks.add(NewsDesk.FASHION_AND_STYLE)
        if (cbSports.isChecked) desks.add(NewsDesk.SPORTS)
        val filter = Filter(date, sortOrder, desks)
        val intent = Intent()
        intent.putExtra("filter", Parcels.wrap(filter));
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
