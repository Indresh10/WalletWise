@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.walletwise.ui.screens.transaction

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.rounded.FolderZip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.walletwise.R
import com.example.walletwise.data.model.AccountType
import com.example.walletwise.data.model.ImageType
import com.example.walletwise.data.model.TagType
import com.example.walletwise.data.model.TransactionData
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.NavigationDestination
import com.example.walletwise.ui.theme.WalletWiseTheme
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object HistoryDestination : NavigationDestination {
    override val route: String
        get() = "history"
    override val title: String
        get() = "History"
    const val keyArg = "keyArg"
    const val valueArg = "valueArg"
    val routeWithArgs = "$route/{$keyArg}/{$valueArg}"
}

@Composable
fun BalanceScreen(
    onTagClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BalanceScreenViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val enableDecimal by viewModel.showDecimal.collectAsStateWithLifecycle(true)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if ((viewModel.getKey() != "Time") and (viewModel.getKey() != "AccountType") and (viewModel.getKey() != "TagType"))
            Text(
                text = uiState.subtitle,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp)
                    )
            )
        else {
            when (viewModel.getKey()) {
                "AccountType" -> {
                    val accountType = AccountType.entries.toTypedArray()
                    var selected by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    NextPrevRow(
                        onPrev = {
                            if (selected != 0) {
                                selected--
                                viewModel.updateAccountType(accountType[selected])
                            }
                        },
                        prevEnable = selected != 0,
                        onNext = {
                            if (selected != accountType.size - 1) {
                                selected++
                                viewModel.updateAccountType(accountType[selected])
                            }
                        },
                        nextEnable = selected != accountType.size - 1,
                        text = accountType[selected].name,
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(
                                bottomStart = 10.dp,
                                bottomEnd = 10.dp
                            )
                        )
                    )
                }

                "TagType" -> {
                    val tagType = arrayOf(
                        TagType.Income.name,
                        TagType.Expense.name,
                        "ALL"
                    )
                    var selected by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    NextPrevRow(
                        onPrev = {
                            if (selected != 0) {
                                selected--
                                viewModel.updateTagType(TagType.valueOf(tagType[selected]))
                            }
                        },
                        prevEnable = selected != 0,
                        onNext = {
                            if (selected != tagType.size - 1) {
                                selected++
                                viewModel.updateTagType(
                                    if (selected != tagType.size - 1) TagType.valueOf(
                                        tagType[selected]
                                    ) else null
                                )
                            }
                        },
                        nextEnable = selected != tagType.size - 1,
                        text = tagType[selected],
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(
                                bottomStart = 10.dp,
                                bottomEnd = 10.dp
                            )
                        )
                    )
                }

                "Time" -> {
                    val timeLayout = TimeLayout.entries.toTypedArray()
                    var selected by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    NextPrevRow(
                        onPrev = {
                            if (selected != 0) {
                                selected--
                                viewModel.updateDateRange(
                                    timeLayout = timeLayout[selected],
                                    data = if (selected == 0) LocalDate.now().monthValue else LocalDate.now().year,
                                    start = LocalDate.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                    end = LocalDate.now().plusDays(7)
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                )
                            }
                        },
                        prevEnable = selected != 0,
                        onNext = {
                            if (selected != timeLayout.size - 1) selected++
                            viewModel.updateDateRange(
                                timeLayout = timeLayout[selected],
                                data = if (selected == 0) LocalDate.now().monthValue else LocalDate.now().year,
                                start = LocalDate.now()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                end = LocalDate.now().plusDays(7)
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            )
                        },
                        nextEnable = selected != timeLayout.size - 1,
                        text = timeLayout[selected].name,
                        modifier = Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    when (timeLayout[selected]) {
                        TimeLayout.Month -> {
                            var selectedMonth by rememberSaveable {
                                mutableIntStateOf(LocalDate.now().monthValue)
                            }
                            NextPrevRow(
                                onPrev = {
                                    if (selectedMonth != 1) {
                                        selectedMonth--
                                        viewModel.updateDateRange(
                                            timeLayout = TimeLayout.Month,
                                            data = selectedMonth
                                        )
                                    }
                                },
                                prevEnable = selectedMonth != 1,
                                onNext = {
                                    if (selectedMonth != 12) {
                                        selectedMonth++
                                        viewModel.updateDateRange(
                                            timeLayout = TimeLayout.Month,
                                            data = selectedMonth
                                        )
                                    }
                                },
                                nextEnable = selectedMonth != 12,
                                text = Month.of(selectedMonth).name,
                                modifier = Modifier.background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(
                                        bottomStart = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                )
                            )
                        }

                        TimeLayout.Year -> {
                            var selectedYear by rememberSaveable {
                                mutableIntStateOf(LocalDate.now().year)
                            }
                            val currentYear = LocalDate.now().year
                            NextPrevRow(
                                onPrev = {
                                    if (selectedYear != currentYear - 5) {
                                        selectedYear--
                                        viewModel.updateDateRange(
                                            timeLayout = TimeLayout.Year,
                                            data = selectedYear
                                        )
                                    }
                                },
                                prevEnable = selectedYear != currentYear - 5,
                                onNext = {
                                    if (selectedYear != currentYear + 5) {
                                        selectedYear++
                                        viewModel.updateDateRange(
                                            timeLayout = TimeLayout.Year,
                                            data = selectedYear
                                        )
                                    }
                                },
                                nextEnable = selectedYear != currentYear + 5,
                                text = selectedYear.toString(),
                                modifier = Modifier.background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(
                                        bottomStart = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                )
                            )
                        }

                        TimeLayout.Range -> {
                            val today = LocalDateTime.now()
                            RangeDateRow(
                                start = today.toInstant(ZoneOffset.UTC).toEpochMilli(),
                                end = today.plusDays(7).toInstant(ZoneOffset.UTC).toEpochMilli(),
                                onSubmit = { start, end ->
                                    viewModel.updateDateRange(
                                        timeLayout = TimeLayout.Range,
                                        start = start,
                                        end = end
                                    )
                                },
                                modifier = Modifier.background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(
                                        bottomStart = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        BalanceCard(
            positive = uiState.positiveAmt,
            negative = uiState.negativeAmt,
            enableDecimal = enableDecimal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(text = "Transactions", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { exportData() }) {
                Icon(imageVector = Icons.Rounded.FolderZip, contentDescription = "Export Data")
            }
        }
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        TransactionList(
            dateGroupedTransaction = uiState.datedTransactions,
            onTagClick,
            enableDecimal
        )
    }
}

fun exportData() {

}

@Preview(showBackground = true)
@Composable
fun BalanceScreenPreview() {
    WalletWiseTheme {
        BalanceScreen({})
    }
}

@Composable
fun BalanceCard(
    positive: Double,
    negative: Double,
    enableDecimal: Boolean,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = positive + negative,
                label = "MainBalance",
                transitionSpec = {
                    slideInVertically {
                        it
                    } togetherWith slideOutVertically {
                        -it
                    }
                }) {
                Text(
                    text = DecimalFormat.getCurrencyInstance(Locale("en", "in"))
                        .format(it).dropLast(if(enableDecimal) 0 else 3),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                AnimatedContent(
                    targetState = positive, label = "MainBalance", transitionSpec = {
                        slideInHorizontally {
                            -it
                        } togetherWith slideOutVertically {
                            it
                        }
                    }, modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .weight(1f, true)
                ) {
                    Text(
                        text = String.format(
                            DecimalFormat.getCurrencyInstance(Locale("en", "in"))
                                .format(it).dropLast(if(enableDecimal) 0 else 3)
                        ),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                AnimatedContent(
                    targetState = negative, label = "MainBalance", transitionSpec = {
                        slideInHorizontally {
                            it
                        } togetherWith slideOutHorizontally {
                            -it
                        }
                    }, modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .weight(1f, true)
                ) {
                    Text(
                        text = DecimalFormat.getCurrencyInstance(Locale("en", "in"))
                            .format(it).dropLast(if(enableDecimal) 0 else 3),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TransactionList(
    dateGroupedTransaction: Map<String, List<TransactionData>>,
    onTagClick: (Long) -> Unit,
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
        val comp by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.nodata))
        LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (dateGroupedTransaction.isEmpty())
                item {
                    LottieAnimation(comp, clipSpec = LottieClipSpec.Progress(0f,0.8f))
                }
            else
                itemsIndexed(dateGroupedTransaction.toList()) { index, it ->
                    TransactionListItem(
                        date = it.first,
                        transactions = it.second,
                        onTagClick = onTagClick,
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
        }
    }
}

@Composable
fun TransactionListItem(
    date: String,
    enableDecimal: Boolean,
    transactions: List<TransactionData>,
    onTagClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            transactions.forEach { data ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (data.tag.type) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val amount =
                        if (data.tag.type) data.transaction.amount else -data.transaction.amount
                    Column {
                        Text(
                            text = DecimalFormat.getCurrencyInstance(Locale("en", "in"))
                                .format(amount).dropLast(if(enableDecimal) 0 else 3),
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        Text(
                            text = data.transaction.notes?.let {
                                data.accounts.name + ", " + it
                            } ?: data.accounts.name, modifier = Modifier.padding(start = 16.dp),
                            fontSize = 14.sp, color = if (data.tag.type) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer, fontStyle = FontStyle.Italic
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    AssistChip(
                        onClick = { onTagClick(data.tag.id) },
                        label = { Text(text = data.tag.name) },
                        leadingIcon = {
                            Icon(
                                imageVector = ImageType.entries[data.tag.icon].res,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun NextPrevRow(
    onPrev: () -> Unit,
    prevEnable: Boolean,
    onNext: () -> Unit,
    nextEnable: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev, enabled = prevEnable) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "previous"
            )
        }
        Text(text = text, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        IconButton(onClick = onNext, enabled = nextEnable) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "next"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeDateRow(
    start: Long,
    end: Long,
    onSubmit: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = start,
        initialSelectedEndDateMillis = end,
        initialDisplayMode = DisplayMode.Input
    )

    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(bottomEnd = 32.dp, bottomStart = 32.dp)
            )
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var expand by rememberSaveable {
            mutableStateOf(false)
        }
        if (expand)
            DateRangePicker(state = datePickerState, showModeToggle = false, title = {
                Text(
                    text = "Select Dates",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }, headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = datePickerState.selectedStartDateMillis,
                    selectedEndDateMillis = datePickerState.selectedEndDateMillis,
                    displayMode = DisplayMode.Picker,
                    dateFormatter = DatePickerDefaults.dateFormatter(selectedDateSkeleton = "MMMdy"),
                )
            })
        else {
            val startDate = Instant.ofEpochMilli(datePickerState.selectedStartDateMillis!!)
                .atZone(ZoneId.systemDefault()).toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            val endDate = Instant.ofEpochMilli(datePickerState.selectedEndDateMillis!!)
                .atZone(ZoneId.systemDefault()).toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
            Text(
                text = "$startDate ~ $endDate",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = {
            if (expand) {
                datePickerState.displayMode = DisplayMode.Input
                val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                keyboardController?.hide()
                onSubmit(
                    format.format(
                        Instant.ofEpochMilli(datePickerState.selectedStartDateMillis!!)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                    ),
                    format.format(
                        Instant.ofEpochMilli(datePickerState.selectedEndDateMillis!!)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                    )
                )
                expand = false
            } else {
                expand = true
            }
        }) {
            Text(text = if (expand) "Submit" else "Edit")
        }
    }
}