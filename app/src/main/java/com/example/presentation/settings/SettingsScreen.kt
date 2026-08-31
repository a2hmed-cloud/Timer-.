package com.example.presentation.settings

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.entity.ColorAccent
import com.example.data.repository.AppLanguage
import com.example.data.repository.AppThemeMode
import com.example.presentation.onboarding.AccentOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val prefs = state.preferences
    val profile = state.userProfile

    var showEditNameDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Profile Section
            SettingsSectionCard(
                title = stringResource(R.string.student_profile_title),
                icon = Icons.Default.Person
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile?.name?.ifBlank { stringResource(R.string.default_student_name) }
                                    ?: stringResource(R.string.default_student_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            val educationDetails = listOfNotNull(
                                profile?.educationCountryName,
                                profile?.educationSystemName,
                                profile?.gradeName
                            ).joinToString(" • ")

                            if (educationDetails.isNotBlank()) {
                                Text(
                                    text = educationDetails,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { showEditNameDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_name),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Theme Mode Section
            SettingsSectionCard(
                title = stringResource(R.string.settings_theme_section),
                icon = Icons.Default.DarkMode
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val themes = listOf(
                        AppThemeMode.SYSTEM to stringResource(R.string.theme_system),
                        AppThemeMode.LIGHT to stringResource(R.string.theme_light),
                        AppThemeMode.DARK to stringResource(R.string.theme_dark)
                    )
                    themes.forEach { (mode, label) ->
                        FilterChip(
                            selected = prefs.themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            label = { Text(label) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Color Accent Section
            SettingsSectionCard(
                title = stringResource(R.string.color_accent),
                icon = Icons.Default.Palette
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AccentOption(
                        name = stringResource(R.string.accent_dynamic),
                        color = MaterialTheme.colorScheme.primary,
                        isSelected = prefs.colorAccent == ColorAccent.DYNAMIC,
                        onClick = { viewModel.setColorAccent(ColorAccent.DYNAMIC) }
                    )
                    AccentOption(
                        name = stringResource(R.string.accent_blue),
                        color = Color(0xFF2563EB),
                        isSelected = prefs.colorAccent == ColorAccent.BLUE,
                        onClick = { viewModel.setColorAccent(ColorAccent.BLUE) }
                    )
                    AccentOption(
                        name = stringResource(R.string.accent_green),
                        color = Color(0xFF059669),
                        isSelected = prefs.colorAccent == ColorAccent.GREEN,
                        onClick = { viewModel.setColorAccent(ColorAccent.GREEN) }
                    )
                    AccentOption(
                        name = stringResource(R.string.accent_purple),
                        color = Color(0xFF7C3AED),
                        isSelected = prefs.colorAccent == ColorAccent.PURPLE,
                        onClick = { viewModel.setColorAccent(ColorAccent.PURPLE) }
                    )
                    AccentOption(
                        name = stringResource(R.string.accent_amber),
                        color = Color(0xFFD97706),
                        isSelected = prefs.colorAccent == ColorAccent.AMBER,
                        onClick = { viewModel.setColorAccent(ColorAccent.AMBER) }
                    )
                }
            }

            // Pomodoro Focus Setup Section
            SettingsSectionCard(
                title = stringResource(R.string.focus_timer_settings_title),
                icon = Icons.Default.Timer
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Focus duration slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.focus_duration),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${prefs.focusDurationMinutes} ${stringResource(R.string.minutes_short)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = prefs.focusDurationMinutes.toFloat(),
                            onValueChange = {
                                viewModel.setPomodoroDurations(
                                    focus = it.toInt(),
                                    shortBreak = prefs.shortBreakMinutes,
                                    longBreak = prefs.longBreakMinutes,
                                    cycleCount = prefs.sessionsBeforeLongBreak
                                )
                            },
                            valueRange = 10f..60f,
                            steps = 9
                        )
                    }

                    // Short break slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.short_break_duration),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${prefs.shortBreakMinutes} ${stringResource(R.string.minutes_short)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = prefs.shortBreakMinutes.toFloat(),
                            onValueChange = {
                                viewModel.setPomodoroDurations(
                                    focus = prefs.focusDurationMinutes,
                                    shortBreak = it.toInt(),
                                    longBreak = prefs.longBreakMinutes,
                                    cycleCount = prefs.sessionsBeforeLongBreak
                                )
                            },
                            valueRange = 3f..15f,
                            steps = 3
                        )
                    }
                }
            }

            // Language Section
            SettingsSectionCard(
                title = stringResource(R.string.settings_language_section),
                icon = Icons.Default.Language
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val languages = listOf(
                        AppLanguage.SYSTEM to stringResource(R.string.language_system),
                        AppLanguage.ENGLISH to stringResource(R.string.language_english),
                        AppLanguage.ARABIC to stringResource(R.string.language_arabic)
                    )
                    languages.forEach { (lang, label) ->
                        FilterChip(
                            selected = prefs.language == lang,
                            onClick = { viewModel.setLanguage(lang) },
                            label = { Text(label) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Notifications Section
            SettingsSectionCard(
                title = stringResource(R.string.settings_notifications_section),
                icon = Icons.Default.Notifications
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Master switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.enable_notifications),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Switch(
                            checked = prefs.notificationsEnabled,
                            onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                            modifier = Modifier.testTag("settings_master_notifications_switch")
                        )
                    }

                    if (prefs.notificationsEnabled) {
                        // Task Reminders switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.enable_task_reminders),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Switch(
                                checked = prefs.taskRemindersEnabled,
                                onCheckedChange = { viewModel.setTaskRemindersEnabled(it) }
                            )
                        }

                        // Recurring Reminders switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.enable_recurring_reminders),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Switch(
                                checked = prefs.recurringRemindersEnabled,
                                onCheckedChange = { viewModel.setRecurringRemindersEnabled(it) }
                            )
                        }

                        // Default reminder offset
                        Column {
                            Text(
                                text = stringResource(R.string.default_reminder_offset),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val offsets = listOf(
                                    5 to stringResource(R.string.reminder_5min),
                                    15 to stringResource(R.string.reminder_15min),
                                    30 to stringResource(R.string.reminder_30min),
                                    60 to stringResource(R.string.reminder_1hour)
                                )
                                offsets.forEach { (mins, label) ->
                                    FilterChip(
                                        selected = prefs.defaultReminderOffsetMinutes == mins,
                                        onClick = { viewModel.setDefaultReminderOffset(mins) },
                                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // About Section
            SettingsSectionCard(
                title = stringResource(R.string.about_title),
                icon = Icons.Default.Info
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.about_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.about_version, "2.0.0"),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(profile?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text(stringResource(R.string.edit_name)) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text(stringResource(R.string.your_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserName(newName)
                        showEditNameDialog = false
                    },
                    enabled = newName.isNotBlank()
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}
