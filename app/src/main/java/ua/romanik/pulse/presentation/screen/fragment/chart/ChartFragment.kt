package ua.romanik.pulse.presentation.screen.fragment.chart

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.gesture.ContainerScrollType
import lecho.lib.hellocharts.gesture.ZoomType
import lecho.lib.hellocharts.model.*
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import ua.romanik.pulse.R
import ua.romanik.pulse.data.local.UserRepository
import ua.romanik.pulse.data.network.api.PulseApi
import ua.romanik.pulse.data.network.model.PulseDataModel
import ua.romanik.pulse.presentation.screen.fragment.base.BaseFragment

class ChartFragment : BaseFragment(R.layout.fragment_chart) {

    private val pulseApi by inject<PulseApi>()
    private val userRepository by inject<UserRepository>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        charts?.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL)
        charts?.setInteractive(true)
        charts?.zoomType = ZoomType.HORIZONTAL_AND_VERTICAL
        fetchPulse()
    }

    private fun fetchPulse() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    runCatching {
                        pulseApi.getPulse(userRepository.fetchAuthUserData())
                    }.onSuccess {
                        displayCharts(it)
                    }.onFailure {
                        handleError(it)
                    }
                    delay(2500)
                }
            }
        }
    }

    private suspend fun displayCharts(data: List<PulseDataModel>?) = withContext(Dispatchers.Main) {
        data?.let {
            val yAxisValues = ArrayList<PointValue>()
            val axisValues = ArrayList<AxisValue>()
            val line = Line(yAxisValues)
            line.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)

            var level: Float

            for (i in it.indices) {
                axisValues.add(
                    i,
                    AxisValue(i.toFloat()).setLabel(DateTime(it[i].time?.time).toString("HH:mm"))
                )
                level = it[i].pulseValue?.toFloat() ?: 0.0F
                yAxisValues.add(PointValue(i.toFloat(), level))
            }

            val lines = ArrayList<Line>()
            lines.add(line)

            val data = LineChartData()
            data.lines = lines

            val axis = Axis()
            axis.values = axisValues
            axis.textSize = 7
            axis.textColor = ContextCompat.getColor(requireContext(), R.color.colorText)
            data.axisXBottom = axis

            val yAxis = Axis()
            yAxis.textSize = 10
            yAxis.setHasLines(true)
            yAxis.textColor = ContextCompat.getColor(requireContext(), R.color.colorText)
            data.axisYLeft = yAxis

            charts?.startDataAnimation()
            charts?.lineChartData = data

        }
    }

}
