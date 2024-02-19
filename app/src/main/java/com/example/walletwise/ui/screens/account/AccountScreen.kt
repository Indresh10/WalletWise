package com.example.walletwise.ui.screens.account

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.walletwise.data.model.AccountType
import com.example.walletwise.data.model.Accounts
import com.example.walletwise.data.model.Balance
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.navigation.TipRow
import com.example.walletwise.ui.theme.WalletWiseTheme
import java.text.DecimalFormat
import java.util.Locale

object AccountDestination : NavigationDestination {
    override val route: String
        get() = "accounts"
    override val title: String
        get() = "Accounts"
}

@Composable
fun AccountScreen(
    onAdd: (Long) -> Unit,
    onSub: (Long) -> Unit,
    onAmountClick: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    navigateToAddAccount: () -> Unit,
    viewModel: AccountScreenViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val balances by viewModel.balances.collectAsStateWithLifecycle()
    val enableDecimal by viewModel.showDecimal.collectAsStateWithLifecycle(initialValue = true)
    var setDeleteDialog by remember {
        mutableLongStateOf(-1)
    }
    if (setDeleteDialog != -1L) {
        AlertDialog(
            onDismissRequest = { setDeleteDialog = -1L },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAccount(setDeleteDialog)
                    setDeleteDialog = -1L
                }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { setDeleteDialog = -1L }) {
                    Text(text = "No")
                }
            },
            text = { Text("Delete Account") })
    }
    AccountSummaryScreen(
        navigateToAddAccount,
        balances,
        accounts,
        onAdd,
        onSub,
        enableDecimal,
        onAmountClick,
        onEdit
    ) {
        setDeleteDialog = it
    }

}

@Composable
private fun AccountSummaryScreen(
    navigateToAddAccount: () -> Unit,
    balances: List<Balance>,
    accounts: List<Accounts>,
    onAdd: (Long) -> Unit,
    onSub: (Long) -> Unit,
    enableDecimal: Boolean,
    onAmountClick: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    var selected by rememberSaveable {
        mutableIntStateOf(0)
    }
    var isFabVisible by remember {
        mutableStateOf(true)
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Hide FAB
                if (available.y < -1) {
                    isFabVisible = false
                }

                // Show FAB
                if (available.y > 1) {
                    isFabVisible = true
                }

                return Offset.Zero
            }
        }
    }
    Scaffold(floatingActionButton = {
        AnimatedVisibility(visible = isFabVisible, enter = scaleIn(), exit = scaleOut()) {
            FloatingActionButton(onClick = navigateToAddAccount) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Account")
            }
        }
    }) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BalanceCard(
                balances = getBalances(balances),
                selected = selected,
                balance = getBalances(balances)[selected].total,
                onSelect = { index ->
                    selected = index
                },
                enableDecimal = enableDecimal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Text(
                text = "Accounts", modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), style = MaterialTheme.typography.titleLarge
            )
            AccountsCardList(
                accounts = accounts,
                onAdd = onAdd,
                onSub = onSub,
                onEdit = onEdit,
                onDelete = onDelete,
                onAmountClick = onAmountClick,
                enableDecimal = enableDecimal,
                modifier = Modifier.nestedScroll(nestedScrollConnection)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    WalletWiseTheme {
        AccountSummaryScreen(
            {}, listOf(Balance("Cash", 5000.0)),
            listOf(Accounts(0, "Cash", AccountType.Cash.name, 5000.0)), {}, {}, false, {}, {}, {}
        )
    }
}

@Composable
fun BalanceCard(
    balances: List<Balance>,
    selected: Int,
    balance: Double,
    onSelect: (Int) -> Unit,
    enableDecimal: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Total Balance", style = MaterialTheme.typography.titleLarge)
            AnimatedContent(targetState = balance, label = "Balances", transitionSpec = {
                slideInVertically {
                    it
                } togetherWith slideOutVertically {
                    -it
                }
            }) {
                Text(
                    text = DecimalFormat.getCurrencyInstance(Locale("en", "in"))
                        .format(it).dropLast(if (enableDecimal) 0 else 3),
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                itemsIndexed(balances) { index, value ->
                    ElevatedFilterChip(
                        selected = selected == index,
                        onClick = { onSelect(index) },
                        label = {
                            Text(text = value.type, style = MaterialTheme.typography.labelLarge)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun AccountsCardList(
    accounts: List<Accounts>,
    onAdd: (Long) -> Unit,
    onSub: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onAmountClick: (Long) -> Unit,
    enableDecimal: Boolean,
    modifier: Modifier = Modifier
) {
    val visible = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = visible,
        enter = slideInVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)),
        exit = slideOutVertically(),
        modifier = Modifier
    ) {
        LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (accounts.isEmpty())
                item {
                    TipRow(message = "Use '+' to Add Accounts")
                }
            else
                itemsIndexed(accounts) { _, account ->
                    AccountCard(
                        account = account,
                        onAdd = { onAdd(account.id) },
                        onSub = { onSub(account.id) },
                        onEdit = { onEdit(account.id) },
                        onDelete = { onDelete(account.id) },
                        onAmountClick = { onAmountClick(account.id) },
                        enableDecimal = enableDecimal,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                }
            item {
                Spacer(modifier = Modifier)
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountCard(
    account: Accounts,
    onAdd: () -> Unit,
    onSub: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAmountClick: () -> Unit,
    enableDecimal: Boolean,
    modifier: Modifier = Modifier
) {
    var isMenuVisible by remember {
        mutableStateOf(false)
    }
    Card(modifier = modifier.combinedClickable(onClick = onAmountClick, onLongClick = {
        isMenuVisible = true
    })) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = account.name, style = MaterialTheme.typography.titleLarge)
                Icon(
                    imageVector = when (account.type) {
                        AccountType.Cash.name -> Icons.Rounded.Money
                        AccountType.Bank.name -> Icons.Rounded.AccountBalance
                        AccountType.Wallet.name -> Icons.Rounded.Wallet
                        else -> Icons.Rounded.Money
                    }, contentDescription = account.type,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = onAdd) {
                    Icon(
                        imageVector = Icons.Rounded.AddCircle,
                        contentDescription = "Credit to ${account.name}",
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = DecimalFormat.getCurrencyInstance(Locale("en", "in"))
                        .format(account.curAmount).dropLast(if (enableDecimal) 0 else 3),
                    style = MaterialTheme.typography.headlineLarge, overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = onSub) {
                    Icon(
                        imageVector = Icons.Filled.RemoveCircle,
                        contentDescription = "Debit from ${account.name}",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            DropdownMenu(
                expanded = isMenuVisible,
                onDismissRequest = { isMenuVisible = false },
                modifier = Modifier.align(
                    Alignment.End
                )
            ) {
                DropdownMenuItem(text = { Text(text = "Edit") }, onClick = onEdit, leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "edit the account details"
                    )
                })
                DropdownMenuItem(
                    text = { Text(text = "Delete") },
                    onClick = onDelete,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "delete the account details"
                        )
                    })
            }
        }

    }

}

fun getBalances(balances: List<Balance>): List<Balance> {
    val list = mutableListOf<Balance>()
    var total = 0.0
    balances.forEach {
        total += it.total
        list.add(it)
    }
    list.add(0, Balance("All", total))
    return list.toList()
}