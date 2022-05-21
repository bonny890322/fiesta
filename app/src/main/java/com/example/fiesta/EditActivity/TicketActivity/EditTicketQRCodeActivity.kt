package com.network.fiesta

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_edit_ticket_qrcode.*
import kotlinx.android.synthetic.main.dialog_check.view.*
import org.json.JSONObject
import java.lang.Exception

class EditTicketQRCodeActivity : AppCompatActivity() {

    private lateinit var detector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private var check = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ticket_qrcode)

        val token = getSharedPreferences("Data", MODE_PRIVATE).getString("token", "token")!!
        detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}
            @SuppressLint("MissingPermission")
            override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
                val barcodes = p0?.detectedItems
                if (barcodes!!.size() > 0) {
                    if (check) {
                        check = false
                        runOnUiThread {
                            img_loading.visibility = View.VISIBLE
                            progress_Bar.visibility = View.VISIBLE
                        }
                        try {
                            val temp1 = JSONObject(barcodes.valueAt(0).displayValue)
                            val temp2 = API(this@EditTicketQRCodeActivity.applicationContext,token).QRCodeCheck(
                                temp1.getString("act_Id"),
                                temp1.getString("authId")
                            )
                            if (temp2.getString("code") == "001") {
                                runOnUiThread {
                                    img_loading.visibility = View.GONE
                                    progress_Bar.visibility = View.GONE
                                    val result = temp2.getJSONArray("result").getJSONObject(0)
                                    val myDialog =
                                        LayoutInflater.from(this@EditTicketQRCodeActivity)
                                            .inflate(R.layout.dialog_check, null)
                                    val d = AlertDialog.Builder(this@EditTicketQRCodeActivity)
                                        .setView(myDialog)
                                        .setTitle(title)
                                    val myAlertDialog = d.show()
                                    if (result.getString("ticketKinds") == "null") {
                                        myDialog.tv_ticketKinds.setText("一般票")
                                    } else {
                                        myDialog.tv_ticketKinds.setText(result.getString("ticketKinds"))
                                    }
                                    myDialog.tv_status.setText("驗票成功")
                                    myDialog.tv_nickName.setText(result.getString("nickName"))
                                    myDialog.et_Notes.setText(result.getString("Notes"))
                                    myDialog.btn.setOnClickListener {
                                        check = true
                                        myAlertDialog.dismiss()
                                    }
                                }
                            } else if (temp2.getString("code") == "010") {
                                runOnUiThread {
                                    img_loading.visibility = View.GONE
                                    progress_Bar.visibility = View.GONE
                                    val myDialog =
                                        LayoutInflater.from(this@EditTicketQRCodeActivity)
                                            .inflate(R.layout.dialog_check, null)
                                    val d = AlertDialog.Builder(this@EditTicketQRCodeActivity)
                                        .setView(myDialog)
                                        .setTitle(title)
                                    val myAlertDialog = d.show()
                                    myDialog.tv_status.setText("驗票失敗")
                                    myDialog.tv_substatus.visibility = View.VISIBLE
                                    myDialog.tv_nickName.visibility = View.GONE
                                    myDialog.tv_ticketKinds.visibility = View.GONE
                                    myDialog.et_Notes.visibility = View.GONE
                                    myDialog.temp1.visibility = View.GONE
                                    myDialog.temp2.visibility = View.GONE
                                    myDialog.temp3.visibility = View.GONE
                                    myDialog.btn.setOnClickListener {
                                        check = true
                                        myAlertDialog.dismiss()
                                    }
                                }
                            } else {
                                runOnUiThread {
                                    img_loading.visibility = View.GONE
                                    progress_Bar.visibility = View.GONE
                                    val myDialog =
                                        LayoutInflater.from(this@EditTicketQRCodeActivity)
                                            .inflate(R.layout.dialog_check, null)
                                    val d = AlertDialog.Builder(this@EditTicketQRCodeActivity)
                                        .setView(myDialog)
                                        .setTitle(title)
                                    val myAlertDialog = d.show()
                                    myDialog.tv_status.setText("驗票失敗")
                                    myDialog.tv_status.visibility = View.GONE
                                    myDialog.tv_nickName.visibility = View.GONE
                                    myDialog.tv_ticketKinds.visibility = View.GONE
                                    myDialog.et_Notes.visibility = View.GONE
                                    myDialog.temp1.visibility = View.GONE
                                    myDialog.temp2.visibility = View.GONE
                                    myDialog.temp3.visibility = View.GONE
                                    myDialog.btn.setOnClickListener {
                                        check = true
                                        myAlertDialog.dismiss()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                img_loading.visibility = View.GONE
                                progress_Bar.visibility = View.GONE
                                Toast.makeText(
                                    this@EditTicketQRCodeActivity,
                                    "發生未知錯誤",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Thread.sleep(2000)
                        }
                    } else {
                        Thread.sleep(2000)
                    }
                }
            }
        })
        cameraSource = CameraSource.Builder(this, detector).setRequestedPreviewSize(1024, 768)
            .setRequestedFps(30f).setAutoFocusEnabled(true).build()
        sv_barcode.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ContextCompat.checkSelfPermission(
                        this@EditTicketQRCodeActivity,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    cameraSource.start(holder)
                } else {
                    ActivityCompat.requestPermissions(
                        this@EditTicketQRCodeActivity,
                        arrayOf(android.Manifest.permission.CAMERA),
                        123
                    )
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(sv_barcode.holder)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detector.release()
        cameraSource.stop()
        cameraSource.release()
    }
}
