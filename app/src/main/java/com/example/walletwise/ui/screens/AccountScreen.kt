package com.example.walletwise.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    navigateToAddAccount: () -> Unit,
    viewModel: AccountScreenViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val balances by viewModel.balances.collectAsStateWithLifecycle()
    val enableDecimal by viewModel.showDecimal.collectAsStateWithLifecycle(initialValue = true)
    AccountSummaryScreen(
        navigateToAddAccount,
        balances,
        accounts,
        onAdd,
        onSub,
        enableDecimal,
        onAmountClick
    )

}

@Composable
private fun AccountSummaryScreen(
    navigateToAddAccount: () -> Unit,
    balances: List<Balance>,
    accounts: List<Accounts>,
    onAdd: (Long) -> Unit,
    onSub: (Long) -> Unit,
    enableDecimal: Boolean,
    onAmountClick: (Long) -> Unit
) {
    var selected by rememberSaveable {
        mutableIntStateOf(0)
    }
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = navigateToAddAccount) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Account")
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
                onAmountClick = onAmountClick,
                enableDecimal = enableDecimal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    WalletWiseTheme {
        AccountSummaryScreen({}, listOf(Balance("Cash", 5000.0)),
            listOf(Accounts(0, "Cash", AccountType.Cash.name, 5000.0)), {}, {}, false, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                        .format(it).dropLast(if(enableDecimal) 0 else 3),
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


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AccountsCardList(
    accounts: List<Accounts>,
    onAdd: (Long) -> Unit,
    onSub: (Long) -> Unit,
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
        enter = fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)),
        exit = fadeOut(),
        modifier = modifier
    ) {
        LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (accounts.isEmpty())
                item {
                    TipRow(message = "Use '+' to Add Accounts")
                }
            else
                itemsIndexed(accounts) { index, account ->
                    AccountCard(
                        account = account,
                        onAdd = { onAdd(account.id) },
                        onSub = { onSub(account.id) },
                        onAmountClick = { onAmountClick(account.id) },
                        enableDecimal = enableDecimal,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .animateEnterExit(
                                enter = slideInVertically(animationSpec = spring(
                                    stiffness = Spring.StiffnessVeryLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                ), initialOffsetY = { it * (index + 1) })
                            )
                    )
                }
            item {
                Spacer(modifier = Modifier)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountCard(
    account: Accounts,
    onAdd: () -> Unit,
    onSub: () -> Unit,
    onAmountClick: () -> Unit,
    enableDecimal: Boolean,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, onClick = onAmountClick) {
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
                        .format(account.curAmount).dropLast(if(enableDecimal) 0 else 3),
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