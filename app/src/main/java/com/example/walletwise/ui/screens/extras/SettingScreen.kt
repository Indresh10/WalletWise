package com.example.walletwise.ui.screens.extras

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.theme.WalletWiseTheme

object SettingScreenDestination:NavigationDestination{
    override val route: String
        get() = "settings"
    override val title: String
        get() = "Settings"
}

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    vm: SettingViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Enable Decimal")
            Switch(checked = uiState.decimal, onCheckedChange = {
                vm.updateDecimal(it)
            })
        }
        ChangeDataLayout(
            title = "Name",
            data = uiState.name,
            onDataChange = { vm.updateName(it) },
            saveData = { vm.saveName() },
            passwordType = false
        )
        ChangeDataLayout(
            title = "Pin",
            data = uiState.pin,
            onDataChange = { vm.updatePin(it) },
            saveData = { vm.savePin() },
            passwordType = true
        )
    }
}

@Composable
fun ChangeDataLayout(
    title: String,
    data: String,
    onDataChange: (String) -> Unit,
    saveData: () -> Unit,
    passwordType: Boolean,
    modifier: Modifier = Modifier
) {
    var enable by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(text = title)
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = data,
                onValueChange = {
                    if (passwordType) {
                        if (it.length <= 4) onDataChange(it)
                    } else {
                        onDataChange(it)
                    }
                },
                enabled = enable,
                isError = if(!passwordType) data.isEmpty() else data.length != 4,
                supportingText = {
                    if (!passwordType && data.isEmpty() && enable)
                        Text(text = "Required")
                    else if (passwordType && data.length != 4 && enable)
                        Text(text = "Required pin to be of 4 digits")
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = if (passwordType) KeyboardType.NumberPassword else KeyboardType.Text),
                visualTransformation = if (passwordType) PasswordVisualTransformation('#') else VisualTransformation.None
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if (enable) {
                    if (!passwordType && data.isNotEmpty()) {
                        saveData()
                        enable = false
                    }else if(data.length == 4){
                        saveData()
                        enable = false
                    }
                } else {
                    enable = true
                }
            }, modifier = Modifier.padding(top = 4.dp)) {
                Text(text = if (enable) "Save" else "Edit")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    WalletWiseTheme {
        SettingScreen(modifier = Modifier.fillMaxSize())
    }
}