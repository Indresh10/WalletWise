package com.example.walletwise.ui.screens.extras

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletwise.R
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.theme.WalletWiseTheme
import com.example.walletwise.ui.theme.badScript

object AboutScreenDestination : NavigationDestination {
    override val route: String
        get() = "about"
    override val title: String
        get() = "About Me"
}

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "Indresh Hemani\nAndroid Developer",
                    textAlign = TextAlign.Center,
                    fontFamily = badScript,
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    FilledIconButton(onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("mailto:hemaniindresh@gmail.com")
                            )
                        )
                    }) {
                        Icon(imageVector = Icons.Filled.Mail, contentDescription = "Mail me")
                    }
                    FilledIconButton(onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/Indresh10")
                            )
                        )
                    }) {
                        Icon(
                            painterResource(id = R.drawable.github),
                            contentDescription = "Contact me through github",
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    FilledIconButton(onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.linkedin.com/in/indresh-hemani-10058418a/")
                            )
                        )
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.linkedin),
                            contentDescription = "Reach me at Linkedin",
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            Surface(
                shape = RoundedCornerShape(bottomStart = 24.dp),
                shadowElevation = 16.dp,
                modifier = Modifier.weight(.75f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.my),
                    contentDescription = null, contentScale = ContentScale.Crop
                )
            }

        }
        Image(
            painter = painterResource(id = R.drawable.compose),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )
        Text(
            text = "Made With JetPack Compose",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    WalletWiseTheme {
        AboutScreen(Modifier.fillMaxSize())
    }
}