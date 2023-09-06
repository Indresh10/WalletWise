@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.walletwise.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walletwise.data.model.ImageType
import com.example.walletwise.data.model.Tag
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.navigation.TipRow
import com.example.walletwise.ui.theme.WalletWiseTheme

object AddTransactionDestination : NavigationDestination {
    override val route: String
        get() = "add_transaction"
    override val title: String = ""

    fun getTitle(positive: Boolean): String = if (positive) "Income" else "Expense"

    const val accIdArg = "accId"
    const val typeArg = "type"
    val routeWithArgs = "$route/{$accIdArg}/{$typeArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var visibleNotes by rememberSaveable {
        mutableStateOf(false)
    }
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val tagType = viewModel.getType()
    var open by rememberSaveable {
        mutableStateOf(false)
    }
    val dateState = rememberDatePickerState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        InputAndDateRow(
            dateText = uiState.date,
            onDateTextChange = {
                open = true
            },
            inputText = uiState.amount,
            onInputTextChange = { viewModel.updateAmount(it) },
            notesText = uiState.notes ?: "",
            onNotesTextChange = { viewModel.updateNotes(it) },
            visible = visibleNotes,
            onDropClick = { visibleNotes = !visibleNotes },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (tagType) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
                )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Tags", style = MaterialTheme.typography.titleLarge)
        TagChipsList(tags = tags, onTagClick = {
            if (viewModel.validateTransaction()) {
                viewModel.saveTransactions(it)
                navigateBack()
            }
        }, modifier = Modifier.padding(16.dp))
        TipRow(message = "Use '⁝' to add new Tags")
        Text(
            text = "Enter the amount and select the tag to save transaction",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (open) {
            DatePickerDialog(
                onDismissRequest = { open = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.validateAndUpdateDate(
                            dateState.selectedDateMillis!!
                        )
                        open = false
                    }, enabled = dateState.selectedDateMillis != null) {
                        Text(text = "Ok")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { open = false }) {
                        Text(text = "Cancel")
                    }
                }) {
                DatePicker(state = dateState)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    WalletWiseTheme {
        AddTransactionScreen({})
    }
}

@Composable
fun InputAndDateRow(
    dateText: String,
    onDateTextChange: () -> Unit,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    notesText: String,
    onNotesTextChange: (String) -> Unit,
    visible: Boolean,
    onDropClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Bottom) {
            Button(onClick = onDateTextChange, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = dateText)
            }
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                modifier = Modifier.weight(1f),
                label = {
                    Text(text = "Amount")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                )

            )
            IconButton(onClick = onDropClick) {
                Icon(
                    imageVector = if (!visible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Notes:$visible"
                )
            }
        }
        AnimatedVisibility(visible = visible) {
            Column {
                OutlinedTextField(
                    value = notesText,
                    onValueChange = onNotesTextChange,
                    label = {
                        Text(text = "Notes")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        disabledContainerColor = MaterialTheme.colorScheme.background,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TagChipsList(tags: List<Tag>, onTagClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        items(tags) { tag ->
            AssistChip(
                onClick = { onTagClick(tag.id) },
                label = { Text(text = tag.name, overflow = TextOverflow.Ellipsis, maxLines = 1) },
                leadingIcon = {
                    Icon(
                        imageVector = ImageType.values()[tag.icon].res,
                        contentDescription = tag.name
                    )
                }
            )
        }
    }
}