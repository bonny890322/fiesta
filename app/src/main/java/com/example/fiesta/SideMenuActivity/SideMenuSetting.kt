package com.network.fiesta

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_menu_setting.*

class SideMenuSetting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_setting)


        tv_facebook.setOnClickListener{
            val uri = Uri.parse("https://www.facebook.com/Fiesta-Network-Taiwan-100614108231891/?ref=py_c");
            val intent = Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent)
        }

        tv_instagram.setOnClickListener{
            val uri = Uri.parse("https://instagram.com/fiesta.network/");
            val intent = Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent)
        }

        tv_privacypolicy.setOnClickListener{
            val intent = Intent(this, SettingPrivacyActivity::class.java)
            startActivity(intent)
        }

        tv_copyright.setOnClickListener {
            val intent = Intent(this, SettingCopyrightActivity::class.java)
            startActivity(intent)
        }
    }

}
