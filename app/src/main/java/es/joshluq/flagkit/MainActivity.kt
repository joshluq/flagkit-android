package es.joshluq.flagkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.joshluq.flagkit.data.cache.InMemoryFlagCache
import es.joshluq.flagkit.data.provider.MapBasedFlagProvider
import es.joshluq.flagkit.ui.theme.FlagkitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val flagKitManager = (application as FlagKitExampleApp).flagKitManager

        setContent {
            FlagkitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlagStatusScreen(
                        flagKitManager = flagKitManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FlagStatusScreen(
    flagKitManager: FlagKitManager,
    modifier: Modifier = Modifier
) {
    // Observamos los flags reactivamente
    val showGreeting by flagKitManager.observeFeature("show_greeting", false).collectAsState(initial = false)
    val newUiEnabled by flagKitManager.observeFeature("new_ui_enabled", false).collectAsState(initial = false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FlagKit Demo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        FlagCard(
            flagName = "show_greeting",
            isEnabled = showGreeting
        )

        FlagCard(
            flagName = "new_ui_enabled",
            isEnabled = newUiEnabled
        )

        if (showGreeting) {
            Text(
                text = "👋 Hello from FlagKit!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 32.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FlagCard(
    flagName: String,
    isEnabled: Boolean
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Flag: $flagName",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Status: ${if (isEnabled) "ENABLED" else "DISABLED"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlagStatusScreenPreview() {
    // Configuración Mock para el Preview
    val cache = InMemoryFlagCache()
    // Pre-cargamos valores para que se vean en el preview
    cache.put("show_greeting", true)
    cache.put("new_ui_enabled", false)

    val provider = MapBasedFlagProvider(cache)
    val manager = FlagKitBuilder().withProvider(provider).build()

    FlagkitTheme {
        FlagStatusScreen(flagKitManager = manager)
    }
}
