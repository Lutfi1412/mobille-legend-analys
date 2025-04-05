package com.example.mobillelegendanalys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobillelegendanalys.ui.theme.MobilleLegendAnalysTheme
import kotlinx.coroutines.launch
import org.example.mlanalis.data.GetMeta
import org.example.mlanalis.data.GetOP
import org.example.mlanalis.data.GetUnder
import org.example.mlanalis.local.DataStoreManager.clearMeta
import org.example.mlanalis.local.DataStoreManager.clearOP
import org.example.mlanalis.local.DataStoreManager.clearUnder
import org.example.mlanalis.services.fetchMeta
import org.example.mlanalis.services.fetchOP
import org.example.mlanalis.services.fetchUnder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobilleLegendAnalysTheme {
                Column (modifier = Modifier.fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val navItems = listOf("Over Power", "Meta", "Underrated")
    var selectedIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // State untuk data OP
    var opData by remember { mutableStateOf<List<GetOP>>(emptyList()) }
    var metaData by remember { mutableStateOf<List<GetMeta>>(emptyList()) }
    var underData by remember { mutableStateOf<List<GetUnder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(selectedIndex) {
        isLoading = true
        when (selectedIndex) {
            0 -> opData = fetchOP(context)
            1 -> metaData = fetchMeta(context)
            2 -> underData = fetchUnder(context)
        }
        isLoading = false
    }

    Scaffold(
        // Hapus scaffoldState karena tidak digunakan
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${navItems[selectedIndex]} By Lutfi BKZ",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6A1B9A), // ungu tua
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            isLoading = true
                            clearOP(context)
                            opData = fetchOP(context)

                            clearMeta(context)
                            metaData = fetchMeta(context)

                            clearUnder(context)
                            underData = fetchUnder(context)
                            isLoading = false
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFF6A1B9A), // ungu juga biar match
                contentColor = Color.White
            ) {
                navItems.forEachIndexed { index, title ->
                    BottomNavigationItem(
                        icon = {},
                        label = {
                            Text(
                                title,
                                color = if (selectedIndex == index) Color.White else Color.LightGray
                            )
                        },
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.LightGray
                    )
                }
            }

        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedIndex) {
                0 -> {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (opData.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Data kosong.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(opData) { hero ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = 4.dp
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = hero.name, style = MaterialTheme.typography.h6)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Ban Rate: ${hero.ban_rate}",
                                            style = MaterialTheme.typography.body1.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = MaterialTheme.typography.body1.fontSize
                                            )
                                        )
                                        Text("Win Rate: ${hero.win_rate}")
                                        Text("Pick Rate: ${hero.pick_rate}")
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (metaData.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Data kosong.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(metaData) { hero ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = 4.dp
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = hero.name, style = MaterialTheme.typography.h6)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Pick Rate: ${hero.pick_rate}",
                                            style = MaterialTheme.typography.body1.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = MaterialTheme.typography.body1.fontSize
                                            )
                                        )
                                        Text("win Rate: ${hero.win_rate}")
                                        Text("Ban Rate: ${hero.ban_rate}")
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (underData.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Data kosong.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            items(underData) { hero ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    elevation = 4.dp
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = hero.name, style = MaterialTheme.typography.h6)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Win Rate: ${hero.win_rate}",
                                            style = MaterialTheme.typography.body1.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = MaterialTheme.typography.body1.fontSize
                                            )
                                        )
                                        Text("Ban Rate: ${hero.ban_rate}")
                                        Text("Pick Rate: ${hero.pick_rate}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

