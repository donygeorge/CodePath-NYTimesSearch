package com.donygeorge.nytimessearch.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.models.Filter
import org.parceler.Parcels



class FilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        supportActionBar?.title = "Search settings"
        val filter = Parcels.unwrap<Filter>(intent.getParcelableExtra("filter"))

    }

}
