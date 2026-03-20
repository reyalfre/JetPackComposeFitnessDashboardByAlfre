package com.example.jetpackcomposexamplebyalfre

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(vm: AppViewModel, navigate: (Screen) -> Unit) {
    val log = vm.dailyLog
    val scroll = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        // Ambient glows
        Box(
            modifier = Modifier.size(280.dp).offset((-70).dp, (-50).dp)
                .background(Brush.radialGradient(listOf(AccentPurple.copy(0.18f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier.size(220.dp).align(Alignment.BottomEnd).offset(60.dp, (-100).dp)
                .background(Brush.radialGradient(listOf(AccentNeon.copy(0.10f), Color.Transparent)), CircleShape)
        )

        Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(bottom = 16.dp)) {
            // ── Header ────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Good Morning,", color = TextSecondary, fontSize = 13.sp, letterSpacing = 1.sp)
                    Text("${vm.profile.name} 👋", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconCircle(Icons.Default.Notifications, AccentNeon) {}
                    IconCircle(Icons.Default.Search, TextSecondary) {}
                }
            }

            // ── Activity Ring ─────────────────────────────────
            val ringProgress = log.caloriesBurned.toFloat() / log.caloriesGoal
            val animRing = remember { Animatable(0f) }
            LaunchedEffect(ringProgress) {
                animRing.animateTo(ringProgress, tween(1400, easing = FastOutSlowInEasing))
            }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    Modifier.size(220.dp)
                        .background(Brush.radialGradient(listOf(AccentNeon.copy(0.05f), Color.Transparent)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ArcRingCanvas(
                        progress = animRing.value, size = 220.dp, strokeWidth = 20.dp,
                        trackColor = RingBg,
                        brush = Brush.sweepGradient(listOf(AccentNeon, AccentBlue, AccentNeon))
                    )
                    // Inner ring
                    ArcRingCanvas(
                        progress = log.activeMinutes.toFloat() / log.activeGoalMin,
                        size = 170.dp, strokeWidth = 12.dp,
                        trackColor = RingBg,
                        brush = Brush.sweepGradient(listOf(AccentPurple, AccentPink, AccentPurple))
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(animRing.value * 100).toInt()}%", color = TextPrimary, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Daily Goal", color = TextSecondary, fontSize = 12.sp, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(4.dp))
                        NeonBadge("${log.caloriesBurned} / ${log.caloriesGoal} kcal", AccentNeon)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Quick Stats ───────────────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickStat(Icons.Default.DirectionsRun, "${log.steps}", "Steps", AccentOrange, Modifier.weight(1f))
                QuickStat(Icons.Default.Favorite, "${log.heartRate}", "BPM", Color(0xFFFF4D6D), Modifier.weight(1f))
                QuickStat(Icons.Default.WaterDrop, "${log.waterMl}ml", "Water", AccentBlue, Modifier.weight(1f))
                QuickStat(Icons.Default.Bedtime, "${log.sleepHours}h", "Sleep", AccentPurple, Modifier.weight(1f))
            }

            // ── Water Tracker ─────────────────────────────────
            SectionHeader("Hydration", "Add")
            WaterTrackerCard(vm)

            // ── Weekly Chart ──────────────────────────────────
            SectionHeader("Weekly Calories", "Details")
            WeeklyChartCard(vm)

            // ── Today's Goals ─────────────────────────────────
            SectionHeader("Today's Goals", "")
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GoalProgressRow("Calories",    log.caloriesBurned,  log.caloriesGoal,    AccentOrange)
                GoalProgressRow("Active Min",  log.activeMinutes,   log.activeGoalMin,   AccentNeon)
                GoalProgressRow("Hydration",   log.waterMl / 100,   log.waterGoalMl / 100, AccentBlue)
                GoalProgressRow("Steps",       log.steps / 100,     10000 / 100,         AccentPurple)
            }
        }
    }
}

// ── Water Tracker ──────────────────────────────────────────────────────────────
@Composable
fun WaterTrackerCard(vm: AppViewModel) {
    val log = vm.dailyLog
    val fills = listOf(150, 250, 350, 500)

    Card(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${log.waterMl} ml", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("of ${log.waterGoalMl} ml goal", color = TextSecondary, fontSize = 12.sp)
                }
                // Wave progress circle
                Box(Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                    ArcRingCanvas(
                        progress = log.waterMl.toFloat() / log.waterGoalMl,
                        size = 60.dp, strokeWidth = 7.dp,
                        trackColor = RingBg,
                        brush = Brush.sweepGradient(listOf(AccentBlue, Color(0xFF00D4FF), AccentBlue))
                    )
                    Text(
                        "${(log.waterMl.toFloat() / log.waterGoalMl * 100).toInt()}%",
                        color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            // Quick add buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fills.forEach { ml ->
                    val canAdd = log.waterMl < log.waterGoalMl
                    OutlinedButton(
                        onClick = { if (canAdd) vm.addWater(ml) },
                        modifier = Modifier.weight(1f).height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (canAdd) AccentBlue else TextSecondary
                        ),
                        border = BorderStroke(1.dp, if (canAdd) AccentBlue.copy(0.4f) else RingBg),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("+${ml}ml", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ── Weekly Chart ───────────────────────────────────────────────────────────────
@Composable
fun WeeklyChartCard(vm: AppViewModel) {
    val days = vm.getWeekDayLabels()
    val values = vm.weeklyCalories
    val maxVal = values.max().toFloat()
    val todayIdx = 6

    Card(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("${values[todayIdx]} kcal today", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("avg ${values.average().toInt()} kcal/day this week", color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth().height(110.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { idx, v ->
                    val isToday = idx == todayIdx
                    val animH = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(idx * 60L)
                        animH.animateTo(v / maxVal, tween(700, easing = FastOutSlowInEasing))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Box(
                            Modifier.width(26.dp).height((animH.value * 90).dp).clip(RoundedCornerShape(7.dp))
                                .background(
                                    if (isToday) Brush.verticalGradient(listOf(AccentNeon, AccentBlue))
                                    else Brush.verticalGradient(listOf(RingBg, RingBg))
                                )
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(days[idx], color = if (isToday) AccentNeon else TextSecondary, fontSize = 11.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

// ── Reusable small components ─────────────────────────────────────────────────
@Composable
fun ArcRingCanvas(progress: Float, size: Dp, strokeWidth: Dp, trackColor: Color, brush: Brush) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
        val stroke = strokeWidth.toPx()
        val d = this.size.minDimension - stroke
        val tl = Offset(stroke / 2, stroke / 2)
        val sz = androidx.compose.ui.geometry.Size(d, d)
        drawArc(color = trackColor, startAngle = -90f, sweepAngle = 360f, useCenter = false,
            topLeft = tl, size = sz, style = Stroke(stroke, cap = StrokeCap.Round))
        drawArc(brush = brush, startAngle = -90f, sweepAngle = 360f * progress.coerceIn(0f, 1f),
            useCenter = false, topLeft = tl, size = sz, style = Stroke(stroke, cap = StrokeCap.Round))
    }
}

@Composable
fun NeonBadge(text: String, color: Color) {
    Box(
        Modifier.clip(RoundedCornerShape(20.dp)).background(color.copy(0.15f))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) { Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
}

@Composable
fun IconCircle(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier.size(42.dp).clip(CircleShape).background(CardDark)
            .border(1.dp, tint.copy(0.3f), CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
}

@Composable
fun QuickStat(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, color: Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(16.dp)).background(CardDark)
            .border(1.dp, color.copy(0.2f), RoundedCornerShape(16.dp)).padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(label, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun SectionHeader(title: String, action: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        if (action.isNotEmpty()) Text(action, color = AccentNeon, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun GoalProgressRow(label: String, current: Int, total: Int, color: Color) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(current) { anim.animateTo(current.toFloat() / total, tween(800)) }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardDark).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("$current / $total", color = TextSecondary, fontSize = 11.sp)
            }
            Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)).background(RingBg)) {
                Box(Modifier.fillMaxWidth(anim.value.coerceIn(0f, 1f)).fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brush.horizontalGradient(listOf(color, color.copy(0.6f)))))
            }
        }
        Spacer(Modifier.width(10.dp))
        Text("${(anim.value * 100).toInt()}%", color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp), textAlign = TextAlign.End)
    }
}