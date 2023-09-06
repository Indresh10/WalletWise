@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.walletwise.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walletwise.R
import com.example.walletwise.data.model.AccountType
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.theme.WalletWiseTheme

object AddAccountDestination : NavigationDestination {
    override val route: String
        get() = "add_account"
    override val title: String
        get() = "Add Account"
}

// material Container Transform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddAccountViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    var error by rememberSaveable {
        mutableStateOf(false)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(256.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.updateName(it) },
            label = {
                Text(text = "Name")
            },
            singleLine = true,
            isError = error and uiState.name.isEmpty(),
            supportingText = {
                Text(text = if (error and uiState.name.isEmpty()) "Required*" else "")
            }
        )
        OutlinedTextField(
            value = uiState.amount,
            onValueChange = { viewModel.updateAmount(it) },
            label = {
                Text(text = "Amount")
            },
            singleLine = true,
            isError = error and (uiState.amount.isEmpty() or ((uiState.amount.toDoubleOrNull()
                ?: 0.0) > 0.0)),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            supportingText = {
                Text(
                    text = if (error and (uiState.amount.isEmpty() or ((uiState.amount.toDoubleOrNull()
                            ?: 0.0) > 0.0))
                    ) "Required* and value should be greater than 0" else ""
                )
            }
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !it }) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(),
                value = uiState.type,
                readOnly = true,
                onValueChange = { },
                label = { Text(text = "Type") },
                isError = error and uiState.type.isEmpty(),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(if (expanded) 180f else 0f)
                        )
                    }
                },
                supportingText = {
                    Text(text = if (error and uiState.type.isEmpty()) "Required*" else "")
                }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                AccountType.values().forEach {
                    DropdownMenuItem(
                        text = { Text(text = it.name) },
                        onClick = {
                            viewModel.updateType(it.name)
                            expanded = false
                        }
                    )
                }
            }
        }
        Button(onClick = {
            if (viewModel.validateInput()) {
                viewModel.saveAccount()
                Toast.makeText(context, "Account added Successfully", Toast.LENGTH_SHORT).show()
                navigateBack()
            } else {
                error = true
                Toast.makeText(
                    context,
                    "Oops! Please fill out all fields correctly before moving forward",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text(text = "Save")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddAccountScreenPreview() {
    WalletWiseTheme {
        AddAccountScreen({})
    }
}

