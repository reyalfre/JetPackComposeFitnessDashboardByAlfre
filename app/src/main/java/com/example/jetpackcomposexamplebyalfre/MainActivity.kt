package com.example.jetpackcomposexamplebyalfre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposexamplebyalfre.ui.theme.JetPackComposExampleByAlfreTheme

// ══════════════════════════════════════════════════════════
//  COLOR PALETTE  (shared across all files)
// ══════════════════════════════════════════════════════════
val BackgroundDark = Color(0xFF0A0A0F)
val SurfaceDark    = Color(0xFF13131A)
val CardDark       = Color(0xFF1C1C27)
val AccentNeon     = Color(0xFF00F5A0)
val AccentPurple   = Color(0xFF7C3AED)
val AccentOrange   = Color(0xFFFF6B35)
val AccentBlue     = Color(0xFF3B82F6)
val AccentPink     = Color(0xFFFF0080)
val TextPrimary    = Color(0xFFF0F0FF)
val TextSecondary  = Color(0xFF8888AA)
val RingBg         = Color(0xFF252535)

// ══════════════════════════════════════════════════════════
//  NAV DESTINATIONS
// ══════════════════════════════════════════════════════════
enum class Screen { DASHBOARD, WORKOUTS, TIMER, PROFILE, ACHIEVEMENTS }

data class NavItem(
    val screen: Screen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

// ══════════════════════════════════════════════════════════
//  ENTRY POINT
// ══════════════════════════════════════════════════════════
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetPackComposExampleByAlfreTheme {
                FitnessApp()
            }
        }
    }
}

@Composable
fun FitnessApp() {
    val vm: AppViewModel = viewModel()
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }

    val navItems = listOf(
        NavItem(Screen.DASHBOARD,    Icons.Default.Home,         "Home"),
        NavItem(Screen.WORKOUTS,     Icons.Default.FitnessCenter,"Train"),
        NavItem(Screen.TIMER,        Icons.Default.Timer,        "Timer"),
        NavItem(Screen.ACHIEVEMENTS, Icons.Default.EmojiEvents,  "Awards"),
        NavItem(Screen.PROFILE,      Icons.Default.Person,       "Profile"),
    )

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {

        // ── Screen content ────────────────────────────────
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(tween(220)) togetherWith fadeOut(tween(180))
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) { screen ->
            when (screen) {
                Screen.DASHBOARD    -> DashboardScreen(vm) { currentScreen = it }
                Screen.WORKOUTS     -> WorkoutsScreen(vm)
                Screen.TIMER        -> TimerScreen(vm)
                Screen.ACHIEVEMENTS -> AchievementsScreen(vm)
                Screen.PROFILE      -> ProfileScreen(vm)
            }
        }

        // ── Bottom Nav Bar ────────────────────────────────
        BottomNavBar(
            items = navItems,
            current = currentScreen,
            onSelect = { currentScreen = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ══════════════════════════════════════════════════════════
//  BOTTOM NAV BAR
// ══════════════════════════════════════════════════════════
@Composable
fun BottomNavBar(
    items: List<NavItem>,
    current: Screen,
    onSelect: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, SurfaceDark.copy(alpha = 0.98f))
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                val isSelected = item.screen == current
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) AccentNeon else TextSecondary,
                    animationSpec = tween(200)
                )
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .drawBehind {
                            if (isSelected) {
                                drawCircle(
                                    color = AccentNeon.copy(alpha = 0.12f),
                                    radius = 30.dp.toPx(),
                                    center = Offset(size.width / 2, size.height / 2)
                                )
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { onSelect(item.screen) },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size((22 * scale).dp)
                        )
                    }
                    Text(
                        text = item.label,
                        color = iconColor,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
/*
package com.example.jetpackcomposexamplebyalfre

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcomposexamplebyalfre.ui.theme.JetPackComposExampleByAlfreTheme

// ── Color Palette ──────────────────────────────────────────────────────────────
val BackgroundDark   = Color(0xFF0A0A0F)
val SurfaceDark      = Color(0xFF13131A)
val CardDark         = Color(0xFF1C1C27)
val AccentNeon       = Color(0xFF00F5A0)
val AccentPurple     = Color(0xFF7C3AED)
val AccentOrange     = Color(0xFFFF6B35)
val AccentBlue       = Color(0xFF3B82F6)
val TextPrimary      = Color(0xFFF0F0FF)
val TextSecondary    = Color(0xFF8888AA)
val RingBg           = Color(0xFF252535)

// ── Entry Point ────────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetPackComposExampleByAlfreTheme {
                FitnessDashboard()
            }
        }
    }
}

// ── Root Screen ────────────────────────────────────────────────────────────────
@Composable
fun FitnessDashboard() {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Decorative ambient glow top-left
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset((-80).dp, (-60).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentPurple.copy(alpha = 0.18f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        // Decorative ambient glow bottom-right
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(80.dp, 80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(AccentNeon.copy(alpha = 0.12f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 32.dp)
        ) {
            TopBar()
            HeroActivityRing()
            QuickStatsRow()
            SectionTitle("Weekly Progress")
            WeeklyBarChart()
            SectionTitle("Active Workouts")
            WorkoutCards()
            SectionTitle("Today's Goals")
            GoalsList()
        }
    }
}

// ── Top Bar ────────────────────────────────────────────────────────────────────
@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good Morning,",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.sp
            )
            Text(
                text = "Alfre 👋",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(CardDark)
                .border(1.dp, AccentNeon.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = AccentNeon,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ── Hero: Animated Activity Ring ──────────────────────────────────────────────
@Composable
fun HeroActivityRing() {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 0.78f,
            animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing)
        )
    }
    val progress = animProgress.value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(AccentNeon.copy(alpha = 0.05f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            ArcRing(
                progress = progress,
                size = 220.dp,
                strokeWidth = 18.dp,
                trackColor = RingBg,
                progressBrush = Brush.sweepGradient(
                    listOf(AccentNeon, AccentBlue, AccentNeon)
                )
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = TextPrimary,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = "Daily Goal",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AccentNeon.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "1,560 / 2,000 kcal",
                        color = AccentNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun ArcRing(
    progress: Float,
    size: Dp,
    strokeWidth: Dp,
    trackColor: Color,
    progressBrush: Brush
) {
    Canvas(modifier = Modifier.size(size)) {
        val stroke = strokeWidth.toPx()
        val diameter = this.size.minDimension - stroke
        val topLeft = Offset(stroke / 2, stroke / 2)
        val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

        // Track
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        // Progress
        drawArc(
            brush = progressBrush,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

// ── Quick Stats ────────────────────────────────────────────────────────────────
data class StatItem(val icon: androidx.compose.ui.graphics.vector.ImageVector, val value: String, val label: String, val color: Color)

@Composable
fun QuickStatsRow() {
    val stats = listOf(
        StatItem(Icons.AutoMirrored.Filled.DirectionsRun, "8,432", "Steps",    AccentOrange),
        StatItem(Icons.Default.Favorite,      "72",    "BPM",      Color(0xFFFF4D6D)),
        StatItem(Icons.Default.WaterDrop,     "2.1L",  "Water",    AccentBlue),
        StatItem(Icons.Default.Bedtime,       "7h 20m","Sleep",    AccentPurple)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stats) { stat -> StatCard(stat) }
    }
}

@Composable
fun StatCard(stat: StatItem) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, stat.color.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(stat.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(stat.icon, contentDescription = null, tint = stat.color, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(stat.value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(stat.label, color = TextSecondary, fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}

// ── Section Title ──────────────────────────────────────────────────────────────
@Composable
fun SectionTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("See all", color = AccentNeon, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ── Weekly Bar Chart ───────────────────────────────────────────────────────────
@Composable
fun WeeklyBarChart() {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val values = listOf(0.6f, 0.85f, 0.4f, 0.95f, 0.7f, 1.0f, 0.3f)
    val today = 5

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            days.forEachIndexed { index, day ->
                val isToday = index == today
                val animHeight = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 80L)
                    animHeight.animateTo(values[index], tween(600, easing = FastOutSlowInEasing))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height((animHeight.value * 100).dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isToday)
                                    Brush.verticalGradient(listOf(AccentNeon, AccentBlue))
                                else
                                    Brush.verticalGradient(listOf(RingBg, RingBg))
                            )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        day,
                        color = if (isToday) AccentNeon else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ── Workout Cards ──────────────────────────────────────────────────────────────
data class WorkoutData(val name: String, val duration: String, val calories: String, val gradient: List<Color>)

@Composable
fun WorkoutCards() {
    val workouts = listOf(
        WorkoutData("HIIT Blast",    "30 min", "380 kcal", listOf(Color(0xFFFF6B35), Color(0xFFFF0080))),
        WorkoutData("Yoga Flow",     "45 min", "180 kcal", listOf(AccentPurple,     AccentBlue)),
        WorkoutData("Strength",      "50 min", "420 kcal", listOf(AccentBlue,       AccentNeon)),
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(workouts) { workout ->
            WorkoutCard(workout)
        }
    }
}

@Composable
fun WorkoutCard(workout: WorkoutData) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.verticalGradient(workout.gradient))
    ) {
        // Dark overlay for text legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xCC000000))
                    )
                )
        )
        // Decorative circle top-right
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopEnd)
                .offset(20.dp, (-20).dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(workout.name, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(workout.duration, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            Text(workout.calories, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}

// ── Goals List ─────────────────────────────────────────────────────────────────
data class GoalItem(val label: String, val current: Int, val total: Int, val color: Color)

@Composable
fun GoalsList() {
    val goals = listOf(
        GoalItem("Steps",    8432,  10000, AccentOrange),
        GoalItem("Calories", 1560,  2000,  AccentNeon),
        GoalItem("Hydration",2100,  3000,  AccentBlue),
        GoalItem("Active min",47,   60,    AccentPurple),
    )
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        goals.forEach { goal -> GoalRow(goal) }
    }
}

@Composable
fun GoalRow(goal: GoalItem) {
    val animW = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animW.animateTo(goal.current.toFloat() / goal.total, tween(900, easing = FastOutSlowInEasing))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(goal.color)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(goal.label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "${goal.current} / ${goal.total}",
                    color = TextSecondary, fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(RingBg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animW.value)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(listOf(goal.color, goal.color.copy(alpha = 0.6f)))
                        )
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            "${(animW.value * 100).toInt()}%",
            color = goal.color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(38.dp),
            textAlign = TextAlign.End
        )
    }
}

// ── Preview ────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FitnessDashboardPreview() {
    JetPackComposExampleByAlfreTheme {
        FitnessDashboard()
    }
}

 */