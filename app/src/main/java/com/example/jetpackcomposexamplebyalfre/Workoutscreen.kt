package com.example.jetpackcomposexamplebyalfre

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WorkoutsScreen(vm: AppViewModel) {
    var selectedCategory by remember { mutableStateOf<WorkoutCategory?>(null) }
    var expandedId by remember { mutableStateOf<Int?>(null) }

    val filtered = if (selectedCategory == null) vm.workouts
    else vm.workouts.filter { it.category == selectedCategory }

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {

            // ── Header ────────────────────────────────────
            item {
                Column(Modifier.padding(horizontal = 24.dp, vertical = 40.dp)) {
                    Text("Training", color = TextSecondary, fontSize = 13.sp, letterSpacing = 1.sp)
                    Text("Workouts", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    val done = vm.workouts.count { it.completed }
                    Text("$done / ${vm.workouts.size} completed today", color = AccentNeon, fontSize = 13.sp)
                }
            }

            // ── Category Filter ───────────────────────────
            item {
                val cats = listOf(null) + WorkoutCategory.entries
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cats) { cat ->
                        val selected = cat == selectedCategory
                        Box(
                            Modifier.clip(RoundedCornerShape(20.dp))
                                .background(if (selected) AccentNeon else CardDark)
                                .border(1.dp, if (selected) AccentNeon else RingBg, RoundedCornerShape(20.dp))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                cat?.label ?: "All",
                                color = if (selected) BackgroundDark else TextSecondary,
                                fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── Workout Cards ─────────────────────────────
            items(filtered, key = { it.id }) { workout ->
                WorkoutExpandableCard(
                    workout = workout,
                    expanded = expandedId == workout.id,
                    onToggle = { expandedId = if (expandedId == workout.id) null else workout.id },
                    onComplete = { vm.completeWorkout(workout.id) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun WorkoutExpandableCard(
    workout: WorkoutSession,
    expanded: Boolean,
    onToggle: () -> Unit,
    onComplete: () -> Unit
) {
    val borderColor by animateColorAsState(
        if (workout.completed) AccentNeon.copy(0.5f) else RingBg,
        tween(400)
    )

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, borderColor, RoundedCornerShape(22.dp))
            .background(CardDark)
    ) {
        // ── Card Header ───────────────────────────────────
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gradient icon box
            Box(
                Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                    .background(Brush.verticalGradient(workout.gradient.map { Color(it) })),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (workout.category) {
                        WorkoutCategory.HIIT     -> Icons.Default.Bolt
                        WorkoutCategory.STRENGTH -> Icons.Default.FitnessCenter
                        WorkoutCategory.YOGA     -> Icons.Default.SelfImprovement
                        WorkoutCategory.CARDIO   -> Icons.Default.DirectionsRun
                        WorkoutCategory.MOBILITY -> Icons.Default.AccessibilityNew
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(workout.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${workout.durationMin} min  •  ${workout.caloriesBurned} kcal  •  ${workout.category.label}",
                    color = TextSecondary, fontSize = 12.sp
                )
            }
            if (workout.completed) {
                Icon(Icons.Default.CheckCircle, null, tint = AccentNeon, modifier = Modifier.size(24.dp))
            } else {
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    null, tint = TextSecondary, modifier = Modifier.size(24.dp)
                )
            }
        }

        // ── Expanded Detail ───────────────────────────────
        AnimatedVisibility(visible = expanded && !workout.completed) {
            Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                HorizontalDivider(color = RingBg, modifier = Modifier.padding(bottom = 14.dp))

                // Stat chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOfNotNull(
                        "⏱ ${workout.durationMin} min",
                        "🔥 ${workout.caloriesBurned} kcal",
                        if (workout.sets > 0) "💪 ${workout.sets}×${workout.reps}" else null
                    ).forEach { chip ->
                        Box(
                            Modifier.clip(RoundedCornerShape(10.dp)).background(RingBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) { Text(chip, color = TextSecondary, fontSize = 12.sp) }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Fake exercise list
                val exercises = when (workout.category) {
                    WorkoutCategory.HIIT     -> listOf("Burpees", "Mountain Climbers", "Jump Squats", "High Knees")
                    WorkoutCategory.STRENGTH -> listOf("Bench Press", "Deadlift", "Squat", "Overhead Press")
                    WorkoutCategory.YOGA     -> listOf("Sun Salutation", "Warrior I", "Downward Dog", "Child's Pose")
                    WorkoutCategory.CARDIO   -> listOf("Warm-up Run", "Intervals x6", "Cool-down Walk")
                    WorkoutCategory.MOBILITY -> listOf("Hip Circles", "Shoulder Rolls", "Spinal Twist", "Pigeon Pose")
                }
                exercises.forEachIndexed { i, ex ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(22.dp).clip(CircleShape)
                                .background(Brush.linearGradient(workout.gradient.map { Color(it) })),
                            contentAlignment = Alignment.Center
                        ) { Text("${i+1}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.width(10.dp))
                        Text(ex, color = TextPrimary, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Complete button
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(Brush.horizontalGradient(workout.gradient.map { Color(it) })),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Start & Complete", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}