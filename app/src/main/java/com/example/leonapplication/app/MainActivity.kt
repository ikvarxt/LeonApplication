package com.example.leonapplication.app

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.leonapplication.app.theme.LeonApplicationTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val activities = getAllActivities() ?: emptyList()

    setContent {
      LeonApplicationTheme {
        Content(activities = activities.toImmutableList())
      }
    }
  }
}

@Composable
private fun Content(activities: ImmutableList<LaunchEntryActivity>, modifier: Modifier = Modifier) {
  Box(modifier.fillMaxSize()) {
    if (activities.isNotEmpty()) {
      LazyColumn(
        Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .systemBarsPadding()
          .align(Alignment.BottomCenter)
      ) {
        items(activities) {
          LaunchComponent(it.name, it.componentClassName)
        }
      }
    } else {
      Text(
        text = "no activities found",
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.align(Alignment.Center)
      )
    }
  }
}

@Composable
fun LaunchComponent(name: String, component: String, modifier: Modifier = Modifier) {
  val context = LocalContext.current

  Button(
    onClick = {
      Intent().apply {
        setComponent(ComponentName(context, component))
      }.also {
        context.startActivity(it)
      }
    },
    modifier = modifier
      .fillMaxWidth()
      .padding(8.dp)
  ) {
    Text(text = "Launch $name")
  }
}
