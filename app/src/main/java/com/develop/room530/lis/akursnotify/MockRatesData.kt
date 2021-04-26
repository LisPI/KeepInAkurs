package com.develop.room530.lis.akursnotify

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.develop.room530.lis.akursnotify.model.AlfaRateModel
import com.develop.room530.lis.akursnotify.model.NbRbRateModel
import java.util.*

object MockRatesData {
    private var nbrbData = listOf(
        NbRbRateModel(2.4f, Date()),
        NbRbRateModel(2.2f, Date(Date().time + 500000)),
        NbRbRateModel(2.6f, Date(Date().time + 1000000)),
        NbRbRateModel(2.7f, Date(Date().time + 1100000)),
        NbRbRateModel(2.456f, Date(Date().time + 1200000)),
    )
    private var alfaData = listOf(
        AlfaRateModel(2.2f, Date(Date().time - 500000), ""),
        AlfaRateModel(2.1f, Date(Date().time + 0), ""),
        AlfaRateModel(2.3f, Date(Date().time + 500000), ""),
        AlfaRateModel(2.4f, Date(Date().time + 1000000), ""),
    )

    val liveNbRb = MutableLiveData<List<NbRbRateModel>>()
    val liveAlfa = MutableLiveData<List<AlfaRateModel>>()

    fun newNbRb() {
        liveNbRb.value = nbrbData
        Handler(Looper.getMainLooper()).postDelayed({ liveNbRb.value = emptyList() }, 5000)
    }

    fun newNbRb2() {
        Handler(Looper.getMainLooper()).postDelayed({ liveNbRb.value = nbrbData }, 5000)
    }

    fun newAlfa() {
        liveAlfa.value = alfaData
        //Handler(Looper.getMainLooper()).postDelayed({liveAlfa.value = emptyList()}, 8000)
    }
}
