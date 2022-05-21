package com.network.fiesta

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import org.json.JSONObject


class EditTicketPagerAdapter(val Parent: Activity, val ticket_kinds: ArrayList<String>, val totalArray: MutableList<JSONObject>,val click:(authId: String, nickName: String, ticketKinds: String, ticketStatus: String, Notes: String, Mail: String) -> Unit,val click_delete:(authId: String) -> Unit, val click_switch:(authId: String, isChecked: Boolean, TicketStatus: String, switch_check: Switch) -> Unit ): PagerAdapter() {
    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

    override fun getCount(): Int {
        return ticket_kinds.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.row_recycleview, container, false)

        val recycleview = view.findViewById<RecyclerView>(R.id.recycleview)
        recycleview.layoutManager = LinearLayoutManager(Parent, RecyclerView.VERTICAL, false)

        if(totalArray.size != 0){
            recycleview.adapter = EditTicketStatusAdapter(Parent,position,ticket_kinds,totalArray,::click1,::click2,::click3)
        }else{
            recycleview.adapter = TextAdapter("尚無人參加")
        }

        /*
        if(position == 0){
            if(totalArray.size != 0){
                recycleview.adapter = EditTicketStatusAdapter(Parent,totalArray,::click2)
            }else{
                recycleview.adapter = TextAdapter("尚無人參加")
            }
        }else if(position == 1){

            if(normalArray.size != 0){
                recycleview.adapter = EditTicketStatusAdapter(Parent,normalArray,::click2)
            }else{
                recycleview.adapter = TextAdapter("尚無人參加")
            }
        }else if(position == 2){
            if(studentArray.size != 0){
                recycleview.adapter = EditTicketStatusAdapter(Parent,studentArray,::click2)
            }else{
                recycleview.adapter = TextAdapter("尚無人參加")
            }
        }
        */

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }

    fun click1(authId: String, nickName: String, ticketKinds: String, ticketStatus: String, Notes: String, Mail :String){
        click(authId, nickName, ticketKinds, ticketStatus, Notes, Mail)
    }

    fun click2(authId: String){
        click_delete(authId)
    }

    fun click3(authId: String, isChecked: Boolean, TicketStatus: String, switch_check: Switch){
        click_switch(authId, isChecked, TicketStatus, switch_check)
    }

}