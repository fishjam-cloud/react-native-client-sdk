package com.example.fishjamandroidexample

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fishjamandroidexample.ui.theme.FishjamAndroidExampleTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FishjamAndroidExampleTheme {
                MainContent()
            }
        }
    }

    private fun connect(token: String) {
        Intent(this@MainActivity, RoomActivity::class.java).apply {
            putExtra(
                RoomActivity.ARGS,
                RoomActivity.BundleArgs(token)
            )
        }.let {
            startActivity(it)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainContent() {
        val roomToken = remember { mutableStateOf(TextFieldValue("")) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = roomToken.value,
                    onValueChange = { roomToken.value = it },
                    placeholder = { Text("Enter room token...") },
                    label = { Text("Room token") }
                )

                ConnectWithPermissions {
                    Button(
                        onClick = {
                            connect(roomToken.value.text.trim())
                        },
                        enabled = !(roomToken.value.text.isEmpty()),
                        modifier =
                            Modifier
                                .width(200.dp)
                    ) {
                        Text("Join room")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun ConnectWithPermissions(content: @Composable () -> Unit) {
        val multiplePermissionsState =
            rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )

        val alreadyRequested = remember { mutableStateOf(false) }

        if (multiplePermissionsState.allPermissionsGranted) {
            content()
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val textToShow =
                    when {
                        multiplePermissionsState.shouldShowRationale ->
                            "Application requires an access to a microphone and camera for it to work"

                        !multiplePermissionsState.shouldShowRationale && alreadyRequested.value ->
                            "You need to explicitly grant the access to the camera and microphone in system settings..."

                        else ->
                            null
                    }

                Button(
                    onClick = {
                        if (!multiplePermissionsState.shouldShowRationale && alreadyRequested.value) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.data = Uri.parse("package:$packageName")

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

                            startActivity(intent)
                        } else {
                            multiplePermissionsState.launchMultiplePermissionRequest()
                        }

                        alreadyRequested.value = true
                    }
                ) {
                    Text("Request permissions")
                }

                textToShow?.let {
                    Text(it, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
