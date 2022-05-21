package com.network.fiesta

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_edit_map.*
import java.util.*

class EditMapActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    var latitude: Double = 25.105497
    var longitude: Double = 121.597366
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_map)

        val intent = getIntent()
        val Id = intent.getStringExtra("Id")
        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        val Data = API(this.applicationContext,token).getActivityById(Id).getJSONArray("result").getJSONObject(0)
        try {
            latitude = Data.getString("Latitude").toDouble()
            longitude = Data.getString("Longitude").toDouble()
        }catch (e: Exception){ }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        et_location.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    lateinit var location: String
                    location = et_location.text.toString()
                    var addressList: List<Address>? = null

                    if (location != null || location != "") {
                        val geoCoder = Geocoder(this@EditMapActivity)
                        try {
                            addressList = geoCoder.getFromLocationName(location, 1)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if(addressList!!.size != 0) {
                            val address = addressList!![0]
                            latitude = address.latitude
                            longitude = address.longitude
                            val latLng = LatLng(latitude, longitude)
                            mMap!!.clear()
                            mMap!!.addMarker(MarkerOptions().position(latLng))
                            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                        }else{
                            Toast.makeText(this@EditMapActivity,"輸入的地址似乎有誤\n請在試一次", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return true
                }
                return false
            }
        })

        btn_done.setOnClickListener {
            Thread(Runnable {
                run{
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1).first().getAddressLine(0)
                    API(this.applicationContext,token).updateActivity(Id,Location = addresses, Latitude = latitude.toString(), Longitude = longitude.toString())
                    finish()
                }
            }).start()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val location = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(location))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,14f))
    }
}
