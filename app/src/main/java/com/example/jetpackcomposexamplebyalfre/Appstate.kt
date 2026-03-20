package com.example.jetpackcomposexamplebyalfre

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Data Models
 */

data class UserProfile(
    val name: String = "Alfre",
    val age: Int = 28,
    val weight: Float = 75f,   // kg
    val height: Int = 178,     // cm
    val weeklyGoal: Int = 5    // workouts per week
)

data class WorkoutSession(
    val id: Int,
    val name: String,
    val category: WorkoutCategory,
    val durationMin: Int,
    val caloriesBurned: Int,
    val sets: Int = 0,
    val reps: Int = 0,
    val date: String = "Today",
    val completed: Boolean = false,
    val gradient: List<Long>
)

enum class WorkoutCategory(val label: String) {
    HIIT("HIIT"), STRENGTH("Strength"), YOGA("Yoga"),
    CARDIO("Cardio"), MOBILITY("Mobility")
}

data class DailyLog(
    val steps: Int = 8432,
    val caloriesBurned: Int = 1560,
    val caloriesGoal: Int = 2000,
    val waterMl: Int = 2100,
    val waterGoalMl: Int = 3000,
    val activeMinutes: Int = 47,
    val activeGoalMin: Int = 60,
    val heartRate: Int = 72,
    val sleepHours: Float = 7.3f
)

data class TimerState(
    val isRunning: Boolean = false,
    val secondsElapsed: Int = 0,
    val workoutName: String = "",
    val phase: TimerPhase = TimerPhase.WORK,
    val workSeconds: Int = 40,
    val restSeconds: Int = 20,
    val currentRound: Int = 1,
    val totalRounds: Int = 8
)

enum class TimerPhase { WORK, REST, FINISHED }

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean,
    val progress: Float = 1f
)

/**
 * APP STATE (ViewModel)
 */

class AppViewModel : ViewModel() {

    // ── Profile ────────────────────────────────────────────
    var profile by mutableStateOf(UserProfile())
        private set

    // ── Daily Log ─────────────────────────────────────────
    var dailyLog by mutableStateOf(DailyLog())
        private set

    // ── Workouts ──────────────────────────────────────────
    var workouts by mutableStateOf(defaultWorkouts())
        private set

    // ── Water tracking ────────────────────────────────────
    fun addWater(ml: Int) {
        dailyLog = dailyLog.copy(waterMl = (dailyLog.waterMl + ml).coerceAtMost(dailyLog.waterGoalMl))
    }

    // ── Steps ─────────────────────────────────────────────
    fun addSteps(steps: Int) {
        dailyLog = dailyLog.copy(steps = dailyLog.steps + steps)
    }

    // ── Complete workout ───────────────────────────────────
    fun completeWorkout(id: Int) {
        workouts = workouts.map {
            if (it.id == id) it.copy(completed = true) else it
        }
        val workout = workouts.find { it.id == id } ?: return
        dailyLog = dailyLog.copy(
            caloriesBurned = (dailyLog.caloriesBurned + workout.caloriesBurned)
                .coerceAtMost(dailyLog.caloriesGoal),
            activeMinutes = (dailyLog.activeMinutes + workout.durationMin)
                .coerceAtMost(dailyLog.activeGoalMin + 30)
        )
        checkAchievements()
    }

    // ── Timer ─────────────────────────────────────────────
    var timerState by mutableStateOf(TimerState())
        private set

    fun startTimer(workoutName: String, workSec: Int = 40, restSec: Int = 20, rounds: Int = 8) {
        timerState = TimerState(
            isRunning = true,
            workoutName = workoutName,
            workSeconds = workSec,
            restSeconds = restSec,
            totalRounds = rounds,
            phase = TimerPhase.WORK
        )
    }

    fun tickTimer() {
        if (!timerState.isRunning) return
        val current = timerState
        val phaseMax = if (current.phase == TimerPhase.WORK) current.workSeconds else current.restSeconds
        val next = current.secondsElapsed + 1

        if (next >= phaseMax) {
            when {
                current.phase == TimerPhase.WORK -> {
                    timerState = current.copy(secondsElapsed = 0, phase = TimerPhase.REST)
                }
                current.currentRound >= current.totalRounds -> {
                    timerState = current.copy(isRunning = false, phase = TimerPhase.FINISHED)
                }
                else -> {
                    timerState = current.copy(
                        secondsElapsed = 0,
                        phase = TimerPhase.WORK,
                        currentRound = current.currentRound + 1
                    )
                }
            }
        } else {
            timerState = current.copy(secondsElapsed = next)
        }
    }

    fun pauseResumeTimer() {
        timerState = timerState.copy(isRunning = !timerState.isRunning)
    }

    fun resetTimer() {
        timerState = TimerState()
    }

    // ── Weekly data ───────────────────────────────────────
    val weeklyCalories = listOf(1200, 1850, 950, 2100, 1600, 2200, 1560)
    val weeklySteps    = listOf(6200, 9100, 5400, 11200, 8300, 10500, 8432)

    fun getWeekDayLabels(): List<String> {
        val today = LocalDate.now()
        return (6 downTo 0).map { offset ->
            today.minusDays(offset.toLong())
                .dayOfWeek
                .getDisplayName(TextStyle.NARROW, Locale.getDefault())
        }
    }

    // ── Achievements ──────────────────────────────────────
    var achievements by mutableStateOf(defaultAchievements())
        private set

    private fun checkAchievements() {
        val completedCount = workouts.count { it.completed }
        achievements = achievements.map { ach ->
            when (ach.id) {
                1 -> if (completedCount >= 1) ach.copy(unlocked = true) else ach
                2 -> if (completedCount >= 3) ach.copy(unlocked = true) else
                    ach.copy(progress = completedCount / 3f)
                3 -> if (dailyLog.waterMl >= dailyLog.waterGoalMl) ach.copy(unlocked = true) else
                    ach.copy(progress = dailyLog.waterMl.toFloat() / dailyLog.waterGoalMl)
                else -> ach
            }
        }
    }

    // ── Profile edit ──────────────────────────────────────
    fun updateProfile(newProfile: UserProfile) {
        profile = newProfile
    }
}

// ══════════════════════════════════════════════════════════
//  DEFAULT DATA
// ══════════════════════════════════════════════════════════

fun defaultWorkouts() = listOf(
    WorkoutSession(1, "HIIT Blast",      WorkoutCategory.HIIT,     30, 380, gradient = listOf(0xFFFF6B35, 0xFFFF0080)),
    WorkoutSession(2, "Yoga Flow",       WorkoutCategory.YOGA,     45, 180, gradient = listOf(0xFF7C3AED, 0xFF3B82F6)),
    WorkoutSession(3, "Strength Pro",    WorkoutCategory.STRENGTH, 50, 420, sets = 4, reps = 12, gradient = listOf(0xFF3B82F6, 0xFF00F5A0)),
    WorkoutSession(4, "Morning Cardio",  WorkoutCategory.CARDIO,   35, 310, gradient = listOf(0xFFFF0080, 0xFFFF6B35)),
    WorkoutSession(5, "Mobility Reset",  WorkoutCategory.MOBILITY, 25, 120, gradient = listOf(0xFF00F5A0, 0xFF3B82F6)),
    WorkoutSession(6, "Power Lifting",   WorkoutCategory.STRENGTH, 60, 500, sets = 5, reps = 5,  gradient = listOf(0xFFFF6B35, 0xFF7C3AED)),
)

fun defaultAchievements() = listOf(
    Achievement(1, "First Sweat",    "Complete your first workout",      "🏅", false, 0f),
    Achievement(2, "Hat Trick",      "Complete 3 workouts",              "🎯", false, 0f),
    Achievement(3, "Hydration Hero", "Reach your daily water goal",      "💧", false, 0f),
    Achievement(4, "Early Bird",     "Workout before 8am",               "🌅", false, 0f),
    Achievement(5, "Iron Will",      "7 consecutive active days",        "🔥", true,  1f),
    Achievement(6, "10K Club",       "Walk 10,000 steps in a day",       "👟", true,  1f),
)