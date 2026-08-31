package com.example.presentation.focus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.entity.PomodoroSessionType
import com.example.ui.components.PriorityBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    viewModel: FocusViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val displaySeconds = (state.targetSeconds - state.elapsedSeconds).coerceAtLeast(0L)
    val minutes = displaySeconds / 60
    val seconds = displaySeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val progress = if (state.targetSeconds > 0) {
        (state.elapsedSeconds.toFloat() / state.targetSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    var taskDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.focus_timer_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("focus_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_cancel)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mode Selector Tabs (Focus / Short Break / Long Break)
            TabRow(
                selectedTabIndex = state.sessionType.ordinal,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = state.sessionType == PomodoroSessionType.FOCUS,
                    onClick = { viewModel.switchSessionType(PomodoroSessionType.FOCUS) },
                    text = { Text(stringResource(R.string.pomodoro_focus)) },
                    icon = { Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = state.sessionType == PomodoroSessionType.SHORT_BREAK,
                    onClick = { viewModel.switchSessionType(PomodoroSessionType.SHORT_BREAK) },
                    text = { Text(stringResource(R.string.pomodoro_short_break)) },
                    icon = { Icon(Icons.Default.LocalCafe, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = state.sessionType == PomodoroSessionType.LONG_BREAK,
                    onClick = { viewModel.switchSessionType(PomodoroSessionType.LONG_BREAK) },
                    text = { Text(stringResource(R.string.pomodoro_long_break)) },
                    icon = { Icon(Icons.Default.Coffee, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task Selector (optional)
            if (state.sessionType == PomodoroSessionType.FOCUS) {
                ExposedDropdownMenuBox(
                    expanded = taskDropdownExpanded,
                    onExpandedChange = { taskDropdownExpanded = !taskDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.task?.title ?: stringResource(R.string.general_study_session),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.focus_target_task)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = taskDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = taskDropdownExpanded,
                        onDismissRequest = { taskDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.general_study_session)) },
                            onClick = {
                                viewModel.selectTask(null)
                                taskDropdownExpanded = false
                            }
                        )
                        state.availableTasks.forEach { task ->
                            DropdownMenuItem(
                                text = { Text(task.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                onClick = {
                                    viewModel.selectTask(task)
                                    taskDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Big Circular Focus Timer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 10.dp,
                    color = when (state.sessionType) {
                        PomodoroSessionType.FOCUS -> MaterialTheme.colorScheme.primary
                        PomodoroSessionType.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                        PomodoroSessionType.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (state.timerState) {
                            TimerState.IDLE -> stringResource(R.string.action_ready)
                            TimerState.RUNNING -> stringResource(R.string.status_in_progress)
                            TimerState.PAUSED -> stringResource(R.string.action_pause)
                            TimerState.COMPLETED -> stringResource(R.string.status_completed)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.completedSessionsCount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.pomodoro_sessions_done, state.completedSessionsCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Finished summary card or Timer controls
            if (state.sessionSaved) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.focus_session_done),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val durationMin = ((state.elapsedSeconds + 59) / 60).toInt().coerceAtLeast(1)
                        Text(
                            text = stringResource(R.string.session_duration, durationMin),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.action_finish))
                            }
                            Button(
                                onClick = { viewModel.nextSession() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.next_session))
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (state.timerState) {
                            TimerState.IDLE -> {
                                Button(
                                    onClick = { viewModel.startTimer() },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .fillMaxWidth(0.85f)
                                        .testTag("focus_start_button")
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.action_start),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                            TimerState.RUNNING -> {
                                FilledTonalButton(
                                    onClick = { viewModel.pauseTimer() },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .weight(1f)
                                        .testTag("focus_pause_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Pause, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(stringResource(R.string.action_pause))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = { viewModel.finishSession(markTaskComplete = false) },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .weight(1f)
                                        .testTag("focus_finish_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(stringResource(R.string.action_finish))
                                }
                            }
                            TimerState.PAUSED -> {
                                Button(
                                    onClick = { viewModel.resumeTimer() },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .weight(1f)
                                        .testTag("focus_resume_button")
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(stringResource(R.string.action_resume))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                FilledTonalButton(
                                    onClick = { viewModel.finishSession(markTaskComplete = false) },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .weight(1f)
                                        .testTag("focus_finish_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(stringResource(R.string.action_finish))
                                }
                            }
                            TimerState.COMPLETED -> {}
                        }
                    }

                    if ((state.timerState == TimerState.RUNNING || state.timerState == TimerState.PAUSED) && state.task != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.finishSession(markTaskComplete = true) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Icon(imageVector = Icons.Default.DoneAll, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_complete_and_finish))
                        }
                    }
                }
            }
        }
    }
}
