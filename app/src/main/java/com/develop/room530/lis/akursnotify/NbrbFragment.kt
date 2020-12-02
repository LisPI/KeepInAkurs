package com.develop.room530.lis.akursnotify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.develop.room530.lis.akursnotify.database.getDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NbrbFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nbrb, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chart = view.findViewById<LineChart>(R.id.chart)

        CoroutineScope(Dispatchers.Main).launch {
            val nbRates = withContext(Dispatchers.IO) {
                getDatabase(this@NbrbFragment.requireContext()).nbrbDatabaseDao.getNbrbkurs()
            }.sortedBy { it.date }

            val nbDataset = LineDataSet(nbRates.mapIndexed { index, nbrbModel ->
                Entry(
                    index.toFloat(),
                    nbrbModel.rate.toFloat()
                )
            }, "USD по НБ")
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(nbRates.map { it.date.date.toString() })
            val datasets = listOf<ILineDataSet>(nbDataset)
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            val data = LineData(datasets)
            data.isHighlightEnabled = false
            chart.description.text = ""
            chart.data = data
            chart.axisRight.isEnabled = false
            chart.legend.textSize = 14F
            chart.setNoDataText("")
            chart.invalidate()
        }
    }
}