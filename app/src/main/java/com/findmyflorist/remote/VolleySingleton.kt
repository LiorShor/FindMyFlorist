package com.findmyflorist.remote

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleySingleton private constructor(context: Context) {
    val requestQueue: RequestQueue by lazy{
        Volley.newRequestQueue(context.applicationContext)
    }
    //get() = this.requestQueue
    companion object {
        private var mInstance: VolleySingleton? = null
        @Synchronized
        fun getInstance(context: Context): VolleySingleton? {
            if (mInstance == null) // if instance is not created before
            {
                mInstance = VolleySingleton(context)
            }
            return mInstance
        }
    }
}