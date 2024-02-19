package com.example.walletwise.ui.screens.start


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walletwise.MainActivity
import com.example.walletwise.R
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.theme.WalletWiseTheme

private const val TAG = "Lock Screen"

object LockScreenDestination : NavigationDestination {
    override val route: String
        get() = "lock"
    override val title: String
        get() = ""
}

@Composable
fun LockScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    vm: LockScreenViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val validPin by remember {
        derivedStateOf {
            uiState.pin == uiState.validPin
        }
    }
    LaunchedEffect(key1 = validPin){
        if(validPin){
            Toast.makeText(context, "Unlocked🔓", Toast.LENGTH_LONG).show()
            navigateToHome()
            vm.updatePin("")
        }
    }
    val backColor by animateColorAsState(
        targetValue = if (uiState.pin.length == 4 && !validPin) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer,
        label = "backColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (uiState.pin.length == 4 && !validPin) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer,
        label = "textColor"
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Box(
            modifier = Modifier.background(
                backColor,
                shape = RoundedCornerShape(4.dp)
            )
        ) {
            BasicTextField(
                value = uiState.pin,
                readOnly = true,
                onValueChange = {},
                modifier = Modifier
                    .padding(16.dp)
                    .width(110.dp)
                    .align(Alignment.Center),
                decorationBox = { innerTextField ->
                    Column {
                        innerTextField()
                        AnimatedVisibility(visible = uiState.pin.length == 4 && !validPin) {
                            Text(
                                text = "Invalid pin",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                textStyle = MaterialTheme.typography.headlineLarge.copy(letterSpacing = 16.sp, color = textColor),
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        NumberPadGrid(onNumberClick = {
            if (it != "-1") {
                if (uiState.pin.length != 4) vm.updatePin(uiState.pin.plus(it))
            } else vm.updatePin(uiState.pin.dropLast(1))
        })
        Spacer(modifier = Modifier.height(64.dp))
        FingerprintBtn(
            context = context,
            onAuthSuccess = {
                vm.updatePin(uiState.validPin)
            })
    }
}


@Preview(showBackground = true)
@Composable
fun LockScreenPreview() {
    WalletWiseTheme {
        LockScreen({})
    }
}

@Composable
fun NumberPadGrid(onNumberClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        BtnRow(list = (9 downTo 7).toList(), onNumberClick = onNumberClick)
        BtnRow(list = (6 downTo 4).toList(), onNumberClick = onNumberClick)
        BtnRow(list = (3 downTo 1).toList(), onNumberClick = onNumberClick)
        BtnRow(list = listOf(0, -1), onNumberClick = onNumberClick)
    }
}

@Composable
fun BtnRow(list: List<Int>, onNumberClick: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        for (x in list) {
            if (x != -1)
                Button(onClick = { onNumberClick(x.toString()) }, modifier = modifier.size(50.dp)) {
                    Text(text = x.toString(), fontSize = 16.sp)
                }
            else
                FilledIconButton(
                    onClick = { onNumberClick(x.toString()) },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Backspace,
                        contentDescription = "backspace",
                        modifier = Modifier.offset(x = 2.dp)
                    )
                }
        }
    }
}

@Composable
fun FingerprintBtn(
    context: Context,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var notRecognise by rememberSaveable {
        mutableStateOf(false)
    }
    val biometricAvailable = checkBiometric(context)
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedIconButton(onClick = {
            showBiometricPrompt(context, onAuthSuccess, onAuthFailed = { notRecognise = true })
        }, modifier = Modifier.size(70.dp), enabled = biometricAvailable && !notRecognise) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Fingerprint",
                modifier = Modifier
                    .size(70.dp)
                    .padding(16.dp)
            )
        }
        Text(
            text = if (biometricAvailable && !notRecognise) "Use your fingerprint to unlock"
            else if (notRecognise) "Whoops! Your finger's in incognito mode.🖐️😵‍💫"
            else "Whoops! Your device hasn't evolved the fingerprint feature yet. Maybe in its next life? 🤔🖐️️",
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }

}

private fun showBiometricPrompt(
    context: Context,
    onAuthSuccess: () -> Unit,
    onAuthFailed: () -> Unit
) {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric login for my app")
        .setSubtitle("Log in using your biometric credential")
        .setNegativeButtonText("Cancel")
        .build()
    val biometricPrompt = BiometricPrompt(context as MainActivity, object :
        BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            Log.e(TAG, errString.toString())
            onAuthFailed()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Log.d(TAG, "Auth Failed")
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onAuthSuccess()
        }
    })
    biometricPrompt.authenticate(promptInfo)
}

private fun checkBiometric(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return when (biometricManager.canAuthenticate(DEVICE_CREDENTIAL)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            Log.d(TAG, "App can authenticate using biometrics.")
            true
        }

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            Log.e(TAG, "No biometric features available on this device.")
            false
        }

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            Log.e(TAG, "Biometric features are currently unavailable.")
            false
        }

        else -> {
            Log.e(TAG, "Biometric features have Errors")
            false
        }
    }
}