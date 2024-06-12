package com.pablofraile.montcoin.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pablofraile.montcoin.model.HourOperationsStats
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer

typealias Hour = Int
typealias AmountValue = Int

class IncomeChartData(
    val income: List<Pair<AmountValue, Hour>>,
    val expenses: List<Pair<AmountValue, Hour>>
) {
    override fun toString(): String {
        return "IncomeChartData(income=$income, expenses=$expenses)"
    }
}

fun List<HourOperationsStats>.toChartData() = IncomeChartData(
    income = this.map { Pair(it.positiveAmount.value, -it.hour) },
    expenses = this.map { Pair(it.negativeAmount.value, -it.hour) }
)

@Composable
fun Chart(
    modelProducer: CartesianChartModelProducer,
) {
    ProvideVicoTheme(
        rememberM3VicoTheme(
            lineCartesianLayerColors = listOf(
                Color.Green,
                Color.Red
            )
        )
    ) {
        val scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End)
        val zoomState = rememberVicoZoomState()
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(),
            ),
            modelProducer = modelProducer,
            scrollState = scrollState,
            zoomState = zoomState,
        )
    }
}

