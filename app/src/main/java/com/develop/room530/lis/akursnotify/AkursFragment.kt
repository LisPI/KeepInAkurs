package com.develop.room530.lis.akursnotify

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.develop.room530.lis.akursnotify.database.getDatabase
import com.develop.room530.lis.akursnotify.databinding.FragmentAkursBinding
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

class AkursFragment : Fragment() {

    private var _binding: FragmentAkursBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAkursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            val rates = withContext(Dispatchers.IO) {
                getDatabase(this@AkursFragment.requireContext()).akursDatabaseDao.getAkurs()
            }.sortedBy { it.date }

            val alfaDataset = LineDataSet(rates.mapIndexed { index, akurs ->
                Entry(
                    index.toFloat(),//akurs.date.time.toFloat(),
                    akurs.rate.toFloatOrNull() ?: -1.0F
                )
            }, "USD по А-Курс")

            alfaDataset.color = Color.RED
            alfaDataset.valueTextSize = 12F

            val datasets = listOf<ILineDataSet>(alfaDataset)
            val data = LineData(datasets)
            data.isHighlightEnabled = false

            with(binding.chart) {
                xAxis.valueFormatter = IndexAxisValueFormatter(rates.map { it.time })
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                description.text = ""
                this.data = data
                axisRight.isEnabled = false
                legend.textSize = 14F
                setNoDataText("")
                invalidate()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}