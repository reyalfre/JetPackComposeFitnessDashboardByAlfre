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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ACHIEVEMENTS SCREEN
 */
@Composable
fun AchievementsScreen(vm: AppViewModel) {
    val scroll = rememberScrollState()
    val unlocked = vm.achievements.count { it.unlocked }

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(Modifier.fillMaxSize().verticalScroll(scroll)) {

            // ── Header ────────────────────────────────────
            Column(Modifier.padding(horizontal = 24.dp, vertical = 40.dp)) {
                Text("Progress", color = TextSecondary, fontSize = 13.sp, letterSpacing = 1.sp)
                Text("Achievements", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeonBadge("$unlocked / ${vm.achievements.size} Unlocked", AccentNeon)
                }
            }

            // ── Streak banner ─────────────────────────────
            Box(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(24.dp))
                    .background(Brush.horizontalGradient(listOf(AccentOrange.copy(0.8f), AccentPink.copy(0.8f))))
            ) {
                Row(
                    Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 36.sp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("7 Day Streak", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Keep going! Don't break the chain.", color = Color.White.copy(0.85f), fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Achievements grid ─────────────────────────
            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                vm.achievements.chunked(2).forEach { pair ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        pair.forEach { ach ->
                            AchievementCard(ach, Modifier.weight(1f))
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun AchievementCard(ach: Achievement, modifier: Modifier = Modifier) {
    val shimmer = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by shimmer.animateFloat(
        initialValue = -1f, targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(2200, delayMillis = 400), RepeatMode.Restart),
        label = "shimmerX"
    )

    val scale by animateFloatAsState(if (ach.unlocked) 1f else 0.96f, spring())

    Box(
        modifier.scale(scale).clip(RoundedCornerShape(20.dp))
            .background(
                if (ach.unlocked)
                    Brush.linearGradient(listOf(CardDark, Color(0xFF2A2A3A)))
                else Brush.linearGradient(listOf(CardDark, CardDark))
            )
            .border(
                1.dp,
                if (ach.unlocked) AccentNeon.copy(0.4f) else RingBg,
                RoundedCornerShape(20.dp)
            )
    ) {
        if (ach.unlocked) {
            // Shimmer highlight
            Box(
                Modifier.matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(0.04f), Color.Transparent),
                            start = androidx.compose.ui.geometry.Offset(shimmerX * 200, 0f),
                            end = androidx.compose.ui.geometry.Offset(shimmerX * 200 + 100, 200f)
                        )
                    )
            )
        }

        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                ach.icon,
                fontSize = 32.sp,
                modifier = Modifier.graphicsLayer(alpha = if (ach.unlocked) 1f else 0.3f)
            )
            Spacer(Modifier.height(8.dp))
            Text(ach.title, color = if (ach.unlocked) TextPrimary else TextSecondary,
                fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(ach.description, color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)

            if (!ach.unlocked && ach.progress > 0f) {
                Spacer(Modifier.height(10.dp))
                val anim = remember { Animatable(0f) }
                LaunchedEffect(ach.progress) { anim.animateTo(ach.progress, tween(800)) }
                Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(RingBg)) {
                    Box(Modifier.fillMaxWidth(anim.value).fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp)).background(AccentPurple))
                }
                Text("${(ach.progress * 100).toInt()}%", color = AccentPurple, fontSize = 10.sp)
            }
        }
    }
}

/**
 * PROFILE SCREEN
 */
@Composable
fun ProfileScreen(vm: AppViewModel) {
    var editing by remember { mutableStateOf(false) }
    var draftName by remember { mutableStateOf(vm.profile.name) }
    var draftWeight by remember { mutableStateOf(vm.profile.weight.toString()) }
    var draftHeight by remember { mutableStateOf(vm.profile.height.toString()) }
    var draftGoal by remember { mutableStateOf(vm.profile.weeklyGoal.toString()) }

    val scroll = rememberScrollState()

    Box(Modifier.fillMaxSize().background(BackgroundDark)) {
        // Purple glow top
        Box(
            Modifier.size(260.dp).align(Alignment.TopCenter).offset(y = (-60).dp)
                .background(Brush.radialGradient(listOf(AccentPurple.copy(0.2f), Color.Transparent)), CircleShape)
        )

        Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
            Spacer(Modifier.height(40.dp))

            // ── Avatar + Name ─────────────────────────────
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(96.dp).clip(CircleShape)
                        .background(Brush.linearGradient(listOf(AccentPurple, AccentBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(vm.profile.name.take(1).uppercase(), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(12.dp))
                Text(vm.profile.name, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Fitness Enthusiast", color = TextSecondary, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                NeonBadge("Pro Member 🏆", AccentOrange)
            }

            Spacer(Modifier.height(24.dp))

            // ── Stats row ─────────────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Triple("${vm.profile.weight}kg", "Weight", AccentOrange),
                    Triple("${vm.profile.height}cm", "Height", AccentBlue),
                    Triple("${vm.profile.weeklyGoal}x", "Weekly Goal", AccentNeon),
                    Triple("${vm.profile.age}y", "Age", AccentPurple),
                ).forEach { (value, label, color) ->
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(CardDark)
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(label, color = TextSecondary, fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── BMI card ──────────────────────────────────
            val bmi = vm.profile.weight / ((vm.profile.height / 100f) * (vm.profile.height / 100f))
            val bmiLabel = when {
                bmi < 18.5 -> "Underweight"
                bmi < 25f  -> "Normal ✓"
                bmi < 30f  -> "Overweight"
                else       -> "Obese"
            }
            val bmiColor = if (bmi < 25f) AccentNeon else AccentOrange

            Card(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("BMI Index", color = TextSecondary, fontSize = 12.sp, letterSpacing = 1.sp)
                        Text("%.1f".format(bmi), color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    NeonBadge(bmiLabel, bmiColor)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Edit section ──────────────────────────────
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Info", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    if (editing) {
                        vm.updateProfile(UserProfile(
                            name = draftName,
                            weight = draftWeight.toFloatOrNull() ?: vm.profile.weight,
                            height = draftHeight.toIntOrNull() ?: vm.profile.height,
                            weeklyGoal = draftGoal.toIntOrNull() ?: vm.profile.weeklyGoal,
                            age = vm.profile.age
                        ))
                    } else {
                        draftName = vm.profile.name
                        draftWeight = vm.profile.weight.toString()
                        draftHeight = vm.profile.height.toString()
                        draftGoal = vm.profile.weeklyGoal.toString()
                    }
                    editing = !editing
                }) {
                    Text(if (editing) "Save ✓" else "Edit", color = AccentNeon, fontWeight = FontWeight.SemiBold)
                }
            }

            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileField("Name",        draftName,   editing) { draftName = it }
                ProfileField("Weight (kg)", draftWeight, editing) { draftWeight = it }
                ProfileField("Height (cm)", draftHeight, editing) { draftHeight = it }
                ProfileField("Weekly Goal", draftGoal,   editing) { draftGoal = it }
            }

            Spacer(Modifier.height(20.dp))

            // ── Settings list ─────────────────────────────
            Text("Settings", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))

            val settings = listOf(
                Triple(Icons.Default.Notifications, "Notifications", "Daily reminders"),
                Triple(Icons.Default.DarkMode,      "Dark Mode",     "Enabled"),
                Triple(Icons.Default.Language,      "Language",      "English"),
                Triple(Icons.Default.Security,      "Privacy",       "Protected"),
            )
            Column(
                Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                settings.forEach { (icon, label, subtitle) ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardDark).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, null, tint = AccentPurple, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(subtitle, color = TextSecondary, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * ProfileField
 */
@Composable
fun ProfileField(label: String, value: String, editable: Boolean, onValueChange: (String) -> Unit) {
    if (editable) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentNeon,
                unfocusedBorderColor = RingBg,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = AccentNeon,
                unfocusedContainerColor = CardDark,
                focusedContainerColor = CardDark
            )
        )
    } else {
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(CardDark).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextSecondary, fontSize = 14.sp)
            Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}