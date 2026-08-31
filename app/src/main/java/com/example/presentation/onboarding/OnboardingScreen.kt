package com.example.presentation.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.entity.ColorAccent
import com.example.data.repository.AppThemeMode
import com.example.domain.model.CatalogSubject
import com.example.domain.model.EducationCountry
import com.example.domain.model.EducationGrade
import com.example.domain.model.EducationSystem
import com.example.ui.theme.parseSubjectColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onComplete: (openAddTask: Boolean) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.setNotificationsAllowed(isGranted)
            viewModel.nextStep()
        }
    )

    Scaffold(
        topBar = {
            if (state.currentStep > 1) {
                TopAppBar(
                    title = {
                        Text(
                            text = "${state.currentStep} / ${state.totalSteps}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.previousStep() },
                            modifier = Modifier.testTag("onboarding_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (state.currentStep > 1 && state.currentStep < 10) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.previousStep() },
                            modifier = Modifier.testTag("onboarding_bottom_prev")
                        ) {
                            Text(stringResource(R.string.back))
                        }

                        Button(
                            onClick = {
                                if (state.currentStep == 8 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.nextStep()
                                }
                            },
                            modifier = Modifier.testTag("onboarding_bottom_next")
                        ) {
                            Text(stringResource(R.string.next))
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Progress Indicator
            LinearProgressIndicator(
                progress = { state.currentStep.toFloat() / state.totalSteps.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "onboarding_steps_anim",
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { step ->
                when (step) {
                    1 -> Step1Welcome(onStart = { viewModel.nextStep() })
                    2 -> Step2Name(
                        name = state.name,
                        onNameChange = { viewModel.setName(it) },
                        onNext = { viewModel.nextStep() }
                    )
                    3 -> Step3Country(
                        countries = state.availableCountries,
                        selectedCountry = state.selectedCountry,
                        onSelectCountry = { viewModel.selectCountry(it) }
                    )
                    4 -> Step4SystemAndGrade(
                        country = state.selectedCountry,
                        selectedSystem = state.selectedSystem,
                        selectedGrade = state.selectedGrade,
                        onSelectSystem = { viewModel.selectSystem(it) },
                        onSelectGrade = { viewModel.selectGrade(it) }
                    )
                    5 -> Step5CatalogPreview(
                        grade = state.selectedGrade,
                        catalogSubjects = state.selectedCatalogSubjects,
                        onToggle = { viewModel.toggleCatalogSubject(it) },
                        onAddCustom = { name, color -> viewModel.addCustomSubject(name, color) }
                    )
                    6 -> Step6CustomizeSubjects(
                        catalogSubjects = state.selectedCatalogSubjects,
                        customSubjects = state.customSubjects,
                        onToggleCatalog = { viewModel.toggleCatalogSubject(it) },
                        onAddCustom = { name, color -> viewModel.addCustomSubject(name, color) },
                        onRemoveCustom = { viewModel.removeCustomSubject(it) }
                    )
                    7 -> Step7ThemeAndAccent(
                        selectedTheme = state.selectedThemeMode,
                        selectedAccent = state.selectedColorAccent,
                        onThemeChange = { viewModel.setThemeMode(it) },
                        onAccentChange = { viewModel.setColorAccent(it) }
                    )
                    8 -> Step8Notifications(
                        notificationsAllowed = state.notificationsAllowed,
                        onToggleNotifications = { viewModel.setNotificationsAllowed(it) },
                        onRequestSystemPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.nextStep()
                            }
                        },
                        onSkip = {
                            viewModel.setNotificationsAllowed(false)
                            viewModel.nextStep()
                        }
                    )
                    9 -> Step9Pomodoro(
                        focus = state.focusDurationMinutes,
                        shortBreak = state.shortBreakMinutes,
                        longBreak = state.longBreakMinutes,
                        cycle = state.sessionsBeforeLongBreak,
                        onUpdate = { f, sb, lb, c -> viewModel.setPomodoroDurations(f, sb, lb, c) }
                    )
                    10 -> Step10FirstTask(
                        isSaving = state.isSaving,
                        onAddFirstTask = {
                            viewModel.finishOnboarding(
                                onSuccess = { openAdd -> onComplete(openAdd) },
                                startWithNewTask = true
                            )
                        },
                        onSkipFirstTask = {
                            viewModel.finishOnboarding(
                                onSuccess = { openAdd -> onComplete(openAdd) },
                                startWithNewTask = false
                            )
                        }
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: Welcome
// -------------------------------------------------------------
@Composable
fun Step1Welcome(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_welcome_tagline),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_welcome_subtext),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("onboarding_start_btn"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.get_started),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

// -------------------------------------------------------------
// STEP 2: User Name
// -------------------------------------------------------------
@Composable
fun Step2Name(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.onboarding_name_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_name_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.your_name)) },
            placeholder = { Text(stringResource(R.string.enter_your_name)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { if (name.isNotBlank()) onNext() }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("onboarding_name_input"),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// -------------------------------------------------------------
// STEP 3: Education Country / Region
// -------------------------------------------------------------
@Composable
fun Step3Country(
    countries: List<EducationCountry>,
    selectedCountry: EducationCountry?,
    onSelectCountry: (EducationCountry) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.onboarding_country_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_country_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(countries) { country ->
                val isSelected = selectedCountry?.id == country.id
                Card(
                    onClick = { onSelectCountry(country) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("country_item_${country.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = country.icon,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = country.nameAr,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = country.nameEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4: Education System & Grade
// -------------------------------------------------------------
@Composable
fun Step4SystemAndGrade(
    country: EducationCountry?,
    selectedSystem: EducationSystem?,
    selectedGrade: EducationGrade?,
    onSelectSystem: (EducationSystem) -> Unit,
    onSelectGrade: (EducationGrade) -> Unit
) {
    val systems = country?.systems ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.onboarding_grade_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_grade_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (systems.size > 1) {
            Text(
                text = stringResource(R.string.education_system),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            systems.forEach { sys ->
                val isSysSelected = selectedSystem?.id == sys.id
                Card(
                    onClick = { onSelectSystem(sys) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSysSelected) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = sys.nameAr,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (sys.descriptionAr.isNotBlank()) {
                                Text(
                                    text = sys.descriptionAr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (isSysSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        val grades = selectedSystem?.grades ?: emptyList()
        if (grades.isNotEmpty()) {
            Text(
                text = stringResource(R.string.grade_level),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            grades.forEach { gr ->
                val isGradeSelected = selectedGrade?.id == gr.id
                Card(
                    onClick = { onSelectGrade(gr) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("grade_item_${gr.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isGradeSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isGradeSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = gr.nameAr,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (isGradeSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 5: Catalog Subjects Preview
// -------------------------------------------------------------
@Composable
fun Step5CatalogPreview(
    grade: EducationGrade?,
    catalogSubjects: List<CatalogSubject>,
    onToggle: (CatalogSubject) -> Unit,
    onAddCustom: (String, Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (catalogSubjects.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.onboarding_subjects_auto_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.onboarding_subjects_auto_subtitle, grade?.nameAr ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(catalogSubjects) { sub ->
                    SubjectRowItem(
                        title = sub.nameAr,
                        color = parseSubjectColor(sub.colorHex),
                        isSelected = true,
                        onToggle = { onToggle(sub) }
                    )
                }
            }
        } else {
            // Empty catalog fallback (e.g. University, Custom System)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.onboarding_subjects_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.onboarding_subjects_empty_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("onboarding_add_custom_subject_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_subject))
                }
            }
        }
    }

    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                onAddCustom(name, color)
                showAddDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// STEP 6: Customize Subjects
// -------------------------------------------------------------
@Composable
fun Step6CustomizeSubjects(
    catalogSubjects: List<CatalogSubject>,
    customSubjects: List<com.example.data.entity.Subject>,
    onToggleCatalog: (CatalogSubject) -> Unit,
    onAddCustom: (String, Long) -> Unit,
    onRemoveCustom: (com.example.data.entity.Subject) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.onboarding_customize_subjects_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.onboarding_customize_subjects_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.testTag("customize_add_subject_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.add))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(catalogSubjects) { catSub ->
                SubjectRowItem(
                    title = catSub.nameAr,
                    color = parseSubjectColor(catSub.colorHex),
                    isSelected = true,
                    onToggle = { onToggleCatalog(catSub) }
                )
            }

            items(customSubjects) { customSub ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(parseSubjectColor(customSub.color, MaterialTheme.colorScheme.primary))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = customSub.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onRemoveCustom(customSub) }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                onAddCustom(name, color)
                showAddDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// STEP 7: Theme & Color Accent
// -------------------------------------------------------------
@Composable
fun Step7ThemeAndAccent(
    selectedTheme: AppThemeMode,
    selectedAccent: ColorAccent,
    onThemeChange: (AppThemeMode) -> Unit,
    onAccentChange: (ColorAccent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.onboarding_theme_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_theme_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.theme_mode),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeOptionCard(
                title = stringResource(R.string.theme_system),
                icon = Icons.Default.SettingsBrightness,
                isSelected = selectedTheme == AppThemeMode.SYSTEM,
                modifier = Modifier.weight(1f),
                onClick = { onThemeChange(AppThemeMode.SYSTEM) }
            )
            ThemeOptionCard(
                title = stringResource(R.string.theme_light),
                icon = Icons.Default.LightMode,
                isSelected = selectedTheme == AppThemeMode.LIGHT,
                modifier = Modifier.weight(1f),
                onClick = { onThemeChange(AppThemeMode.LIGHT) }
            )
            ThemeOptionCard(
                title = stringResource(R.string.theme_dark),
                icon = Icons.Default.DarkMode,
                isSelected = selectedTheme == AppThemeMode.DARK,
                modifier = Modifier.weight(1f),
                onClick = { onThemeChange(AppThemeMode.DARK) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.color_accent),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AccentOption(
                name = stringResource(R.string.accent_dynamic),
                color = MaterialTheme.colorScheme.primary,
                isSelected = selectedAccent == ColorAccent.DYNAMIC,
                onClick = { onAccentChange(ColorAccent.DYNAMIC) }
            )
            AccentOption(
                name = stringResource(R.string.accent_blue),
                color = Color(0xFF2563EB),
                isSelected = selectedAccent == ColorAccent.BLUE,
                onClick = { onAccentChange(ColorAccent.BLUE) }
            )
            AccentOption(
                name = stringResource(R.string.accent_green),
                color = Color(0xFF059669),
                isSelected = selectedAccent == ColorAccent.GREEN,
                onClick = { onAccentChange(ColorAccent.GREEN) }
            )
            AccentOption(
                name = stringResource(R.string.accent_purple),
                color = Color(0xFF7C3AED),
                isSelected = selectedAccent == ColorAccent.PURPLE,
                onClick = { onAccentChange(ColorAccent.PURPLE) }
            )
            AccentOption(
                name = stringResource(R.string.accent_amber),
                color = Color(0xFFD97706),
                isSelected = selectedAccent == ColorAccent.AMBER,
                onClick = { onAccentChange(ColorAccent.AMBER) }
            )
        }
    }
}

// -------------------------------------------------------------
// STEP 8: Notifications Context & Permission
// -------------------------------------------------------------
@Composable
fun Step8Notifications(
    notificationsAllowed: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onRequestSystemPermission: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_notifications_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_notifications_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onRequestSystemPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("enable_notifications_btn"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.enable_notifications),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onSkip,
            modifier = Modifier.testTag("skip_notifications_btn")
        ) {
            Text(stringResource(R.string.not_now))
        }
    }
}

// -------------------------------------------------------------
// STEP 9: Pomodoro Focus Setup
// -------------------------------------------------------------
@Composable
fun Step9Pomodoro(
    focus: Int,
    shortBreak: Int,
    longBreak: Int,
    cycle: Int,
    onUpdate: (focus: Int, shortBreak: Int, longBreak: Int, cycle: Int) -> Unit
) {
    var focusDuration by remember { mutableIntStateOf(focus) }
    var shortBreakDuration by remember { mutableIntStateOf(shortBreak) }
    var longBreakDuration by remember { mutableIntStateOf(longBreak) }
    var cycleCount by remember { mutableIntStateOf(cycle) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.onboarding_pomodoro_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_pomodoro_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Focus Slider
        PomodoroSliderRow(
            title = stringResource(R.string.focus_duration),
            value = focusDuration,
            valueSuffix = stringResource(R.string.minutes_short),
            range = 10f..60f,
            steps = 9,
            onValueChange = {
                focusDuration = it.toInt()
                onUpdate(focusDuration, shortBreakDuration, longBreakDuration, cycleCount)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Short Break Slider
        PomodoroSliderRow(
            title = stringResource(R.string.short_break_duration),
            value = shortBreakDuration,
            valueSuffix = stringResource(R.string.minutes_short),
            range = 3f..15f,
            steps = 3,
            onValueChange = {
                shortBreakDuration = it.toInt()
                onUpdate(focusDuration, shortBreakDuration, longBreakDuration, cycleCount)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Long Break Slider
        PomodoroSliderRow(
            title = stringResource(R.string.long_break_duration),
            value = longBreakDuration,
            valueSuffix = stringResource(R.string.minutes_short),
            range = 10f..30f,
            steps = 3,
            onValueChange = {
                longBreakDuration = it.toInt()
                onUpdate(focusDuration, shortBreakDuration, longBreakDuration, cycleCount)
            }
        )
    }
}

// -------------------------------------------------------------
// STEP 10: First Task Prompt
// -------------------------------------------------------------
@Composable
fun Step10FirstTask(
    isSaving: Boolean,
    onAddFirstTask: () -> Unit,
    onSkipFirstTask: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.onboarding_ready_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.onboarding_ready_desc),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onAddFirstTask,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("onboarding_add_first_task_btn"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.add_first_task),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onSkipFirstTask,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("onboarding_start_later_btn"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.start_later),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// -------------------------------------------------------------
// Helper UI Components
// -------------------------------------------------------------
@Composable
fun SubjectRowItem(
    title: String,
    color: Color,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun ThemeOptionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 2.dp) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AccentOption(
    name: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun PomodoroSliderRow(
    title: String,
    value: Int,
    valueSuffix: String,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$value $valueSuffix",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = value.toFloat(),
                onValueChange = onValueChange,
                valueRange = range,
                steps = steps
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorHex: Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0xFF4338CA) }
    val colors = listOf(
        0xFF4338CA, 0xFF0D9488, 0xFF2563EB, 0xFF16A34A,
        0xFFD97706, 0xFFE11D48, 0xFF9333EA, 0xFFEA580C
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_subject)) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.subject_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.subject_color),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { colorHex ->
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(parseSubjectColor(colorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), selectedColor) },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
