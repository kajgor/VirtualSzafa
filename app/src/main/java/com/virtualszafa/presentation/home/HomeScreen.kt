package com.virtualszafa.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scanState by viewModel.scanState.collectAsState()
    val isScanning = scanState is ScanState.Scanning

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.analyzeImageFromGallery(context, it) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) viewModel.toggleCameraScan() else Toast.makeText(context, "Uprawnienie do kamery jest wymagane", Toast.LENGTH_LONG).show()
    }

    Scaffold(
        topBar = {
            // Zawsze widoczny nagłówek i ikona Ustawień od pierwszego uruchomienia
            CenterAlignedTopAppBar(
                title = { Text("VirtualSzafa") },
                navigationIcon = {
                    if (isScanning) {
                        IconButton(onClick = { viewModel.toggleCameraScan() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheet = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ustawienia")
                    }
                }
            )
        }
    ) { padding ->
        if (isScanning) {
            // === TRYB SKANOWANIA – STABILNY PODGLĄD KAMERY ===
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AndroidView(
                        factory = { PreviewView(it).apply { previewView = this; scaleType = PreviewView.ScaleType.FILL_CENTER } },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Z pliku (galeria)")
                }
            }
        } else {
            // === EKRAN GŁÓWNY – nagłówek i ikona zawsze widoczne od startu ===
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.toggleCameraScan()
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.size(280.dp, 120.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Skanuj", style = MaterialTheme.typography.headlineMedium)
                    }
                }

                Spacer(Modifier.height(48.dp))

                Button(onClick = { navController.navigate("my_wardrobe") }, modifier = Modifier.fillMaxWidth(0.85f)) {
                    Text("Moja szafa", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Ustawienia", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { showBottomSheet = false; navController.navigate("settings") }, Modifier.fillMaxWidth()) {
                    Text("Profil")
                }
            }
        }
    }

    // Uruchomienie kamery – stabilne
    LaunchedEffect(isScanning, previewView) {
        if (isScanning && previewView != null) {
            viewModel.startCameraScan(context, lifecycleOwner, previewView!!)
        }
    }

    // Jednorazowa obsługa nawigacji przez Channel
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is HomeViewModel.NavigationEvent.NavigateToAddItem -> {
                    val route = if (event.barcode != null) {
                        "add_item?barcode=${event.barcode}"
                    } else {
                        "add_item?aiId=${event.aiId}"
                    }
                    
                    // Sprawdzamy czy już nie jesteśmy na tym ekranie (dodatkowe zabezpieczenie)
                    if (navController.currentDestination?.route?.startsWith("add_item") != true) {
                        navController.navigate(route)
                    }
                }
            }
        }
    }
}