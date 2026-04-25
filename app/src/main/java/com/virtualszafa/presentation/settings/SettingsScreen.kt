package com.virtualszafa.presentation.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.virtualszafa.domain.model.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    var localProfile by remember { mutableStateOf(profile) }

    // Auto-sync lokalnego stanu z ViewModel
    LaunchedEffect(profile) {
        localProfile = profile
    }

    // BackHandler z potwierdzeniem
    BackHandler {
        if (localProfile != profile) {
            // W prawdziwej aplikacji można dodać AlertDialog „Zapisać zmiany?”
            viewModel.saveProfile(localProfile)
        }
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia / Profil") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (localProfile != profile) viewModel.saveProfile(localProfile)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Powrót")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Wiek: ${localProfile.age} lat (pobrano z Google/Apple)",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = localProfile.clothingSize,
                onValueChange = { localProfile = localProfile.copy(clothingSize = it) },
                label = { Text("Rozmiar ubrań") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = localProfile.shoeSize,
                onValueChange = { localProfile = localProfile.copy(shoeSize = it) },
                label = { Text("Rozmiar buta") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = localProfile.hairColor,
                onValueChange = { localProfile = localProfile.copy(hairColor = it) },
                label = { Text("Kolor włosów") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = localProfile.eyeColor,
                onValueChange = { localProfile = localProfile.copy(eyeColor = it) },
                label = { Text("Kolor oczu") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.saveProfile(localProfile)
                    // TODO: Toast „Zmiany zapisane” w pełnej wersji
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Zapisz zmiany")
            }
        }
    }
}