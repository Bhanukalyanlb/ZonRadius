package com.bhanukalyan.myapplication

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class MyTaskListAdapter(internal var mActivity: Activity, internal var articlesList: List<Item>, internal var listener: CustomItemListener) : RecyclerView.Adapter<MyTaskListAdapter.ViewHolderWorkOrder>() {

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolderWorkOrder {
        val recipeItem = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_my_task, viewGroup, false)
        return ViewHolderWorkOrder(recipeItem)
    }

    override fun onBindViewHolder(viewHolder: ViewHolderWorkOrder, position: Int) {
        val article = articlesList[position]
        viewHolder.tvTitleText.text = if (TextUtils.isEmpty(article.title)) "-" else article.title
        viewHolder.tvCommentText.text = if (TextUtils.isEmpty(article.title)) "-" else article.title

        val time = article.published
        val MyFinalValue = covertTimeToText(time)
        viewHolder.tvTime.text = MyFinalValue
        viewHolder.tvProvider.text = article.author!!.substring(0, article.author!!.indexOf(" "))

        val requestOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)
        Glide.with(mActivity)
                .load(article.media!!.m)
                .apply(requestOptions)
                .into(viewHolder.ivContent)
    }

    fun covertTimeToText(dataDate: String?): String {

        var convTime = ""

        val suffix = "Ago"

        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val pasTime = dateFormat.parse(dataDate!!)

            val nowTime = Date()

            val dateDiff = nowTime.time - pasTime!!.time

            val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)

            if (second < 60) {
                convTime = "$second Seconds $suffix"
            } else if (minute < 60) {
                convTime = "$minute Minutes $suffix"
            } else if (hour < 24) {
                convTime = "$hour Hours $suffix"
            } else if (day >= 7) {
                if (day > 30) {
                    convTime = (day / 30).toString() + " Months " + suffix
                } else if (day > 360) {
                    convTime = (day / 360).toString() + " Years " + suffix
                } else {
                    convTime = (day / 7).toString() + " Week " + suffix
                }
            } else if (day < 7) {
                convTime = "$day Days $suffix"
            }

        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message)
        }

        return convTime
    }

    inner class ViewHolderWorkOrder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        internal var tvTitleText: TextView
        internal var tvCommentText: TextView
        internal var tvProvider: TextView
        internal var tvTime: TextView
        internal var ivContent: ImageView

        init {
            ivContent = view.findViewById(R.id.ivContent)
            tvTitleText = view.findViewById(R.id.tvTitleText)
            tvCommentText = view.findViewById(R.id.tvCommentText)
            tvProvider = view.findViewById(R.id.tvProvider)
            tvTime = view.findViewById(R.id.tvTime)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener.onClick(articlesList[adapterPosition])
        }
    }
}