package com.network.fiesta

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    lateinit var manager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        menuSetup()
    }

    //把 Drawer 的 Listener 定義好
    fun menuSetup() {
        manager = supportFragmentManager
        //呼叫HomeMenu.kt 回傳主畫面後顯示
        manager.beginTransaction().add(R.id.changemenu, SideMenuHome()).commit()
        menu_side.setNavigationItemSelectedListener(this)

        // 設定用戶資料
        val header = this.menu_side.getHeaderView(0)
        val Data = getSharedPreferences("Data", Context.MODE_PRIVATE)
        header.findViewById<TextView>(R.id.tv_userName).text = Data.getString("nickName", "點此進行登入")
        header.findViewById<TextView>(R.id.tv_userId).text = "@" + Data.getString("userId", "")
        val Photo = Data.getString("Photo", "None")
        if(Photo != "None"){
            Thread(Runnable {
                run{
                    try {
                        val bitmap = API(this.applicationContext).SearchIMG(Photo!!)
                        runOnUiThread {
                            header.findViewById<ImageView>(R.id.img).setImageBitmap(bitmap)
                        }
                    }catch (e:Exception){
                        runOnUiThread {
                            header.findViewById<ImageView>(R.id.img).setImageResource(R.drawable.ui_fiestalogo)
                        }
                    }
                }
            }).start()
        }
        //設定MenuHeader 跳轉至個人資料
        if(header.findViewById<TextView>(R.id.tv_userName).text == "點此進行登入") {
            header.findViewById<TextView>(R.id.tv_userName).setOnClickListener {
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }else{
            this.menu_side.getHeaderView(0).setOnClickListener {
                val intent = Intent(this, SideMenuPersonal::class.java)
                startActivity(intent)
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            this.menu_side.getHeaderView(0).findViewById<ImageView>(R.id.img_bell) .setOnClickListener {
                val intent = Intent(this, RemindActivity::class.java)
                startActivity(intent)
            }
        }
    }

    //Drawer 的 Listener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (this.menu_side.getHeaderView(0).findViewById<TextView>(R.id.tv_userName).text == "點此進行登入") {
            Toast.makeText(this,"請先登入",Toast.LENGTH_SHORT).show()
        } else {
            when (item.itemId) {
                R.id.menu_home -> {
                    manager.beginTransaction().replace(R.id.changemenu, SideMenuHome()).commit()
                }
                R.id.menu_create -> {
                    val intent = Intent(this, SideMenuCreate::class.java)
                    startActivity(intent)
                }
                R.id.menu_edit -> {
                    val intent = Intent(this, SideMenuEdit::class.java)
                    startActivity(intent)
                }
                R.id.menu_group -> {
                    val intent = Intent(this, SideMenuGroup::class.java)
                    startActivity(intent)
                }
                R.id.menu_ticket -> {
                    val intent = Intent(this, SideMenuTicket::class.java)
                    startActivity(intent)
                }
                R.id.menu_setting -> {
                    val intent = Intent(this, SideMenuSetting::class.java)
                    startActivity(intent)
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //返回鍵被按下
    override fun onBackPressed() {
        //如果Drawer開啟就收回
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            AlertDialog.Builder(this)
                .setTitle("確定要離開嗎")
                .setNegativeButton("否",null)
                .setPositiveButton("是"){dialog, which ->
                    dialog.cancel()
                    super.onBackPressed()
                }.show()
        }
    }

}