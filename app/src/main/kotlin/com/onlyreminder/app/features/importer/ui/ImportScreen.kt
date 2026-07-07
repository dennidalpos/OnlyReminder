package com.onlyreminder.app.features.importer.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.onlyreminder.app.R
import com.onlyreminder.app.core.ui.components.OnlyReminderTopBar
import com.onlyreminder.app.features.importer.domain.ContactField
import com.onlyreminder.app.features.importer.domain.ImportContact
import com.onlyreminder.app.features.importer.domain.ImportError
import com.onlyreminder.app.features.importer.domain.RawImportRow

@Composable
fun ImportError.toDisplayName(): String {
    return when (this) {
        ImportError.MISSING_DISPLAY_NAME -> stringResource(id = R.string.missing_display_name)
        ImportError.MISSING_PHONE_NUMBER -> stringResource(id = R.string.missing_phone_number)
    }
}

@Composable
fun ImportScreen(
    navController: NavController,
    viewModel: ImportViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            OnlyReminderTopBar(
                title = stringResource(id = R.string.import_contacts),
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep is ImportViewModel.ImportStep.SelectFile) {
                            navController.navigateUp()
                        } else {
                            viewModel.reset()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            when (val step = currentStep) {
                is ImportViewModel.ImportStep.SelectFile -> {
                    SelectFileStep(onFileSelected = { viewModel.loadFile(it) })
                }

                is ImportViewModel.ImportStep.PreviewAndMapping -> {
                    MappingStep(
                        rows = step.rows,
                        onConfirm = { mapping -> viewModel.processMapping(step.rows, mapping) }
                    )
                }

                is ImportViewModel.ImportStep.ValidationAndDeduplication -> {
                    ValidationStep(
                        contacts = step.contacts,
                        onConfirm = { skipDuplicates ->
                            viewModel.confirmImport(
                                step.contacts,
                                skipDuplicates
                            )
                        }
                    )
                }

                is ImportViewModel.ImportStep.Summary -> {
                    // Temporarily just show success or a simple text
                    SuccessStep(onFinish = { navController.navigateUp() })
                }

                is ImportViewModel.ImportStep.Success -> {
                    SuccessStep(onFinish = { navController.navigateUp() })
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            error?.let {
                AlertDialog(
                    onDismissRequest = { viewModel.reset() },
                    title = { Text(stringResource(id = R.string.error_title)) },
                    text = { Text(it.asString()) },
                    confirmButton = {
                        Button(onClick = { viewModel.reset() }) {
                            Text(stringResource(id = R.string.ok))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SelectFileStep(onFileSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onFileSelected(it) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.FileUpload,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(id = R.string.select_file),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            launcher.launch(
                arrayOf(
                    "text/comma-separated-values",
                    "text/csv",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/json",
                    "text/xml",
                    "application/xml"
                )
            )
        }) {
            Text(stringResource(id = R.string.pick_file))
        }
    }
}

@Composable
fun MappingStep(rows: List<RawImportRow>, onConfirm: (Map<Int, ContactField?>) -> Unit) {
    val header = rows.firstOrNull()?.data ?: emptyList()
    val mapping = remember { mutableStateMapOf<Int, ContactField?>() }

    // Auto-mapping logic
    LaunchedEffect(header) {
        header.forEachIndexed { index, colName ->
            val field = ContactField.entries.find {
                it.displayName.equals(colName, ignoreCase = true) ||
                        it.name.replace("_", "").equals(colName.replace(" ", ""), ignoreCase = true)
            }
            if (field != null) mapping[index] = field
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            stringResource(id = R.string.map_columns),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(header.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = header[index],
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(onClick = { expanded = true }) {
                            Text(
                                mapping[index]?.displayName ?: stringResource(id = R.string.ignore)
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.ignore)) },
                                onClick = { mapping[index] = null; expanded = false }
                            )
                            ContactField.entries.forEach { field ->
                                DropdownMenuItem(
                                    text = { Text(field.displayName) },
                                    onClick = { mapping[index] = field; expanded = false }
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onConfirm(mapping.toMap()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.next_validate))
        }
    }
}

@Composable
fun ValidationStep(contacts: List<ImportContact>, onConfirm: (Boolean) -> Unit) {
    var skipDuplicates by remember { mutableStateOf(true) }
    val validCount = contacts.count { it.isValid }
    val duplicateCount = contacts.count { it.isDuplicate }
    val errorCount = contacts.count { !it.isValid }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            stringResource(id = R.string.import_preview),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(stringResource(id = R.string.total_contacts, contacts.size))
                Text(
                    stringResource(id = R.string.valid_contacts, validCount),
                    color = Color(0xFF4CAF50)
                )
                Text(
                    stringResource(id = R.string.duplicate_contacts, duplicateCount),
                    color = Color(0xFFFFA000)
                )
                Text(
                    stringResource(id = R.string.error_contacts, errorCount),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = skipDuplicates, onCheckedChange = { skipDuplicates = it })
            Text(stringResource(id = R.string.skip_duplicates))
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(contacts) { contact ->
                ListItem(
                    headlineContent = { Text(contact.displayName) },
                    supportingContent = {
                        Column {
                            Text(contact.phone)
                            if (contact.isDuplicate) {
                                Text(
                                    contact.duplicateReason ?: "Duplicate",
                                    color = Color(0xFFFFA000),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (!contact.isValid) {
                                contact.validationErrors.forEach { error ->
                                    Text(
                                        text = error.toDisplayName(),
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    },
                    trailingContent = {
                        if (contact.isValid && (!contact.isDuplicate || !skipDuplicates)) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onConfirm(skipDuplicates) },
            modifier = Modifier.fillMaxWidth(),
            enabled = validCount > 0
        ) {
            Text(stringResource(id = R.string.confirm_import))
        }
    }
}

@Composable
fun SuccessStep(onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .background(Color(0xFF4CAF50), shape = MaterialTheme.shapes.extraLarge)
                .padding(16.dp),
            tint = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            stringResource(id = R.string.import_success),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onFinish) {
            Text(stringResource(id = R.string.back_to_contacts))
        }
    }
}
