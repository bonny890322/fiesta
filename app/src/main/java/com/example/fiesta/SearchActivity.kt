package com.network.fiesta

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Thread(Runnable {
                    run {
                        if (s.toString()!=""){
                            val temp = API(this@SearchActivity.applicationContext).activitySearch(s.toString())
                            if(temp.getString("code")=="001"){
                                val Data = temp.getJSONArray("result")
                                runOnUiThread {
                                    View.layoutManager = LinearLayoutManager(View.context)
                                    View.adapter = HomeAdapter(Data,this@SearchActivity)
                                }
                            }else{
                                runOnUiThread {
                                    val SearchArray = ArrayList<String>()
                                    val AuthIdArray = ArrayList<String>()
                                    SearchArray.add("查無資料")
                                    AuthIdArray.add("-1")
                                    View.layoutManager = LinearLayoutManager(this@SearchActivity)
                                    View.adapter= TextAdapter("查無資料")
                                }
                            }
                        }else{
                            runOnUiThread{
                                View.layoutManager = LinearLayoutManager(this@SearchActivity)
                                View.adapter= TextAdapter("")
                            }
                        }
                    }
                }).start()
            }
        })
    }
}