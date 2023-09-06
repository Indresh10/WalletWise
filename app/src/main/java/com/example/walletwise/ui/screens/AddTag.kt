@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.walletwise.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walletwise.data.model.ImageType
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.theme.WalletWiseTheme

object AddTagDestination : NavigationDestination {
    override val route: String
        get() = "add_tag"
    override val title: String
        get() = "Add Tag"
    const val typeArg = "type"
    val routeWithArgs = "$route/{$typeArg}"
}

@Composable
fun AddTagScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTagViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            imageVector = ImageType.values()[uiState.icon].res,
            contentDescription = null,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape)
                .padding(24.dp)
                .size(64.dp)
        )
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.updateName(it) },
            label = {
                Text(text = "Name")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Text(text = "Select Icon")
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = modifier
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                itemsIndexed(ImageType.values()) { index,it ->
                    IconButton(
                        onClick = { viewModel.updateIcon(index) }
                    ) {
                        Icon(
                            imageVector = it.res,
                            contentDescription = it.name,
                            modifier = if (index == uiState.icon) Modifier
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .border(
                                    0.5.dp,
                                    MaterialTheme.colorScheme.onSecondaryContainer,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(4.dp) else Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
        Button(onClick = {
            if (viewModel.validateTag()) {
                viewModel.saveTag()
                navigateBack()
            } else {
                Toast.makeText(
                    context,
                    "Name field is playing hide and seek. Mind helping it come out of hiding? 🙈",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text(text = "Save")
        }
        Text(text = "Current Tags")
        TagChipsList(
            tags = tags, onTagClick = {}, modifier = Modifier.padding(horizontal = 16.dp)
        )

    }
}

@Preview(showBackground = true)
@Composable
fun AddTagScreenPreview() {
    WalletWiseTheme {
        AddTagScreen({})
    }
}

