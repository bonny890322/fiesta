package com.network.fiesta

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import okhttp3.MultipartBody
import android.content.Context
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

class API {
    private lateinit var client: OkHttpClient
    private lateinit var Object: JSONObject
    private lateinit var Array: JSONArray
    private lateinit var String: String
    private var token: String = ""

    constructor(context: Context?){
        client = OkHttpClient().newBuilder().sslSocketFactory(getSslContext(context).socketFactory).build()
    }
    constructor(context: Context?,token: String){
        client = OkHttpClient().newBuilder().sslSocketFactory(getSslContext(context).socketFactory).build()
        this.token = token
    }


    //test
    @Throws(Exception::class)
    fun getSslContext(context: Context?): SSLContext {
        val ks = KeyStore.getInstance(KeyStore.getDefaultType()) // "BKS"
        ks.load(null, null)
        val `is` = context!!.getResources().openRawResource(R.raw.certificate)

        // CertificateFactory
        val cf = CertificateFactory.getInstance("X.509")
        // certificate
        val ca: Certificate
        try {
            ca = cf.generateCertificate(`is`)
        } finally {
            `is`.close()
        }

        ks.setCertificateEntry("my-ca", ca)

        // TrustManagerFactory
        val algorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(algorithm)
        // Create a TrustManager that trusts the CAs in our KeyStore
        tmf.init(ks)

        // Create a SSLContext with the certificate that uses tmf (TrustManager)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.getTrustManagers(), SecureRandom())

        return sslContext
    }





    //拿取會員資料
    fun getUserData(userId: String?, userPassword: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
            .put("userPassword", userPassword)
        POST("http://163.18.42.222:8888/Fiestadb/Account/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //拿取會員信箱狀態
    fun getUserEmailStatus(userId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
        POST("http://163.18.42.222:8888/Fiestadb/Account/getReviewStatus", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //註冊會員
    fun userSingUp(
        userId: String,
        userName: String,
        nickName: String,
        userPassword: String,
        Mail_1: String,
        school: String,
        Phone: String = ""
    ): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
            .put("userId", userId)
            .put("userName", userName)
            .put("nickName",nickName)
            .put("userPassword", userPassword)
            .put("Mail_1", Mail_1)
            .put("school", school)
            .put("Useable", "true")
        if (Phone != "") {
            jsonObject.put("Phone", Phone)
        }
        POST("http://163.18.42.222:8888/Fiestadb/Account/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    // 更新會員資料
    fun userUpdate(
        userId: String?,
        userName: String? = "",
        nickName: String? = "",
        Mail_1: String? = "",
        Phone: String? = "",
        Sex: String? = "",
        Birthday: String? = "",
        Distance: String? = "",
        ID_CARD: String? = ""
    ): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
        if (userName != "") {
            jsonObject.put("userName", userName)
        }
        if (nickName != "") {
            jsonObject.put("nickName", nickName)
        }
        if (Mail_1 != "") {
            jsonObject.put("Mail_1", Mail_1)
        }
        if (Phone != "") {
            jsonObject.put("Phone", Phone)
        }
        if (Sex != "") {
            jsonObject.put("Sex", Sex)
        }
        if (Birthday != "") {
            jsonObject.put("Birthday", Birthday.toString())
        }
        if (Distance != "") {
            jsonObject.put("Distance", Distance)
        }
        if (ID_CARD != "") {
            jsonObject.put("ID_CARD", ID_CARD)
        }
        POST("http://163.18.42.222:8888/Fiestadb/Account/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    fun userTagUpdate(
        userId: String?,
        Tag: String?
    ): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
            .put("Tag", Tag)
        POST("http://163.18.42.222:8888/Fiestadb/Account/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    // 寄出驗證信
    fun mailConfirm(userId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
            .put("type", "1")
        POST("http://163.18.42.222:8888/Fiestadb/Account/SendConfirm", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //忘記密碼
    fun forgetPassword(userId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
            .put("type", "2")
        POST("http://163.18.42.222:8888/Fiestadb/Account/SendConfirm", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //獲取活動評分
    fun getActivityScore(act_Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
        POST("http://163.18.42.222:8888/Fiestadb/FeedBack/Score/Act/SelectByAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //上傳活動評分
    fun uploadActivityScore(act_Id: String?, authId: String?, score_Date: String?, Detail: String? = "", Stars: Float? = -1f, Price: Float? = -1f, Music: Float? = -1f, Flow: Float? = -1f, Vibe: Float? = -1f, Light: Float? = -1f, Moveline: Float? = -1f, Site: Float? = -1f, Staff: Float? = -1f): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId",authId)
            .put("score_Date",score_Date)
            .put("Useable","true")
        if(Detail != ""){
            jsonObject.put("Detail", Detail)
        }
        if (Stars != -1f) {
            jsonObject.put("Stars", Stars)
        }
        if (Price != -1f) {
            jsonObject.put("Price", Price)
        }
        if (Music != -1f) {
            jsonObject.put("Music", Music)
        }
        if (Flow != -1f) {
            jsonObject.put("Flow", Flow)
        }
        if (Vibe != -1f) {
            jsonObject.put("Vibe", Vibe)
        }
        if (Light != -1f) {
            jsonObject.put("Light", Light)
        }
        if (Moveline != -1f) {
            jsonObject.put("Moveline", Moveline)
        }
        if (Site != -1f) {
            jsonObject.put("Site", Site)
        }
        if (Staff != -1f) {
            jsonObject.put("Staff", Staff)
        }
        POST("http://163.18.42.222:8888/Fiestadb/FeedBack/Score/Act/upload", jsonObject, "Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //上傳排程建議
    fun uploadShowScore(act_Id: String?, showId: String?, authId: String?, score_Date: String?, Detail: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id",act_Id)
            .put("showId", showId)
            .put("authId", authId)
            .put("score_Date", score_Date)
            .put("Detail", Detail)
            .put("Useable","true")
        POST("http://163.18.42.222:8888/Fiestadb/FeedBack/Score/Show/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }


    //拿取Show評論
    fun getShowScore(showId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("showId",showId)
        POST("http://163.18.42.222:8888/Fiestadb/FeedBack/Score/Show/SelectByShow", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //獲得個人已評分活動
    fun getActivityScoreByAuth(authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/FeedBack/Score/Act/SelectByAuth", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //新增群組
    fun uploadGroup(authId: String?, groupName: String?, deadline: String? = ""): JSONObject {
        val authIdArray = JSONArray(authId)
        val jsonObject = JSONObject()
            .put("authId", authIdArray)
            .put("groupName", groupName)
            .put("Useable", "true")
        if(deadline!=""){
            jsonObject.put("deadline", deadline)
                .put("timeStatus", "false")
        }else{
            jsonObject.put("timeStatus", "true")
        }
        POST("http://163.18.42.222:8888/Fiestadb/Group/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //查詢群組活動
    fun getGroupActivity(groupId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        POST("http://163.18.42.222:8888/Fiestadb/Group/getAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //查詢群組名稱是否重複
    fun findGroupName(groupName: String): JSONObject {
        val jsonObject = JSONObject()
            .put("groupName", groupName)
        POST("http://163.18.42.222:8888/Fiestadb/Group/FIndName", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //獲取已加入群組
    fun getGroupByUser(authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/Account/getJoinedGroup", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //獲取已加入群組
    fun getGroupByGroupId(groupId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        POST("http://163.18.42.222:8888/Fiestadb/Group/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //更新群組
    fun updateGroup(groupId: String?, Address: String? = "", Mail: String? = "", Phone: String? = "", Photo: String? = "", groupName: String? = "" ,timeStatus: String? = ""): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        if (Address != "") {
            jsonObject.put("Address", Address)
        }
        if (Mail != "") {
            jsonObject.put("Mail", Mail)
        }
        if (Phone != "") {
            jsonObject.put("Phone", Phone)
        }
        if (Photo != "") {
            jsonObject.put("Photo", Photo)
        }
        if (groupName != "") {
            jsonObject.put("groupName", groupName)
        }
        if (timeStatus != "") {
            jsonObject.put("timeStatus", timeStatus)
        }
        POST("http://163.18.42.222:8888/Fiestadb/Group/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //刪除群組
    fun deleteGroup(groupId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        POST("http://163.18.42.222:8888/Fiestadb/Group/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //刪除群組成員
    fun deleteGroupMember(groupId: String?, authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/Group/Member/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //獲取群組成員
    fun selectGroupMember(groupId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        POST("http://163.18.42.222:8888/Fiestadb/Group/Member/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //新增群組成員
    fun addGroupMember(groupId: String?, authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/Group/Member/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }


    //獲取TAG
    fun getTag(): JSONObject {
        GET("http://163.18.42.222:8888/Fiestadb/Tag/select","Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //新增活動
    fun uploadActivity(
        groupId: String?,
        Tag: String?,
        act_Name: String?,
        peopleMaxium: String?,
        Location: String?,
        startTime: String?,
        endTime: String?,
        Price: String?,
        act_Description: String?,
        Latitude: String?,
        Longitude: String?,
        viewStatus: String?
    ): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
            .put("Tag", Tag)
            .put("act_Name", act_Name)
            .put("peopleMaxium", peopleMaxium)
            .put("Location", Location)
            .put("startTime", startTime)
            .put("endTime", endTime)
            .put("Price", Price)
            .put("act_Description", act_Description)
            .put("Latitude", Latitude)
            .put("Longitude", Longitude)
            .put("viewStatus", viewStatus)
            .put("act_Status", "true")
            .put("Useable", "true")
            .put("joinedCount", "0")
        POST("http://163.18.42.222:8888/Fiestadb/Activity/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //獲取推薦活動
    fun getActivity( act_Id: String = "[]"): JSONObject {
        val temp = JSONArray(act_Id)
        val jsonObject = JSONObject()
            .put("act_Id", temp)
        POST("http://163.18.42.222:8888/Fiestadb/Activity/getRecommend", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //刪除活動
    fun deleteActivity(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Id", Id)
        POST("http://163.18.42.222:8888/Fiestadb/Activity/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //獲取指定活動
    fun getActivityById(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Id", Id)
        POST("http://163.18.42.222:8888/Fiestadb/Activity/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }
    //拿取過期活動
    fun getExpActivity():JSONObject{
        val jsonObject = JSONObject()
        POST("http://163.18.42.222:8888/Fiestadb/Activity/getExpire", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //拿取可編輯活動
    fun getEditActivity(authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/Account/getCreateAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //根據TAG拿取活動
    fun getActByTag(Tag: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Tag", Tag)
        POST("http://163.18.42.222:8888/Fiestadb/Activity/getActByTag", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //搜尋活動
    fun activitySearch(Search: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Search", Search)
        POST("http://163.18.42.222:8888/Fiestadb/Activity/Search", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //更新活動資料
    fun updateActivity(
        act_Id: String?,
        Price: String = "",
        Tag: String? = "",
        act_Name: String? = "",
        startTime: String? = "",
        endTime: String? = "",
        act_Description: String? = "",
        peopleMaxium: String? = "",
        Location: String? = "",
        Latitude: String? = "",
        Longitude: String? = "",
        Models: String? = " ",
        viewStatus: String? = ""
    ): JSONObject? {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
        if (Price != "") {
            jsonObject.put("Price", Price)
        }
        if (Tag != "") {
            jsonObject.put("Tag", Tag)
        }
        if (act_Name != "") {
            jsonObject.put("act_Name", act_Name)
        }
        if (act_Description != "") {
            jsonObject.put("act_Description", act_Description)
        }
        if (startTime != "") {
            jsonObject.put("startTime", startTime)
        }
        if (endTime != "") {
            jsonObject.put("endTime", endTime)
        }
        if (peopleMaxium != "") {
            jsonObject.put("peopleMaxium", peopleMaxium)
        }
        if (Location != "") {
            jsonObject.put("Location", Location)
        }
        if (Latitude != "") {
            jsonObject.put("Latitude", Latitude)
        }
        if (Longitude != "") {
            jsonObject.put("Longitude", Longitude)
        }
        if (Models != " ") {
            jsonObject.put("Models", Models)
        }
        if (viewStatus != "") {
            jsonObject.put("viewStatus", viewStatus)
        }
        POST("http://163.18.42.222:8888/Fiestadb/Activity/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //拿取票種
    fun getTicket(act_Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
        POST("http://163.18.42.222:8888/Fiestadb/Ticket/SelectByAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //新增票種
    fun uploadTicket(act_Id: String?, ticketKinds: String?, Mounts: String?, Price: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("ticketKinds", ticketKinds)
            .put("Mounts", Mounts)
            .put("Remainder", Mounts)
            .put("Price", Price)
            .put("Useable", "true")
        POST("http://163.18.42.222:8888/Fiestadb/Ticket/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //刪除票種
    fun deleteTicket(ticketId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("ticketId", ticketId)
        POST("http://163.18.42.222:8888/Fiestadb/Ticket/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //驗票
    fun checkTicket(act_Id: String?, authId: String): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/Ticket/vaild", jsonObject, "Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //取消驗票
    fun undoCheckTicket(act_Id: String?, authId: String): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/Ticket/updateTicketStatusFalse", jsonObject, "Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //買票
    fun buyTicket(act_Id: String?, authId: String?, Notes: String?, ticketKinds: String? = ""): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
            .put("Notes", Notes)
        if (ticketKinds != "") {
            jsonObject.put("ticketKinds", ticketKinds)
        }
        POST("http://163.18.42.222:8888/Fiestadb/Activity/setJoinedList", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //修改票券備註
    fun editTicketNote(act_Id: String?, authId: String?, Notes: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
            .put("Notes", Notes)
        POST("http://163.18.42.222:8888/Fiestadb/Ticket/updateTicketNotes", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //拿取已購買未過期票券
    fun selectbuyTicket(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Id", Id)
        POST("http://163.18.42.222:8888/Fiestadb/Account/getUnexpiredAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //拿取已購買已過期票券
    fun selectexpTicket(): JSONObject {
        val jsonObject = JSONObject()
        POST("http://163.18.42.222:8888/Fiestadb/Activity/getExpire", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //拿取活動已賣出票券
    fun getActivitySellTicket(act_Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
        POST("http://163.18.42.222:8888/Fiestadb/Activity/getJoinedList", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //刪除活動已賣出票券
    fun deleteActivitySellTicket(act_Id: String?, authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/Activity/deleteJoinedList", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //上傳排程
    fun uploadShow(showName: String?, act_Id: String?, Detail: String?, showTime: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("showName",showName)
            .put("act_Id", act_Id)
            .put("Detail", Detail)
            .put("showTime", showTime)
            .put("Useable", "true")
        POST("http://163.18.42.222:8888/Fiestadb/Show/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //拿取排程
    fun getShow(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", Id)
        POST("http://163.18.42.222:8888/Fiestadb/Show/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //上傳排程
    fun updateShow(showId: String?, showName: String?, Detail: String?, showTime: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("showId", showId)
            .put("showName",showName)
            .put("Detail", Detail)
            .put("showTime", showTime)
        POST("http://163.18.42.222:8888/Fiestadb/Show/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //刪除排程
    fun deleteShow(showId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("showId", showId)
        POST("http://163.18.42.222:8888/Fiestadb/Show/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //獲取時間
    fun getTime(): JSONObject {
        GET("http://163.18.42.222:8888/Fiestadb/getTime", "Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //搜尋用戶
    fun userSearch(Search: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Search", Search)
        POST("http://163.18.42.222:8888/Fiestadb/Account/Search", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //轉換QRCode
    fun QRCode(act_Id: String?,authId: String?): String {
        val temp = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        val jsonObject = JSONObject()
            .put("Input",temp)
        POST("http://163.18.42.222:8888/Fiestadb/QRcode", jsonObject,"QRCode")
        for (i in 1..50) {
            if (::String.isInitialized && String != "") return String
            Thread.sleep(200)
        }
        return ""
    }

    //用QRCode驗票
    fun QRCodeCheck(act_Id: String?,authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        POST("http://163.18.42.222:8888/Fiestadb/Ticket/vaildQrcode", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //上傳抽獎
    fun uploadLotte(Prize: String?, act_Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Prize", Prize)
            .put("act_Id", act_Id)
            .put("Useable", "true")
        POST("http://163.18.42.222:8888/Fiestadb/Lotte/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //拿取抽獎
    fun getLotte(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", Id)
        POST("http://163.18.42.222:8888/Fiestadb/Lotte/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //抽取抽獎
    fun getLottery(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", Id)
        POST("http://163.18.42.222:8888/Fiestadb/Lotte/rand", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //更新抽獎
    fun updateLotte(Id: String?, accountId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("lotteId", Id)
            .put("accountId", accountId)
        POST("http://163.18.42.222:8888/Fiestadb/Lotte/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //刪除抽獎
    fun deleteLotte(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("lotteId", Id)
        POST("http://163.18.42.222:8888/Fiestadb/Lotte/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //拿取學校資料
    fun getSchoolData(): JSONObject {
        GET("http://163.18.42.222:8888/Fiestadb/getSchool","Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }


    private fun GET(url: String, type: String) {
        var request : Request
        if(token!=""){
            request  = Request.Builder().addHeader("Authorization","Bearer $token").url(url).build()
        }else{
            request  = Request.Builder().url(url).build()
        }
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("POSTFail", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var responseStr = response.body()?.string()
                Log.e("POSTSuccess",responseStr)
                if(type=="Object"){
                    try {
                        Object = JSONObject(responseStr)
                    }catch (e:Exception){
                        Object = JSONObject().put("code","000")
                    }
                }else{
                    try {
                        Array = JSONArray(responseStr)
                    }catch (e:Exception){
                        Array = JSONArray().put("000")
                    }
                }
            }
        })
    }

    private fun POST(url: String, jsonObject: JSONObject,type: String){
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())
        var request : Request
        if(token!=""&&token!=null&&token!="token"){
            request = Request.Builder().addHeader("Authorization","Bearer $token").url(url).post(body).build()
        }else{
            request = Request.Builder().url(url).post(body).build()
        }

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("POSTFail", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var responseStr = response.body()?.string()
                Log.e("POSTSuccess",responseStr)
                if(type=="Object"){
                    try {
                        Object = JSONObject(responseStr)
                    }catch (e:Exception){
                        Object = JSONObject().put("code","000")
                    }
                }else if(type=="Array"){
                    try {
                        Array = JSONArray(responseStr)
                    }catch (e:Exception){
                        Array = JSONArray().put("000")
                    }
                }else{
                    String = responseStr!!
                }
            }
        })
    }

    //上傳團片
    fun uploadImg(type: String, id: String, bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
            "file",
            "1.jpg",
            RequestBody.create(MediaType.parse("image/*"), byteArrayOutputStream.toByteArray())
        )
        val build = builder.build()
        val request = Request.Builder()
            .addHeader("Authorization","Bearer $token")
            .url("http://163.18.42.222:8888/Fiestadb/uploadImage?type=$type&Id=$id").post(build)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("IMGfail", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("IMG", response.body()?.string())
            }
        })
    }

    fun SearchIMG(url: String): Bitmap? {
        val bitmap = java.net.URL(url).openStream()
        return BitmapFactory.decodeStream(bitmap)
    }

}