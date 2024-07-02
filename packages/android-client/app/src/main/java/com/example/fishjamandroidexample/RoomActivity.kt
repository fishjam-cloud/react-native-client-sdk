package com.example.fishjamandroidexample

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.fishjamandroidexample.ui.theme.FishjamAndroidExampleTheme
import kotlinx.parcelize.Parcelize

class RoomActivity : ComponentActivity() {
    companion object {
        const val ARGS = "args"
    }

    @Parcelize
    data class BundleArgs(val roomToken: String) : Parcelable

    private val viewModel: RoomViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val (roomToken) =
            intent.getParcelableExtra<BundleArgs>(ARGS)
                ?: throw NullPointerException("Failed to decode intent's parcelable")
        viewModel.connect(roomToken)

        setContent {
            FishjamAndroidExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnect()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Content() {
        val participants = viewModel.participants.collectAsState()

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            Box {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    participants.value.forEach {
                        ParticipantCard(
                            participant = it,
                            videoViewLayout = VideoViewLayout.FILL,
                            size = Size(100f, 100f)
                        )
                    }

                    Button(
                        onClick = {
                            finish()
                        },
                        modifier =
                            Modifier
                                .width(200.dp)
                    ) {
                        Text("Disconnect")
                    }
                }
            }
        }
    }

    @Composable
    fun ParticipantCard(
        participant: Participant,
        videoViewLayout: VideoViewLayout,
        size: Size,
        onClick: (() -> Unit)? = null
    ) {
        Box(
            modifier =
                Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onClick?.invoke()
                    }
                    .clip(RoundedCornerShape(10.dp))
                    .height(size.height.dp)
                    .width(size.width.dp)
        ) {
            ParticipantVideoView(
                participant = participant,
                videoViewLayout = videoViewLayout,
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .fillMaxHeight()
            )
        }
    }
}
