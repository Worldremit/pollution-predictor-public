package com.worldremit.confusion

import com.worldremit.DevData
import com.worldremit.DevData.NORMS
import com.worldremit.util.isNotEmpty
import org.junit.jupiter.api.Test

internal class ConfusionServiceTest {

    // FeatureService(FEATURE_FILTER)
    private val confusionService = ConfusionService(NORMS)

    // TODO: finish tests
    @Test
    fun `should create ConfusionRecord`() {
        val confusionRecord =
            confusionService.createConfusionRecord(DevData.MATCHED_RECORD, DevData.MEASUREMENT_STATS_RESULT)

        println(DevData.MATCHED_RECORD.isNotEmpty())


//        assertThat(confusionRecord)
//                .isEqualTo(DevData.CONFUSION_RECORD) // TBD
    }

//    private fun isNotEmptyRegularRecord(matched: MatchedRecord) =
//        matched.predicted.predictedPatterns.isEmpty() && matched.predicted.locationTime.isAfter(trainingEnd)


}