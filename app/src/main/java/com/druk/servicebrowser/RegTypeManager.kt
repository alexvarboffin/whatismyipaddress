package com.druk.servicebrowser

import android.content.Context
import android.text.TextUtils
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.LinkedList
import java.util.TreeMap

class RegTypeManager internal constructor(private val mContext: Context) {
    private var mServiceNamesTree: TreeMap<String?, String?>? = null

    init {
        // Load reg type descriptions as quick as possible on io thread
        Flowable.just<String?>("_zigbee-gateway._udp.")
            .map<String?>(Function { regType: String? -> this.getRegTypeDescription(regType) })
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe()
    }

    val listRegTypes: MutableList<String?>
        get() {
            if (this.mServiceNamesTree == null) {
                return LinkedList<String?>()
            }
            return LinkedList<String?>(mServiceNamesTree!!.keys)
        }

    fun getRegTypeDescription(regType: String?): String? {
        if (mServiceNamesTree == null) {
            synchronized(this) {
                if (mServiceNamesTree == null) {
                    mServiceNamesTree = TreeMap<String?, String?>()
                    try {
                        val `is` = mContext.assets.open("service-names-port-numbers.csv")
                        try {
                            val reader = BufferedReader(InputStreamReader(`is`))
                            var line: String?
                            while ((reader.readLine().also { line = it }) != null) {
                                val rowData: Array<String?> =
                                    line!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                        .toTypedArray()
                                if (rowData.size < 4 || TextUtils.isEmpty(rowData[0]) || TextUtils.isEmpty(
                                        rowData[2]
                                    ) || TextUtils.isEmpty(rowData[3])
                                ) {
                                    continue
                                }
                                if (rowData[0]!!.contains(" ") || rowData[2]!!.contains(" ")) {
                                    continue
                                }
                                mServiceNamesTree!!.put(
                                    "_" + rowData[0] + "._" + rowData[2] + ".",
                                    rowData[3]
                                )
                            }
                        } catch (ex: IOException) {
                            // handle exception
                        } finally {
                            try {
                                `is`.close()
                            } catch (e: IOException) {
                                Log.e(TAG, "init error: ", e)
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Log.e(TAG, "service-names-port-numbers.csv reading error: ", e)
                    }
                }
            }
        }
        return mServiceNamesTree!!.get(regType)
    }


    companion object {
        private const val TAG = "RegTypeManager"
    }
}
