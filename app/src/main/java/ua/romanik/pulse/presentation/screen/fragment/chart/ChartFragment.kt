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
        charts?.isInteractive = false
        fetchPulse()
    }

    private fun fetchPulse() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                while (true) {
                    runCatching {
                        pulseApi.getPulse(userRepository.fetchAuthUserData()).takeLast(15)
                    }.onSuccess {
                        formatChartData(it).let {
                            withContext(Dispatchers.Main) {
                                charts?.lineChartData = it
                            }
                        }
                    }.onFailure {
                        handleError(it)
                    }
                    delay(2000)
                }
            }
        }
    }

    private suspend fun formatChartData(data: List<PulseDataModel>): LineChartData = withContext(Dispatchers.Default) {
        return@withContext data.let {
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
            lines.add(
                Line(
                    listOf(
                        PointValue(0F, 0F),
                        PointValue( 1F, 170F)
                    )
                ).apply { color = ContextCompat.getColor(requireContext(), R.color.colorTransparent) }
            )

            val chartData = LineChartData()
            chartData.lines = lines
            chartData.axisYLeft = Axis.generateAxisFromRange(0F, 170F, 10F).apply {
                textColor = ContextCompat.getColor(requireContext(), R.color.colorText)
                textSize = 10
                setHasLines(true)
            }

            val axis = Axis()
            axis.values = axisValues
            axis.textSize = 10
            axis.textColor = ContextCompat.getColor(requireContext(), R.color.colorText)
            chartData.apply {
                axisXBottom = axis
            }
        }
    }

}
