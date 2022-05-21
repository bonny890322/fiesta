package com.network.fiesta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.activity_menu_home.*
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.fiesta.Lib.WrapContentLinearLayoutManager
import org.json.JSONArray


class SideMenuHome() : Fragment() {
    val actId = mutableListOf<String>()
    var Data = JSONArray()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_menu_home, container, false )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh()
        scrollView.setOnRefreshListener {
            refresh()
        }

        menuView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            var lastVisibleItem: Int = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastVisibleItem = (recyclerView?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //如果不再最上面就隱藏TAG
                if((recyclerView?.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0){
                    menuTag.visibility = View.VISIBLE
                }else{
                    menuTag.visibility = View.GONE
                }
                //下拉刷新的时候不可以加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 === recyclerView.adapter?.itemCount) {
                    if(scrollView is SwipeRefreshLayout){
                        if(!scrollView.isRefreshing){
                            loadmore()
                        }
                    }else{
                        loadmore()
                    }
                }
            }
        })

        img_search.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }

        img_drawer.setOnClickListener {
            activity!!.drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    private fun loadmore(){
        Thread(Runnable {
            run {
                val temp = API(this.context).getActivity(actId.toString())
                var result = JSONArray()
                if(temp.getString("code")=="001"){
                    result = temp.getJSONArray("result")
                    for (i in 0..result.length() - 1) {
                        Data.put(result.getJSONObject(i))
                    }
                    activity!!.runOnUiThread {
                        for(i in 0..result.length()-1){
                            actId.add(result.getJSONObject(i).getString("Id"))
                        }
                        menuView.adapter!!.notifyDataSetChanged()
                    }
                }else if(temp.getString("code")=="013"){
                    activity!!.runOnUiThread{
                        Toast.makeText(activity,"已經沒有更多活動囉!",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    activity!!.runOnUiThread{
                        menuView.layoutManager = WrapContentLinearLayoutManager(menuView.context)
                        menuView.adapter = TextAdapter("連線逾時")
                        scrollView.isRefreshing = false
                    }
                }

            }
        }).start()
    }

    private fun refresh(){
        Thread(Runnable {
            run{
                val temp1 = API(this.context).getTag()
                val test = API(this.context).SearchIMG("https://imgur.com/iNGnle2.jpg")
                if(temp1.getString("code")=="001"){
                    val Tag = temp1.getJSONArray("result").getJSONArray(0)
                    val Img = JSONArray()
                    for (i in 0..Tag.length()){
                        Img.put(test)
                    }
                    activity!!.runOnUiThread {
                        menuTag.visibility = View.VISIBLE
                        menuTag.layoutManager = WrapContentLinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
                        menuTag.adapter = MenuTagAdapter(Tag,Img)
                    }
                }else{
                    activity!!.runOnUiThread {
                        menuTag.visibility = View.VISIBLE
                        menuTag.adapter = TextAdapter("連線逾時")
                        Toast.makeText(activity,"載入標籤連線逾時",Toast.LENGTH_SHORT).show()
                    }
                }

                val temp2 = API(this.context).getActivity()
                if( temp2.getString("code") == "001"){
                    Data = temp2.getJSONArray("result")
                    actId.clear()
                    activity!!.runOnUiThread {
                        scrollView.isRefreshing = false
                        menuView.layoutManager = WrapContentLinearLayoutManager(menuView.context)
                        menuView.adapter = HomeAdapter(Data, activity!!)
                        for(i in 0..Data.length()-1){
                            actId.add(Data.getJSONObject(i).getString("Id"))
                        }
                    }
                }else if(temp2.getString("code")=="013"){
                    activity!!.runOnUiThread {
                        menuView.layoutManager = WrapContentLinearLayoutManager(menuView.context)
                        menuView.adapter = TextAdapter("目前沒有活動喔!")
                        scrollView.isRefreshing = false
                    }
                }else{
                    activity!!.runOnUiThread {
                        scrollView.isRefreshing=false
                        menuView.layoutManager = WrapContentLinearLayoutManager(menuView.context)
                        menuView.adapter = TextAdapter("連線逾時")
                        Toast.makeText(activity,"連線逾時",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }).start()
    }
}