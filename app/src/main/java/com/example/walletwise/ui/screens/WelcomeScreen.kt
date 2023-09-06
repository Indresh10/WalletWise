package com.example.walletwise.ui.screens

import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.theme.WalletWiseTheme

const val CONTENT_ANIMATION_DURATION = 300

object WelcomeDestination : NavigationDestination {
    override val route: String
        get() = "welcome"
    override val title: String
        get() = "Welcome"
}

@Composable
fun WelcomeScreen(
    navigateHome: () -> Unit,
    modifier: Modifier = Modifier,
    vm: WelcomeScreenViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var index by rememberSaveable {
        mutableIntStateOf(0)
    }
    var error by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = uiState.completed){
        if(uiState.completed){
            navigateHome()
        }
    }
    AnimatedContent(targetState = index, label = "Index", transitionSpec = {
        fadeIn() + slideInHorizontally(
            animationSpec = tween(CONTENT_ANIMATION_DURATION),
            initialOffsetX = { fullWidth -> fullWidth }
        ) togetherWith
                fadeOut() + slideOutHorizontally(
            animationSpec = tween(CONTENT_ANIMATION_DURATION),
            targetOffsetX = { fullWidth -> -fullWidth }
        )
    }) {index1 ->
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImageAndDescription(
                description = uiState.desc, animation = uiState.anim, modifier = Modifier.weight(1f)
            )
            AnimatedVisibility(visible = index1 != 1) {
                Text(
                    text = if (index1 == 0) "Let's start by getting to know you." else "Set up a pass lock to keep your data secure.",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                TextField(
                    value = uiState.input,
                    onValueChange = {
                        if(index1 != 2 || it.length <= 4)
                            vm.updateInput(it)
                    },
                    label = {
                        Text(text = if (index1 == 0) "Enter your name" else "Pin")
                    },
                    isError = error && uiState.input.isEmpty(),
                    supportingText = {
                        if (error && uiState.input.isEmpty())
                            Text(text = "Required", color = MaterialTheme.colorScheme.onErrorContainer)
                        if (error && index1 == 2 && uiState.input.length != 4)
                            Text(text = "Required pin of length 4", color = MaterialTheme.colorScheme.onErrorContainer)

                    },
                    textStyle = if (index1 == 2) TextStyle.Default.copy(letterSpacing = 14.sp) else TextStyle.Default,
                    keyboardOptions = KeyboardOptions(keyboardType = if (index1 == 2) KeyboardType.NumberPassword else KeyboardType.Text),
                    visualTransformation = if (index1 == 2) PasswordVisualTransformation('#') else VisualTransformation.None,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            Button(
                onClick = {
                    vm.submit(index1 == 2, index1 + 1)
                    if (index1 < 2) {
                        if (index1 == 0 && uiState.input.isEmpty())
                            error = true
                        else
                            index++
                    }
                    else {
                        if(index1 == 2 && uiState.input.length != 4)
                            error = true
                    }

                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Text(text = if (index1 >= 2) "Setup & Finish" else "Next")
            }
        }


    }

}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WalletWiseTheme {
        WelcomeScreen({})
    }
}

@Composable
fun ImageAndDescription(
    description: String,
    @RawRes animation: Int,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(animation))
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        LottieAnimation(
            composition = composition, modifier = Modifier
                .padding(16.dp)
                .weight(1f),
            iterations = LottieConstants.IterateForever
        )
        Text(
            text = description,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp)
        )
    }
}

