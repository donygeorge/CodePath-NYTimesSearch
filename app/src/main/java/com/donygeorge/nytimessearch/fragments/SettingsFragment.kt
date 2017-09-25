package com.donygeorge.nytimessearch.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.models.Filter
import com.donygeorge.nytimessearch.models.NewsDesk
import com.donygeorge.nytimessearch.models.SortOrder
import kotlinx.android.synthetic.main.activity_filter.*
import org.parceler.Parcels
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : DialogFragment() , DatePickerDialog.OnDateSetListener {

    var mFilter : Filter? = null
    var mDate : Date? = null

    public interface SettingsFragmentListener {
        fun onSettingsSaved(filter : Filter)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFilter = Parcels.unwrap<Filter>(arguments.getParcelable<Parcelable>("filter"))
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
        newFragment.setTargetFragment(this, 300)
        newFragment.show(fragmentManager, "datePicker");
    }

    private fun onSave() {
        val date = mDate
        val sortOrder = SortOrder.values().get(spSortOrder.selectedItemPosition)
        val desks = mutableListOf<NewsDesk>()
        if (cbArts.isChecked) desks.add(NewsDesk.ARTS)
        if (cbFashion.isChecked) desks.add(NewsDesk.FASHION_AND_STYLE)
        if (cbSports.isChecked) desks.add(NewsDesk.SPORTS)
        val filter = Filter(date, sortOrder, desks)
        val listener = activity as SettingsFragmentListener
        listener.onSettingsSaved(filter)
        dismiss()
    }

    companion object {
        fun newInstance(filter : Filter?) : SettingsFragment {
            val fragment = SettingsFragment()

	    	val bundle = Bundle();
            bundle.putParcelable("filter", Parcels.wrap(filter))
            fragment.arguments = bundle

            return fragment
        }
    }
}
