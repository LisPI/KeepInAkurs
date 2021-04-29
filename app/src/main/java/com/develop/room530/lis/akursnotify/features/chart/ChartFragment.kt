package com.develop.room530.lis.akursnotify.features.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.develop.room530.lis.akursnotify.databinding.FragmentChartBinding
import com.develop.room530.lis.akursnotify.getDateHHMMDDMMFormatFromLong
import com.develop.room530.lis.akursnotify.model.AlfaRateModel
import com.develop.room530.lis.akursnotify.model.NbRbRateModel
import com.develop.room530.lis.akursnotify.model.RateModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartFragment : Fragment() { // TODO use constructor with layout parameter

    private var _binding: FragmentChartBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel by viewModels<ChartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("chart", "onCreate $this")
    }

    override fun onStart() {
        super.onStart()
        Log.d("chart", "onStart $this")
    }

    override fun onResume() {
        super.onResume()
        Log.d("chart", "onResume $this")
    }

    override fun onPause() {
        super.onPause()
        Log.d("chart", "onPause $this")
    }

    override fun onStop() {
        super.onStop()
        Log.d("chart", "onStop $this")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("chart", "onDetach $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("chart", "onDestroy $this")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.rates.observe(viewLifecycleOwner, {
            val a = viewModel.alfaRatesData.value
            val b = viewModel.nbrbRatesData.value
            if (a != null && b != null) {
                binding.chart.printChart(listOf(a, b))
            }
        })

        binding.chart.setTouchEnabled(false)
        binding.chipsGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.chip1.id -> {
                    binding.chart.setTouchEnabled(false)
                    viewModel.updateRatesForChart(1)
                }
                binding.chip2.id -> {
                    binding.chart.setTouchEnabled(true)
                    viewModel.updateRatesForChart(2)
                }
                binding.chip3.id -> {
                    binding.chart.setTouchEnabled(true)
                    viewModel.updateRatesForChart(3)
                }
                binding.chip4.id -> {
                    binding.chart.setTouchEnabled(true)
                    viewModel.updateRatesForChart(4)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        Log.d("chart", "onDestroyView $this")
        super.onDestroyView()
    }

    private fun LineChart.printChart(ratesData: List<List<RateModel>>) {
        val dataSets = mutableListOf<ILineDataSet>()
        ratesData.forEach {
            if (it.isNotEmpty()) {
                val label = when (it.first()) {
                    is AlfaRateModel -> "USD по А-Курс"
                    is NbRbRateModel -> "USD по НБ"
                }

                val dataSet = LineDataSet(it.map { rate ->
                    Entry(
                        rate.date.time / 60000F, //index.toFloat(), //rate.date, //FIXME
                        rate.rate
                    )
                }, label)

                dataSet.color = when (it.first()) {
                    is AlfaRateModel -> Color.RED
                    is NbRbRateModel -> Color.GREEN
                }

                dataSet.valueTextSize = 12F

                dataSets.add(dataSet)
            }
        }

        val data = LineData(dataSets)
        data.isHighlightEnabled = false

        setDrawBorders(true)
        xAxis.isGranularityEnabled = true
        //xAxis.granularity = 60 *1F
        xAxis.labelRotationAngle = 315f

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return getDateHHMMDDMMFormatFromLong(value.toLong() * 60000)
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        description.text = ""
        this.data = data
        this.data.notifyDataChanged()

        axisRight.isEnabled = false
        legend.textSize = 14F
        setNoDataText("")
        setExtraOffsets(10F, 10F, 20F, 10F)

        setMaxVisibleValueCount(30)

        notifyDataSetChanged()

        invalidate()
    }
}