package com.bhanukalyan.myapplication

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyTaskResponse {

    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("link")
    @Expose
    var link: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("modified")
    @Expose
    var modified: String? = null
    @SerializedName("generator")
    @Expose
    var generator: String? = null
    @SerializedName("items")
    @Expose
    var items: List<Item>? = null

}
