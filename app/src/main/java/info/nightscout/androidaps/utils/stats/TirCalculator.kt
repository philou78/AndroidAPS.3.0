package info.nightscout.androidaps.utils.stats

import android.text.Spanned
import android.util.LongSparseArray
import info.nightscout.androidaps.Constants
import info.nightscout.androidaps.R
import info.nightscout.androidaps.interfaces.Profile
import info.nightscout.androidaps.database.AppRepository
import info.nightscout.androidaps.interfaces.ProfileFunction
import info.nightscout.androidaps.utils.DateUtil
import info.nightscout.androidaps.utils.HtmlHelper
import info.nightscout.androidaps.utils.MidnightTime
import info.nightscout.androidaps.utils.T
import info.nightscout.androidaps.utils.resources.ResourceHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TirCalculator @Inject constructor(
    private val rh: ResourceHelper,
    private val profileFunction: ProfileFunction,
    private val dateUtil: DateUtil,
    private val repository: AppRepository
) {

    fun calculate(days: Long, lowMgdl: Double, highMgdl: Double): LongSparseArray<TIR> {
        if (lowMgdl < 39) throw RuntimeException("Low below 39")
        if (lowMgdl > highMgdl) throw RuntimeException("Low > High")
        val startTime = MidnightTime.calc(dateUtil.now() - T.days(days).msecs())
        val endTime = MidnightTime.calc(dateUtil.now())

        val bgReadings = repository.compatGetBgReadingsDataFromTime(startTime, endTime, true).blockingGet()
        val result = LongSparseArray<TIR>()
        for (bg in bgReadings) {
            val midnight = MidnightTime.calc(bg.timestamp)
            var tir = result[midnight]
            if (tir == null) {
                tir = TIR(midnight, lowMgdl, highMgdl)
                result.append(midnight, tir)
            }
            if (bg.value < 39) tir.error()
            if (bg.value >= 39 && bg.value < lowMgdl) tir.below()
            if (bg.value in lowMgdl..highMgdl) tir.inRange()
            if (bg.value > highMgdl) tir.above()
        }
        return result
    }

    private fun averageTIR(tirs: LongSparseArray<TIR>): TIR {
        val totalTir = if (tirs.size() > 0) {
            TIR(tirs.valueAt(0).date, tirs.valueAt(0).lowThreshold, tirs.valueAt(0).highThreshold)
        } else {
            TIR(7, 70.0, 180.0)
        }
        for (i in 0 until tirs.size()) {
            val tir = tirs.valueAt(i)
            totalTir.below += tir.below
            totalTir.inRange += tir.inRange
            totalTir.above += tir.above
            totalTir.error += tir.error
            totalTir.count += tir.count
        }
        return totalTir
    }

    fun stats(): Spanned {
        val lowTirMgdl = Constants.STATS_RANGE_LOW_MMOL * Constants.MMOLL_TO_MGDL
        val highTirMgdl = Constants.STATS_RANGE_HIGH_MMOL * Constants.MMOLL_TO_MGDL
        val lowTitMgdl = Constants.STATS_TARGET_LOW_MMOL * Constants.MMOLL_TO_MGDL
        val highTitMgdl = Constants.STATS_TARGET_HIGH_MMOL * Constants.MMOLL_TO_MGDL

        val tir7 = calculate(7, lowTirMgdl, highTirMgdl)
        val averageTir7 = averageTIR(tir7)
        val tir30 = calculate(30, lowTirMgdl, highTirMgdl)
        val averageTir30 = averageTIR(tir30)
        val tir90 = calculate(90, lowTirMgdl, highTirMgdl) //PBA
        val averageTir90 = averageTIR(tir90) //PBA
        val tit7 = calculate(7, lowTitMgdl, highTitMgdl)
        val averageTit7 = averageTIR(tit7)
        val tit30 = calculate(30, lowTitMgdl, highTitMgdl)
        val averageTit30 = averageTIR(tit30)
        val tit90 = calculate(90, lowTitMgdl, highTitMgdl) //PBA
        val averageTit90 = averageTIR(tit90) //PBA
        return HtmlHelper.fromHtml(
            "<br><b>" + rh.gs(R.string.tir) + " (" + Profile.toCurrentUnitsString(profileFunction, lowTirMgdl) + "-" + Profile.toCurrentUnitsString(profileFunction, highTirMgdl) + "):</b><br>" +
                toText(rh, tir7) +
                "<br><b>" + rh.gs(R.string.average) + " (" + Profile.toCurrentUnitsString(profileFunction, lowTirMgdl) + "-" + Profile.toCurrentUnitsString(profileFunction, highTirMgdl) + "):</b><br>" +
                averageTir7.toText(rh, tir7.size()) + "<br>" +
//PBA                averageTir30.toText(rh, tir30.size()) +
                averageTir30.toText(rh, tir30.size()) + "<br>" + //PBA
                averageTir90.toText(rh, tir90.size()) + //PBA
                "<br><b>" + rh.gs(R.string.average) + " (" + Profile.toCurrentUnitsString(profileFunction, lowTitMgdl) + "-" + Profile.toCurrentUnitsString(profileFunction, highTitMgdl) + "):</b><br>" +
                averageTit7.toText(rh, tit7.size()) + "<br>" +
//PBA                averageTit30.toText(rh, tit30.size())
                averageTit30.toText(rh, tit30.size()) + "<br>" + //PBA
            averageTit90.toText(rh, tit90.size()) //PBA
        )
    }

    fun toText(rh: ResourceHelper, tirs: LongSparseArray<TIR>): String {
        var t = ""
        for (i in 0 until tirs.size()) {
            t += "${tirs.valueAt(i).toText(rh, dateUtil)}<br>"
        }
        return t
    }

}