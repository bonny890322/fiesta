package com.network.fiesta

import android.app.ActivityOptions
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_create_location.*


class CreateLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var titlelist = mutableListOf<String>()
    var textlist = mutableListOf<String>()
    var checklist = mutableListOf<Boolean>()
    var latitudelist = mutableListOf<Double>()
    var longitudelist = mutableListOf<Double>()
    var location = ""
    var latitude = 0.0
    var longitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_location)

        val intent = getIntent()
        val authId = intent.getStringExtra("authId")
        val groupName = intent.getStringExtra("groupName")
        val groupId = intent.getStringExtra("groupId")
        val activityname = intent.getStringExtra("activityname")
        val datestart = intent.getStringExtra("datestart")
        val dateend = intent.getStringExtra("dateend")
        val timeStatus = intent.getStringExtra("timeStatus")
        val sort = intent.getStringExtra("sort")
        val people = intent.getStringExtra("people")
        val content = intent.getStringExtra("content")
        val bitmapUri = intent.getStringExtra("bitmapUri")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        et_location.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val temp = et_location.text.toString()
                    var addressList: List<Address>? = null

                    if (temp != null && temp != "") {
                        val geoCoder = Geocoder(this@CreateLocationActivity)
                        try {
                            Thread(Runnable {
                                run{
                                    addressList = geoCoder.getFromLocationName(temp, 1)
                                    if (addressList!!.size != 0) {
                                        location = ""
                                        latitudelist.clear()
                                        longitudelist.clear()
                                        checklist.clear()
                                        titlelist.clear()
                                        textlist.clear()
                                        val address = addressList!![0]
                                        titlelist.add(address.getAddressLine(0))
                                        textlist.add(address.getAddressLine(0))
                                        latitudelist.add(address.latitude)
                                        longitudelist.add(address.longitude)
                                        checklist.add(false)
                                        val temp = API(this@CreateLocationActivity).nearbySearch(address.longitude.toString(),address.latitude.toString())
                                        if(temp.getJSONObject("result").getString("status")=="OK"){
                                            val places = temp.getJSONObject("result").getJSONArray("results")
                                            for (i in 0..places.length()-1){
                                                try {
                                                    val name = places.getJSONObject(i).getString("name")
                                                    val vicinity = places.getJSONObject(i).getString("vicinity")
                                                    val lat = places.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat").toDouble()
                                                    val lng = places.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng").toDouble()
                                                    titlelist.add(name)
                                                    textlist.add(vicinity)
                                                    latitudelist.add(lat)
                                                    longitudelist.add(lng)
                                                    checklist.add(false)
                                                }catch (e:Exception){ }
                                            }
                                        }
                                        runOnUiThread {
                                            view.layoutManager = LinearLayoutManager(this@CreateLocationActivity)
                                            view.adapter = CreateLocationAdapter(titlelist, textlist, checklist, ::click)
                                        }
                                    } else {
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@CreateLocationActivity,
                                                "輸入的地址有誤或不精確\n請在試一次",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }).start()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@CreateLocationActivity,
                                "發生預期外的錯誤",
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }

                    }
                    return true
                }
                return false
            }
        })

        btn_next.setOnClickListener {
            try {
                if (location != "") {
                    val intent = Intent(this, CreateDetailActivity::class.java)
                    intent.putExtra("authId", authId)
                        .putExtra("groupName", groupName)
                        .putExtra("datestart", datestart)
                        .putExtra("activityname", activityname)
                        .putExtra("dateend", dateend)
                        .putExtra("groupId", groupId)
                        .putExtra("location", location)
                        .putExtra("latitude", latitude.toString())
                        .putExtra("longitude", longitude.toString())
                        .putExtra("timeStatus", timeStatus)
                        .putExtra("sort", sort)
                        .putExtra("content",content)
                        .putExtra("people",people)
                    if (bitmapUri != "") {
                        intent.putExtra("bitmapUri", bitmapUri)
                    }
                    val options = ActivityOptions.makeCustomAnimation(this,R.anim.slide_out_left,R.anim.slide_in_right)
                    startActivity(intent, options.toBundle())
                } else {
                    Toast.makeText(this, "請填入地址", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "輸入的地址有誤或不精確\n請在試一次", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun click(position: Int) {
        for (i in 0..checklist.size-1) {
            checklist.set(i, false)
        }
        checklist.set(position, true)
        view.adapter!!.notifyDataSetChanged()
        latitude = latitudelist[position]
        longitude = longitudelist[position]
        location = textlist[position]
        val latLng = LatLng(latitude, longitude)
        mMap!!.clear()
        mMap!!.addMarker(MarkerOptions().position(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val location = LatLng(25.105497, 121.597366)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

}
