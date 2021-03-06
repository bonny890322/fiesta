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





    //??????????????????
    fun getUserData(userId: String?, userPassword: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
            .put("userPassword", userPassword)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????????????????
    fun getUserEmailStatus(userId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/getReviewStatus", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
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
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    // ??????????????????
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
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/update", jsonObject,"Object")
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
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    // ???????????????
    fun mailConfirm(userId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
            .put("type", "1")
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/SendConfirm", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //????????????
    fun forgetPassword(userId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("userId", userId)
            .put("type", "2")
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/SendConfirm", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????
    fun getActivityScore(act_Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/FeedBack/Score/Act/SelectByAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????
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
        POST("https://fiesta.nkust.edu.tw/Fiestadb/FeedBack/Score/Act/upload", jsonObject, "Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????
    fun uploadShowScore(act_Id: String?, showId: String?, authId: String?, score_Date: String?, Detail: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id",act_Id)
            .put("showId", showId)
            .put("authId", authId)
            .put("score_Date", score_Date)
            .put("Detail", Detail)
            .put("Useable","true")
        POST("https://fiesta.nkust.edu.tw/Fiestadb/FeedBack/Score/Show/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }


    //??????Show??????
    fun getShowScore(showId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("showId",showId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/FeedBack/Score/Show/SelectByShow", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //???????????????????????????
    fun getActivityScoreByAuth(authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/FeedBack/Score/Act/SelectByAuth", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //????????????
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
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????
    fun getGroupActivity(groupId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/getAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????????????????
    fun findGroupName(groupName: String): JSONObject {
        val jsonObject = JSONObject()
            .put("groupName", groupName)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/FIndName", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //?????????????????????
    fun getGroupByUser(authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/getJoinedGroup", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //?????????????????????
    fun getGroupByGroupId(groupId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //????????????
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
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //????????????
    fun deleteGroup(groupId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????
    fun deleteGroupMember(groupId: String?, authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/Member/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????
    fun selectGroupMember(groupId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/Member/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????
    fun addGroupMember(groupId: String?, authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("groupId", groupId)
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Group/Member/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }


    //??????TAG
    fun getTag(): JSONObject {
        GET("https://fiesta.nkust.edu.tw/Fiestadb/Tag/select","Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
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
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //??????????????????
    fun getActivity( act_Id: String = "[]"): JSONObject {
        val temp = JSONArray(act_Id)
        val jsonObject = JSONObject()
            .put("act_Id", temp)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/getRecommend", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //????????????
    fun deleteActivity(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Id", Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????????????????
    fun getActivityById(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Id", Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }
    //??????????????????
    fun getExpActivity():JSONObject{
        val jsonObject = JSONObject()
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/getExpire", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //?????????????????????
    fun getEditActivity(authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/getCreateAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????TAG????????????
    fun getActByTag(Tag: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Tag", Tag)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/getActByTag", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun activitySearch(Search: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Search", Search)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/Search", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????????????????
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
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun getTicket(act_Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Ticket/SelectByAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun uploadTicket(act_Id: String?, ticketKinds: String?, Mounts: String?, Price: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("ticketKinds", ticketKinds)
            .put("Mounts", Mounts)
            .put("Remainder", Mounts)
            .put("Price", Price)
            .put("Useable", "true")
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Ticket/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun deleteTicket(ticketId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("ticketId", ticketId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Ticket/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????
    fun checkTicket(act_Id: String?, authId: String): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Ticket/vaild", jsonObject, "Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun undoCheckTicket(act_Id: String?, authId: String): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Ticket/updateTicketStatusFalse", jsonObject, "Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????
    fun buyTicket(act_Id: String?, authId: String?, Notes: String?, ticketKinds: String? = ""): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
            .put("Notes", Notes)
        if (ticketKinds != "") {
            jsonObject.put("ticketKinds", ticketKinds)
        }
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/setJoinedList", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????????????????
    fun editTicketNote(act_Id: String?, authId: String?, Notes: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
            .put("Notes", Notes)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Ticket/updateTicketNotes", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????????????????????????????
    fun selectbuyTicket(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Id", Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/getUnexpiredAct", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????????????????????????????
    fun selectexpTicket(): JSONObject {
        val jsonObject = JSONObject()
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/getExpire", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //???????????????????????????
    fun getActivitySellTicket(act_Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/getJoinedList", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //???????????????????????????
    fun deleteActivitySellTicket(act_Id: String?, authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Activity/deleteJoinedList", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun uploadShow(showName: String?, act_Id: String?, Detail: String?, showTime: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("showName",showName)
            .put("act_Id", act_Id)
            .put("Detail", Detail)
            .put("showTime", showTime)
            .put("Useable", "true")
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Show/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun getShow(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Show/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun updateShow(showId: String?, showName: String?, Detail: String?, showTime: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("showId", showId)
            .put("showName",showName)
            .put("Detail", Detail)
            .put("showTime", showTime)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Show/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun deleteShow(showId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("showId", showId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Show/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun getTime(): JSONObject {
        GET("https://fiesta.nkust.edu.tw/Fiestadb/getTime", "Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //????????????
    fun userSearch(Search: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Search", Search)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Account/Search", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????QRCode
    fun QRCode(act_Id: String?,authId: String?): String {
        val temp = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        val jsonObject = JSONObject()
            .put("Input",temp)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/QRcode", jsonObject,"QRCode")
        for (i in 1..50) {
            if (::String.isInitialized && String != "") return String
            Thread.sleep(200)
        }
        return ""
    }

    //???QRCode??????
    fun QRCodeCheck(act_Id: String?,authId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", act_Id)
            .put("authId", authId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Ticket/vaildQrcode", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //????????????
    fun uploadLotte(Prize: String?, act_Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("Prize", Prize)
            .put("act_Id", act_Id)
            .put("Useable", "true")
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Lotte/upload", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code", "000")
    }

    //????????????
    fun getLotte(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Lotte/select", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun getLottery(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("act_Id", Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Lotte/rand", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun updateLotte(Id: String?, accountId: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("lotteId", Id)
            .put("accountId", accountId)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Lotte/update", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //????????????
    fun deleteLotte(Id: String?): JSONObject {
        val jsonObject = JSONObject()
            .put("lotteId", Id)
        POST("https://fiesta.nkust.edu.tw/Fiestadb/Lotte/delete", jsonObject,"Object")
        for (i in 1..50) {
            if (::Object.isInitialized && Object.length() != 0) return Object
            Thread.sleep(200)
        }
        return JSONObject().put("code","000")
    }

    //??????????????????
    fun getSchoolData(): JSONObject {
        GET("https://fiesta.nkust.edu.tw/Fiestadb/getSchool","Object")
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

    //????????????
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
            .url("https://fiesta.nkust.edu.tw/Fiestadb/uploadImage?type=$type&Id=$id").post(build)
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