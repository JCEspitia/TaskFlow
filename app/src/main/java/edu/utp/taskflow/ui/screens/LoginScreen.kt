
package edu.utp.taskflow.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TaskAlt

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import edu.utp.taskflow.model.RemoteUser

@Composable
fun LoginScreen(
    users: List<RemoteUser>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onLogin: (RemoteUser) -> Unit
) {

    var selectedUser by remember(users) {
        mutableStateOf<RemoteUser?>(null)
    }

    val primary = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 32.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // LOGOTIPO
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = primary.copy(alpha = 0.12f)
            ) {

                Box(
                    modifier = Modifier.size(88.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Filled.TaskAlt,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // TÍTULO
            Text(
                text = "TaskFlow",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = primary
            )

            Text(
                text = "Organiza tu día, alcanza tus metas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¡Bienvenido!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Selecciona tu perfil para continuar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            // CARGA
            if (isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.padding(24.dp)
                )

                Text("Consultando usuarios...")

            } else {

                // ERROR
                if (errorMessage != null) {

                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )

                    OutlinedButton(onClick = onRetry) {
                        Text("Reintentar")
                    }
                }

                // LISTA DE USUARIOS
                if (users.isEmpty() && errorMessage == null) {

                    Text(
                        text = "No hay usuarios disponibles.",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                } else {

                    users.forEach { user ->

                        UserSelectionCard(
                            user = user,
                            selected = selectedUser?.id == user.id,
                            onClick = {
                                selectedUser = user
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTÓN CONTINUAR
            Button(
                onClick = {
                    selectedUser?.let(onLogin)
                },

                enabled =
                    selectedUser != null && !isLoading,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),

                shape = RoundedCornerShape(16.dp)
            ) {

                Text(
                    text = "Continuar",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Acceso de demostración para fines educativos",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun UserSelectionCard(
    user: RemoteUser,
    selected: Boolean,
    onClick: () -> Unit
) {

    val primary = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

        shape = RoundedCornerShape(18.dp),

        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) {
                primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        ),

        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                primary.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 3.dp else 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            // AVATAR
            Surface(
                shape = CircleShape,
                color = primary.copy(alpha = 0.12f)
            ) {

                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // INFORMACIÓN DEL USUARIO
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(4.dp)
            ) {

                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // INDICADOR DE SELECCIÓN
            if (selected) {

                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = primary
                )
            }
        }
    }
}
