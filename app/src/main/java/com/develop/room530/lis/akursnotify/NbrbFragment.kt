package com.develop.room530.lis.akursnotify

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.develop.room530.lis.akursnotify.database.getDatabase
import com.develop.room530.lis.akursnotify.databinding.FragmentNbrbBinding
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

    private var _binding: FragmentNbrbBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNbrbBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            nbDataset.color = Color.GREEN
            nbDataset.valueTextSize = 12F

            val datasets = listOf<ILineDataSet>(nbDataset)
            val data = LineData(datasets)
            data.isHighlightEnabled = false

            with(binding.chart) {
                xAxis.valueFormatter = IndexAxisValueFormatter(nbRates.map { it.date.date.toString() })
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                description.text = ""
                this.data = data
                axisRight.isEnabled = false
                legend.textSize = 14F
                setNoDataText("")
                // TODO do it!
                setExtraOffsets(10F,10F, 20F,10F)

                invalidate()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}