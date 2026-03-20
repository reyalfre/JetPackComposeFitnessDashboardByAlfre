package com.example.jetpackcomposexamplebyalfre

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(vm: AppViewModel) {
    val state = vm.timerState
    val scroll = rememberScrollState()

    // Tick every second while running
    LaunchedEffect(state.isRunning) {
        while (state.isRunning) {
            delay(1000L)
            vm.tickTimer()
        }
    }

    // Pulse animation for WORK phase
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f, targetValue = if (state.isRunning && state.phase == TimerPhase.WORK) 1.04f else 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    val phaseColor = when (state.phase) {
        TimerPhase.WORK     -> AccentNeon
        TimerPhase.REST     -> AccentBlue
        TimerPhase.FINISHED -> AccentOrange
    }

    val phaseMax = if (state.phase == TimerPhase.WORK) state.workSeconds else state.restSeconds
    val ringProgress = if (phaseMax > 0) state.secondsElapsed.toFloat() / phaseMax else 0f

    val animRing = remember { Animatable(0f) }
    LaunchedEffect(state.secondsElapsed, state.phase) {
        animRing.snapTo(ringProgress)
    }

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {

        // Background glow that changes with phase
        Box(
            Modifier.size(400.dp).align(Alignment.Center)
                .background(
                    Brush.radialGradient(listOf(phaseColor.copy(0.08f), Color.Transparent)),
                    CircleShape
                )
        )

        Column(
            Modifier.fillMaxSize().verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ── Title ─────────────────────────────────────
            Text("HIIT Timer", color = TextSecondary, fontSize = 13.sp, letterSpacing = 2.sp)
            Text(
                if (state.workoutName.isEmpty()) "Ready" else state.workoutName,
                color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(32.dp))

            // ── Round indicator ───────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 1..state.totalRounds) {
                    val done = i < state.currentRound || state.phase == TimerPhase.FINISHED
                    val current = i == state.currentRound
                    Box(
                        Modifier.size(if (current) 10.dp else 8.dp).clip(CircleShape)
                            .background(
                                when {
                                    done    -> AccentNeon
                                    current -> phaseColor
                                    else    -> RingBg
                                }
                            )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Round ${state.currentRound} / ${state.totalRounds}",
                color = TextSecondary, fontSize = 13.sp
            )

            Spacer(Modifier.height(32.dp))

            // ── Big timer ring ────────────────────────────
            Box(
                Modifier.size(260.dp).scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Glow circle
                Box(
                    Modifier.size(240.dp)
                        .background(Brush.radialGradient(listOf(phaseColor.copy(0.12f), Color.Transparent)), CircleShape)
                )
                androidx.compose.foundation.Canvas(Modifier.size(260.dp)) {
                    val stroke = 20.dp.toPx()
                    val d = size.minDimension - stroke
                    val tl = androidx.compose.ui.geometry.Offset(stroke / 2, stroke / 2)
                    val sz = androidx.compose.ui.geometry.Size(d, d)
                    drawArc(color = RingBg, startAngle = -90f, sweepAngle = 360f,
                        useCenter = false, topLeft = tl, size = sz, style = Stroke(stroke, cap = StrokeCap.Round))
                    drawArc(
                        brush = Brush.sweepGradient(listOf(phaseColor, phaseColor.copy(0.5f), phaseColor)),
                        startAngle = -90f, sweepAngle = 360f * animRing.value,
                        useCenter = false, topLeft = tl, size = sz, style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedContent(targetState = state.phase, transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }) { phase ->
                        Text(
                            when (phase) {
                                TimerPhase.WORK     -> "WORK"
                                TimerPhase.REST     -> "REST"
                                TimerPhase.FINISHED -> "DONE!"
                            },
                            color = phaseColor, fontSize = 14.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold
                        )
                    }

                    val remaining = phaseMax - state.secondsElapsed
                    val mm = remaining / 60
                    val ss = remaining % 60
                    Text(
                        "%02d:%02d".format(mm, ss),
                        color = TextPrimary, fontSize = 56.sp, fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-2).sp
                    )

                    if (state.phase == TimerPhase.FINISHED) {
                        NeonBadge("🎉 Great work!", AccentOrange)
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            // ── Controls ──────────────────────────────────
            if (state.phase != TimerPhase.FINISHED) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Reset
                    Box(
                        Modifier.size(52.dp).clip(CircleShape).background(CardDark)
                            .border(1.dp, RingBg, CircleShape).clickable { vm.resetTimer() },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Refresh, null, tint = TextSecondary, modifier = Modifier.size(24.dp)) }

                    // Play/Pause BIG
                    Box(
                        Modifier.size(80.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AccentNeon, AccentBlue)))
                            .clickable {
                                if (!state.isRunning && state.secondsElapsed == 0 && state.currentRound == 1) {
                                    vm.startTimer("HIIT Blast")
                                } else {
                                    vm.pauseResumeTimer()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            null, tint = BackgroundDark, modifier = Modifier.size(36.dp)
                        )
                    }

                    // Skip phase
                    Box(
                        Modifier.size(52.dp).clip(CircleShape).background(CardDark)
                            .border(1.dp, RingBg, CircleShape).clickable { vm.tickTimer() },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.SkipNext, null, tint = TextSecondary, modifier = Modifier.size(24.dp)) }
                }
            } else {
                // Finished state — restart button
                Button(
                    onClick = { vm.resetTimer() },
                    modifier = Modifier.width(200.dp).height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(AccentOrange, AccentPink))),
                        contentAlignment = Alignment.Center
                    ) { Text("New Session", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }

            Spacer(Modifier.height(36.dp))

            // ── Preset selector ───────────────────────────
            if (!state.isRunning && state.secondsElapsed == 0) {
                Text("Quick Presets", color = TextSecondary, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(Modifier.height(12.dp))

                val presets = listOf(
                    Triple("Tabata",    20, 10),
                    Triple("Classic",   40, 20),
                    Triple("Endurance", 60, 30),
                    Triple("Power",     45, 15),
                )
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    presets.forEach { (name, work, rest) ->
                        Column(
                            Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(CardDark)
                                .border(1.dp, RingBg, RoundedCornerShape(16.dp))
                                .clickable { vm.startTimer(name, work, rest) }
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(4.dp))
                            Text("${work}s / ${rest}s", color = AccentNeon, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}