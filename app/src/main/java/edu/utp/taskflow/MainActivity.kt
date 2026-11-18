package edu.utp.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import edu.utp.taskflow.data.local.TaskDatabase
import edu.utp.taskflow.data.remote.RetrofitClient
import edu.utp.taskflow.data.repository.RemoteTaskRepository
import edu.utp.taskflow.data.repository.TaskRepository
import edu.utp.taskflow.data.repository.UserRepository

import edu.utp.taskflow.model.TaskPriority

import edu.utp.taskflow.ui.components.NewTaskForm
import edu.utp.taskflow.ui.screens.LoginScreen
import edu.utp.taskflow.ui.screens.RemoteTasksScreen
import edu.utp.taskflow.ui.screens.TaskDetailScreen
import edu.utp.taskflow.ui.screens.TaskFlowScreen

import edu.utp.taskflow.ui.theme.TaskFlowPalette
import edu.utp.taskflow.ui.theme.TaskFlowTheme

import edu.utp.taskflow.viewmodel.RemoteTaskViewModel
import edu.utp.taskflow.viewmodel.RemoteTaskViewModelFactory
import edu.utp.taskflow.viewmodel.SessionViewModel
import edu.utp.taskflow.viewmodel.SessionViewModelFactory
import edu.utp.taskflow.viewmodel.TaskViewModel
import edu.utp.taskflow.viewmodel.TaskViewModelFactory


// RUTAS DE NAVEGACIÓN
private object TaskRoutes {

    const val LIST = "tasks"

    const val REMOTE = "remote_tasks"

    const val DETAIL = "task_detail/{taskId}"

    const val REMOTE_DETAIL = "remote_task_detail/{taskId}"

    fun detail(taskId: Int): String {
        return "task_detail/$taskId"
    }

    fun remoteDetail(taskId: Int): String {
        return "remote_task_detail/$taskId"
    }
}


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CONFIGURACIÓN ROOM
        val dao = TaskDatabase
            .getDatabase(applicationContext)
            .taskDao()

        val repository = TaskRepository(dao)

        val factory = TaskViewModelFactory(repository)


        // CONFIGURACIÓN RETROFIT
        val api = RetrofitClient.api

        val remoteRepository = RemoteTaskRepository(api)

        val remoteFactory =
            RemoteTaskViewModelFactory(remoteRepository)

        // CONFIGURACIÓN DE USUARIOS
        val userRepository = UserRepository(api)

        val sessionFactory = SessionViewModelFactory(userRepository)


        setContent {
            val taskViewModel: TaskViewModel =
                viewModel(factory = factory)

            val remoteViewModel: RemoteTaskViewModel =
                viewModel(factory = remoteFactory)

            val sessionViewModel: SessionViewModel =
                viewModel(factory = sessionFactory)

            TaskFlowDemo(
                taskViewModel = taskViewModel,
                remoteViewModel = remoteViewModel,
                sessionViewModel = sessionViewModel,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFlowDemo(
    taskViewModel: TaskViewModel,
    remoteViewModel: RemoteTaskViewModel,
    sessionViewModel: SessionViewModel,
) {

    // ESTADOS DE LAS TAREAS LOCALES
    val tasks by taskViewModel.tasks
        .collectAsStateWithLifecycle()

    val selectedFilter by taskViewModel.selectedFilter
        .collectAsStateWithLifecycle()

    // ESTADOS DE LAS TAREAS REMOTAS
    val remoteTasks by remoteViewModel.tasks
        .collectAsStateWithLifecycle()

    val remoteIsLoading by remoteViewModel.isLoading
        .collectAsStateWithLifecycle()

    val remoteIsSaving by remoteViewModel.isSaving
        .collectAsStateWithLifecycle()

    val remoteErrorMessage by remoteViewModel.errorMessage
        .collectAsStateWithLifecycle()

    // ESTADOS DE SESIÓN
    val users by sessionViewModel.users
        .collectAsStateWithLifecycle()

    val currentUser by sessionViewModel.currentUser
        .collectAsStateWithLifecycle()

    val sessionIsLoading by sessionViewModel.isLoading
        .collectAsStateWithLifecycle()

    val sessionError by sessionViewModel.errorMessage
        .collectAsStateWithLifecycle()


    // PALETA DE COLORES
    var palette by remember {
        mutableStateOf(TaskFlowPalette.OCEAN)
    }


    // ESTADOS DEL FORMULARIO
    var showNewTaskSheet by remember {
        mutableStateOf(false)
    }

    var newTaskTitle by remember {
        mutableStateOf("")
    }

    var newTaskPriority by remember {
        mutableStateOf(TaskPriority.MEDIUM)
    }


    // INDICA SI SE CREA EN ROOM O EN MOCKAPI
    var creatingRemoteTask by remember {
        mutableStateOf(false)
    }


    // CONTROLADOR DE NAVEGACIÓN
    val navController = rememberNavController()

    val backStackEntry by navController
        .currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(currentUser?.id) {
        remoteViewModel.setUser(currentUser?.id)
    }

    TaskFlowTheme(palette = palette) {
        val activeUser = currentUser
        if (activeUser == null) {
            LoginScreen(
                users = users,
                isLoading = sessionIsLoading,
                errorMessage = sessionError,
                onRetry = {
                    sessionViewModel.loadUsers()
                },
                onLogin = { user ->
                    sessionViewModel.login(user)
                }
            )
        } else {
            Scaffold(
                topBar = {

                    if (
                        currentRoute == TaskRoutes.LIST ||
                        currentRoute == TaskRoutes.REMOTE
                    ) {

                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp
                        ) {

                            Row(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 20.dp,
                                        vertical = 10.dp
                                    ),

                                horizontalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = activeUser.name,
                                        style =
                                            MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Text(
                                        text = activeUser.email,
                                        style =
                                            MaterialTheme.typography.bodySmall,
                                        color =
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        sessionViewModel.logout()
                                              },
                                    enabled = !remoteIsSaving
                                ) {

                                    Icon(
                                        imageVector = Icons.Filled.Logout,
                                        contentDescription = "Cerrar sesión"
                                    )
                                }
                            }
                        }
                    }
                },

                // BARRA DE NAVEGACIÓN INFERIOR
                bottomBar = {

                    if (
                        currentRoute == TaskRoutes.LIST ||
                        currentRoute == TaskRoutes.REMOTE
                    ) {

                        NavigationBar {

                            // OPCIÓN LOCAL
                            NavigationBarItem(

                                selected =
                                    currentRoute == TaskRoutes.LIST,

                                onClick = {

                                    navController.navigate(
                                        TaskRoutes.LIST
                                    ) {

                                        popUpTo(TaskRoutes.LIST) {
                                            saveState = true
                                        }

                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },

                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Home,
                                        contentDescription = "Tareas locales"
                                    )
                                },

                                label = {
                                    Text("Local")
                                }
                            )


                            // OPCIÓN REMOTO
                            NavigationBarItem(

                                selected =
                                    currentRoute == TaskRoutes.REMOTE,

                                onClick = {

                                    navController.navigate(
                                        TaskRoutes.REMOTE
                                    ) {

                                        popUpTo(TaskRoutes.LIST) {
                                            saveState = true
                                        }

                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },

                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Cloud,
                                        contentDescription = "Tareas remotas"
                                    )
                                },

                                label = {
                                    Text("Remoto")
                                }
                            )
                        }
                    }
                }

            ) { innerPadding ->

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),

                    color = MaterialTheme.colorScheme.background
                ) {

                    NavHost(
                        navController = navController,
                        startDestination = TaskRoutes.LIST
                    ) {


                        // =====================================
                        // PANTALLA DE TAREAS LOCALES
                        // =====================================

                        composable(TaskRoutes.LIST) {

                            TaskFlowScreen(
                                tasks = tasks,

                                selectedFilter = selectedFilter,

                                onFilterChange =
                                    taskViewModel::changeFilter,

                                onToggleTask =
                                    taskViewModel::toggleTask,

                                onDeleteTask =
                                    taskViewModel::deleteTask,

                                onTaskClick = { taskId ->

                                    navController.navigate(
                                        TaskRoutes.detail(taskId)
                                    )
                                },

                                onNewTaskClick = {

                                    // Crear tarea en Room
                                    creatingRemoteTask = false

                                    newTaskTitle = ""

                                    newTaskPriority =
                                        TaskPriority.MEDIUM

                                    showNewTaskSheet = true
                                },

                                palette = palette,

                                onPaletteChange = {
                                    palette = it
                                }
                            )
                        }


                        // =====================================
                        // PANTALLA DE TAREAS REMOTAS
                        // =====================================

                        composable(TaskRoutes.REMOTE) {

                            RemoteTasksScreen(
                                tasks = remoteTasks,

                                isLoading = remoteIsLoading,

                                isSaving = remoteIsSaving,

                                errorMessage = remoteErrorMessage,

                                onRetry = {
                                    remoteViewModel.loadTasks()
                                },

                                onToggleTask = { task ->

                                    // PUT: completar o dejar pendiente
                                    remoteViewModel.toggleTask(task)
                                },

                                onDeleteTask = { taskId ->

                                    // DELETE: eliminar de MockAPI
                                    remoteViewModel.deleteTask(taskId)
                                },

                                onTaskClick = { taskId ->

                                    // Abrir detalle remoto
                                    navController.navigate(
                                        TaskRoutes.remoteDetail(taskId)
                                    )
                                },

                                onNewTaskClick = {

                                    // Crear tarea en MockAPI
                                    creatingRemoteTask = true

                                    newTaskTitle = ""

                                    newTaskPriority =
                                        TaskPriority.MEDIUM

                                    showNewTaskSheet = true
                                }
                            )
                        }


                        // =====================================
                        // DETALLE DE UNA TAREA LOCAL
                        // =====================================

                        composable(
                            route = TaskRoutes.DETAIL,

                            arguments = listOf(
                                navArgument("taskId") {
                                    type = NavType.IntType
                                }
                            )

                        ) { entry ->

                            val taskId =
                                entry.arguments?.getInt("taskId")

                            val selectedTask = tasks.firstOrNull {
                                it.id == taskId
                            }

                            TaskDetailScreen(
                                task = selectedTask,

                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }


                        // =====================================
                        // DETALLE DE UNA TAREA REMOTA
                        // =====================================

                        composable(
                            route = TaskRoutes.REMOTE_DETAIL,

                            arguments = listOf(
                                navArgument("taskId") {
                                    type = NavType.IntType
                                }
                            )

                        ) { entry ->

                            val taskId =
                                entry.arguments?.getInt("taskId")

                            val selectedTask =
                                remoteTasks.firstOrNull {
                                    it.id == taskId
                                }

                            TaskDetailScreen(
                                task = selectedTask,

                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }

        // =====================================
        // FORMULARIO COMPARTIDO DE CREACIÓN
        // =====================================

        if (showNewTaskSheet) {

            ModalBottomSheet(

                onDismissRequest = {
                    showNewTaskSheet = false
                }

            ) {

                NewTaskForm(
                    title = newTaskTitle,

                    onTitleChange = {
                        newTaskTitle = it
                    },

                    selectedPriority = newTaskPriority,

                    onPriorityChange = {
                        newTaskPriority = it
                    },

                    onSave = {

                        if (newTaskTitle.isNotBlank()) {

                            if (creatingRemoteTask) {

                                // POST: CREAR TAREA EN MOCKAPI
                                if (!remoteIsLoading && !remoteIsSaving) {

                                    remoteViewModel.createTask(
                                        title = newTaskTitle,
                                        priority = newTaskPriority
                                    )

                                    showNewTaskSheet = false
                                }

                            } else {

                                // CREAR TAREA EN ROOM
                                taskViewModel.addTask(
                                    title = newTaskTitle,
                                    priority = newTaskPriority
                                )

                                showNewTaskSheet = false
                            }
                        }
                    },

                    onCancel = {
                        showNewTaskSheet = false
                    }
                )
            }
        }
    }
}
