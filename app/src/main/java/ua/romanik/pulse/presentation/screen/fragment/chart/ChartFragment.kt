package ua.romanik.pulse.presentation.screen.fragment.chart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val REQUEST_CALL = 11
    private var canCall = true

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
                            .also { checkPulseAndCallIfItNecessary(it.last()) }
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

    private suspend fun checkPulseAndCallIfItNecessary(pulseDataModel: PulseDataModel) {
        if (canCall) {
            pulseDataModel.pulseValue?.toInt()?.let { pulseValue ->
                if (pulseValue >= 130 || pulseValue <= 49) {
                    withContext(Dispatchers.Main) {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.CALL_PHONE
                            ) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            callHelp()
                        } else {
                            requestPermissions(
                                arrayOf(Manifest.permission.CALL_PHONE),
                                REQUEST_CALL
                            )
                        }
                    }
                }
            }
        }
    }

    private fun callHelp() {
        Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:+380990353693")
        }.also {
            canCall = false
            startActivity(it)
        }
    }

    override fun onResume() {
        super.onResume()
        canCall = true
    }

    override fun onPause() {
        super.onPause()
        canCall = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CALL) {
            if (permissions.size == 1 &&
                permissions[0] === Manifest.permission.CALL_PHONE &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                callHelp()
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
