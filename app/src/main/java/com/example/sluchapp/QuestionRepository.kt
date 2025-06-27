package com.example.sluchapp.data

import android.content.Context
import com.example.sluchapp.ExerciseQuestion
import com.example.sluchapp.EarTrainingType

object QuestionRepository {

    fun getQuestions(context: Context, type: EarTrainingType, level: String): List<ExerciseQuestion> {
        return when (type) {
            EarTrainingType.INTERVALS -> getIntervalQuestions(context, level)
            EarTrainingType.CHORDS -> getChordQuestions(context, level)
            else -> emptyList()
        }
    }

    private fun getIntervalQuestions(context: Context, level: String): List<ExerciseQuestion> {
        val basic = listOf(
            IntervalData("interval_unison", "Unison"),
            IntervalData("interval_major_second", "Major second"),
            IntervalData("interval_minor_second", "Minor second"),
            IntervalData("interval_major_third", "Major third"),
            IntervalData("interval_minor_third", "Minor third")
        )

        val advanced = listOf(
            IntervalData("interval_perfect_fourth", "Perfect fourth"),
            IntervalData("interval_tritone", "Tritone"),
            IntervalData("interval_perfect_fifth", "Perfect fifth"),
            IntervalData("interval_major_sixth", "Major sixth"),
            IntervalData("interval_minor_sixth", "Minor sixth"),
            IntervalData("interval_major_seventh", "Major seventh"),
            IntervalData("interval_minor_seventh", "Minor seventh"),
            IntervalData("interval_octave", "Octave")
        )

        val data = if (level == "basic") basic else basic + advanced
        return data.map {
            ExerciseQuestion(
                questionText = "What interval is this?",
                answers = data.map { d -> d.label },
                correctAnswerIndex = data.indexOf(it),
                audioResId = getResIdByName(context, it.fileName)
            )
        }
    }

    private fun getChordQuestions(context: Context, level: String): List<ExerciseQuestion> {
        val basic = listOf(
            ChordData("chord_major", "Major"),
            ChordData("chord_minor", "Minor")
        )

        val advanced = listOf(
            ChordData("chord_diminished", "Diminished"),
            ChordData("chord_augmented", "Augmented"),
            ChordData("chord_major_seventh", "Major 7th"),
            ChordData("chord_minor_seventh", "Minor 7th"),
            ChordData("chord_dominant_seventh", "Dominant 7th")
        )

        val data = if (level == "basic") basic else basic + advanced
        return data.map {
            ExerciseQuestion(
                questionText = "What chord is this?",
                answers = data.map { c -> c.label },
                correctAnswerIndex = data.indexOf(it),
                audioResId = getResIdByName(context, it.fileName)
            )
        }
    }

    private fun getResIdByName(context: Context, name: String): Int {
        return context.resources.getIdentifier(name, "raw", context.packageName)
    }

    private data class IntervalData(val fileName: String, val label: String)
    private data class ChordData(val fileName: String, val label: String)
}
