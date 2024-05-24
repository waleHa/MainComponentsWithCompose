package com.example.maincomponents.ui

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.maincomponents.MainService
import com.example.maincomponents.R
import com.example.maincomponents.ui.theme.Green85

@Composable
fun ServiceToggleButton(context: Context) {
    var buttonText by remember { mutableStateOf(context.getString(R.string.stopped)) }
    var isPlaying by remember { mutableStateOf(false) }
    var isPause by remember { mutableStateOf(false) }

    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (isMyServiceRunning(MainService::class.java)) {
                        buttonText = context.getString(R.string.stopped)
                        isPlaying = false
                        isPause = true
                        context.stopService(Intent(context, MainService::class.java))
                    } else {
                        buttonText = context.getString(R.string.started)
                        isPlaying = true
                        context.startService(Intent(context, MainService::class.java))
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) Color.Red else Green85
                ),
                modifier = Modifier
                    .padding(16.dp)

            )
            {
                Text(buttonText)
            }

            Button(
                onClick = {
                    context.sendBroadcast(Intent("com.example.maincomponents.PAUSE_MUSIC"))
                    isPause = true
                },
                enabled = isPlaying && !isPause,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Pause")
            }

            Button(
                onClick = {
                    context.sendBroadcast(Intent("com.example.maincomponents.RESUME_MUSIC"))
                    isPause = false
                },
                enabled = isPlaying && isPause,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green85
                ),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Resume")
            }
        }

    }
}
