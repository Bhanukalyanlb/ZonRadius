package com.bhanukalyan.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    lateinit var tvEmpty: TextView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout)

        tvEmpty.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        swipeRefreshLayout.setOnRefreshListener { getProjectTask() }

        getProjectTask()
    }

    fun getProjectTask() {
        // Below conditon for check internet is available or not
        if (isInternetConnected(this@MainActivity)) {

            val url = "https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1"
            val client = OkHttpClient()
            // Build request
            val request = Request.Builder().get().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    call.cancel()
                    val myResponse = e.message
                    this@MainActivity.runOnUiThread {
                        recyclerView.visibility = View.GONE
                        tvEmpty.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        swipeRefreshLayout.isRefreshing = false
                        println("jsonResponse_GetService::" + myResponse!!)
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    println("jsonResponse_GetService:$response")
                    val myResponse = response.body!!.string()
                    this@MainActivity.runOnUiThread {
                        swipeRefreshLayout.isRefreshing = false
                        val gson = Gson()
                        val myTaskResponse = gson.fromJson(myResponse, MyTaskResponse::class.java)
                        if (myTaskResponse != null && myTaskResponse.items != null && myTaskResponse.items!!.size > 0) {
                            initWorkOrder(myTaskResponse.items)
                            recyclerView.visibility = View.VISIBLE
                            tvEmpty.visibility = View.GONE
                        } else {
                            recyclerView.visibility = View.GONE
                            tvEmpty.visibility = View.VISIBLE
                        }
                        progressBar.visibility = View.GONE
                    }
                }
            })
        } else {
            Toast.makeText(
                this,
                getString(R.string.internet_connection_error_text),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initWorkOrder(workOrderList: List<Item>?) {
        recyclerView.setItemViewCacheSize(workOrderList!!.size)
        recyclerView.adapter = workOrderList?.let {
            MyTaskListAdapter(this@MainActivity, it, object : CustomItemListener {
                override fun onClick(`object`: Item) {
                    Toast.makeText(
                        this@MainActivity,
                        "Posted by : " + `object`.author!!.substring(0, `object`.author!!.indexOf(" ")),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    companion object {

        fun isInternetConnected(appContext: Context?): Boolean {
            var haveConnectedWifi = false
            var haveConnectedMobile = false

            if (appContext == null)
                return true
            val cm =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.allNetworkInfo
            for (ni in netInfo) {
                if (ni.typeName.equals("WIFI", ignoreCase = true))
                    if (ni.isConnected)
                        haveConnectedWifi = true
                if (ni.typeName.equals("MOBILE", ignoreCase = true))
                    if (ni.isConnected)
                        haveConnectedMobile = true
            }
            return haveConnectedWifi || haveConnectedMobile

        }
    }
}
