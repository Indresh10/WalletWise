@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.walletwise.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.walletwise.R
import com.example.walletwise.ui.ViewModelProvider
import com.example.walletwise.ui.navigation.DrawerListItem
import com.example.walletwise.ui.navigation.GeneralDrawerItems
import kotlinx.coroutines.launch

@Composable
fun WalletWiseScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewmodel: WalletWiseViewModel = viewModel(factory = ViewModelProvider.factory)
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val name by viewmodel.name.collectAsStateWithLifecycle(initialValue = "")
    val navUiState = viewmodel.uiState
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(drawerContent = {
        ModalDrawerSheet {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                        .padding(4.dp)
                        .size(128.dp)
                )
                Text(
                    text = "Welcome, $name",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                item {
                    Text(
                        text = "Transactions",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                item { Divider() }
                items(DrawerListItem) { item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            navController.navigate(item.route)
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
                item { Divider() }
                items(GeneralDrawerItems) { item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            navController.navigate(item.route)
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        }
    }, drawerState = drawerState) {
        WalletWiseScaffold(navController, drawerState, navUiState, modifier)
    }


}

@Composable
private fun WalletWiseScaffold(
    navController: NavHostController,
    drawerState: DrawerState,
    navUiState: NavUiState,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scope = rememberCoroutineScope()
    Scaffold(topBar = {
        WalletWiseTopAppBar(
            title = backStackEntry?.destination?.route?.let {
                if (it.startsWith(AddTagDestination.route)) AddTagDestination.title
                else if (it == AccountDestination.route) AccountDestination.title
                else if (it == AddAccountDestination.route) AddAccountDestination.title
                else if (it.startsWith(AddTransactionDestination.route)) AddTransactionDestination.getTitle(
                    backStackEntry?.arguments?.getBoolean(AddTransactionDestination.typeArg)
                        ?: false
                )
                else if (it.startsWith(HistoryDestination.route)) HistoryDestination.title
                else if (it == WelcomeDestination.route) WelcomeDestination.title
                else if (it == SettingScreenDestination.route) SettingScreenDestination.title
                else if (it == AboutScreenDestination.route) AboutScreenDestination.title
                else ""
            } ?: "",
            backEnabled = (navController.previousBackStackEntry != null) and (backStackEntry?.destination?.route != AccountDestination.route),
            navEnabled = backStackEntry?.destination?.route == AccountDestination.route,
            navigateBack = {
                navController.navigateUp()
            },
            openDrawer = {
                scope.launch {
                    drawerState.open()
                }
            },
            actionEnabled = backStackEntry?.destination?.route?.startsWith(
                AddTransactionDestination.route
            )
                ?: false,
            onAction = {
                if (backStackEntry?.arguments?.getBoolean(AddTagDestination.typeArg) == true)
                    navController.navigate("${AddTagDestination.route}/${true}")
                else
                    navController.navigate("${AddTagDestination.route}/${false}")
            })
    }) { paddingValues ->
        WalletWiseNavHost(navController, navUiState, modifier.padding(paddingValues))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun WalletWiseNavHost(
    navController: NavHostController,
    navUiState: NavUiState,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = when (navUiState) {
            NavUiState.HomeState -> AccountDestination.route
            NavUiState.LoadingState -> "Loading"
            NavUiState.LockState -> LockScreenDestination.route
            NavUiState.WelcomeState -> WelcomeDestination.route
        },
        modifier = modifier
    ) {
        composable("Loading") {
            Box(modifier = Modifier.fillMaxSize())
        }
        composable(WelcomeDestination.route) {
            WelcomeScreen(navigateHome = {
                navController.navigate(AccountDestination.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                navController.graph.setStartDestination(AccountDestination.route)
            })
        }
        composable(LockScreenDestination.route) {
            LockScreen(navigateToHome = {
                navController.navigate(AccountDestination.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
                navController.graph.setStartDestination(AccountDestination.route)
            }, modifier = Modifier.fillMaxSize())
        }
        composable(AccountDestination.route, enterTransition = {
            fadeIn() + this.scaleInToFitContainer(contentScale = ContentScale.FillHeight)
        }, exitTransition = {
            ExitTransition.Hold + fadeOut() + this.scaleOutToFitContainer(contentScale = ContentScale.FillHeight)
        }) {
            AccountScreen(onAdd = {
                navController.navigate("${AddTransactionDestination.route}/$it/${true}")
            }, onSub = {
                navController.navigate("${AddTransactionDestination.route}/$it/${false}")
            }, navigateToAddAccount = {
                navController.navigate(AddAccountDestination.route)
            }, onAmountClick = {
                navController.navigate("${HistoryDestination.route}/Account/$it")
            })
        }
        composable(AddAccountDestination.route, enterTransition = {
            fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up)
        }, exitTransition = {
            fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down)
        }) {
            AddAccountScreen(navigateBack = {
                navController.navigateUp()
            })
        }
        composable(AddTransactionDestination.routeWithArgs, arguments = listOf(
            navArgument(AddTransactionDestination.accIdArg) {
                type = NavType.LongType
            },
            navArgument(AddTransactionDestination.typeArg) {
                type = NavType.BoolType
            }
        ), enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Down
            )
        }, exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Up
            )
        }) {
            AddTransactionScreen(
                navigateBack = { navController.navigateUp() })
        }
        composable(AddTagDestination.routeWithArgs, arguments = listOf(
            navArgument(AddTagDestination.typeArg) {
                type = NavType.BoolType
            }
        ), enterTransition = {
            fadeIn() + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up
            )
        }, exitTransition = {
            ExitTransition.Hold + fadeOut() + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Down
            )
        }) {
            AddTagScreen(navigateBack = { navController.navigateUp() })
        }
        composable(
            HistoryDestination.routeWithArgs,
            arguments = listOf(navArgument(HistoryDestination.keyArg) {
                type = NavType.StringType
            }, navArgument(HistoryDestination.valueArg) {
                type = NavType.LongType
            }), enterTransition = {
                fadeIn() + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up
                )
            }, exitTransition = {
                ExitTransition.Hold + fadeOut() + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down
                )
            }) {
            BalanceScreen({
                navController.navigate("${HistoryDestination.route}/Tag/$it")
            })
        }
        composable(SettingScreenDestination.route) {
            SettingScreen(modifier = Modifier.fillMaxSize())
        }
        composable(AboutScreenDestination.route) {
            AboutScreen(modifier = Modifier.fillMaxSize())
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WalletWisePreview() {
    WalletWiseScreen()
}

@Composable
fun WalletWiseTopAppBar(
    title: String,
    backEnabled: Boolean,
    navEnabled: Boolean,
    navigateBack: () -> Unit,
    openDrawer: () -> Unit,
    actionEnabled: Boolean,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {

    CenterAlignedTopAppBar(title = {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary
    ), navigationIcon = {
        if (backEnabled)
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        if (navEnabled)
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
    }, actions = {
        if (actionEnabled) {
            var expand by rememberSaveable {
                mutableStateOf(false)
            }
            IconButton(onClick = { expand = !expand }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
                DropdownMenuItem(
                    text = { Text(text = "Add Tag") },
                    onClick = {
                        onAction()
                        expand = false
                    })
            }
        }
    },
        modifier = modifier
    )
}