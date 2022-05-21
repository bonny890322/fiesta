package com.network.fiesta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_ticket_detail.*

class TicketDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_detail)

        val aspter = FragmentAdapter(getSupportFragmentManager())
//        aspter.addFragment(TicketDetailFragment())
//        aspter.addFragment(TicketShowFragment())
//        ticketView.adapter = aspter
//
//        ticketView.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
//        tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(ticketView))

    }
}
