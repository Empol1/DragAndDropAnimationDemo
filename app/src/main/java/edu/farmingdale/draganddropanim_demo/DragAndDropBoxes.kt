@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun DragAndDropBoxes(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(true) }
    var position by remember { mutableStateOf(IntOffset(0, 0)) } // State for rectangle position
    var dragSourceIndex by remember { mutableStateOf(-1) }
    var dragTargetIndex by remember { mutableStateOf(-1) }
    var dragDirection by remember { mutableStateOf("None") }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.2f)
        ) {
            val boxCount = 4
            var dragBoxIndex by remember { mutableIntStateOf(0) }

            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN).also {
                                    println("Drag started with MIME type: ${event.mimeTypes()}")
                                }
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        dragTargetIndex = index

                                        // Determine direction and log the drop
                                        dragDirection = when {
                                            dragTargetIndex > dragSourceIndex -> "Down"
                                            dragTargetIndex < dragSourceIndex -> "Up"
                                            else -> "None"
                                        }
                                        println("Dropped at index $index, dragDirection: $dragDirection")
                                        isPlaying = !isPlaying
                                        dragBoxIndex = index
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    this@Row.AnimatedVisibility(
                        visible = index == dragBoxIndex,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        // Replace the icon with a colored box
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (dragDirection == "Up") Color.Green else Color.Green)
                                .dragAndDropSource {
                                    detectTapGestures(
                                        onLongPress = {
                                            dragSourceIndex = index
                                            println("Drag initiated for box at index $index")
                                            startTransfer(
                                                transferData = DragAndDropTransferData(
                                                    clipData = ClipData.newPlainText("text", "Box $index")
                                                )
                                            )
                                        }
                                    )
                                }
                        )
                    }
                }
            }
        }

        val pOffset by animateIntOffsetAsState(
            targetValue = position,
            animationSpec = tween(3000, easing = LinearEasing)
        )

        val rtatView by animateFloatAsState(
            targetValue = if (isPlaying) 360f else 0.0f,
            animationSpec = repeatable(
                iterations = if (isPlaying) 10 else 1,
                tween(durationMillis = 3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color.Magenta)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .offset(pOffset.x.dp, pOffset.y.dp)
                    .rotate(rtatView) // Retain rotation
                    .background(Color.Blue)
                    .size(100.dp, 50.dp) // Rectangle dimensions
            )
        }

        // add a reset button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    position = IntOffset(0, 0) // Reset position to the center
                    dragDirection = "None"
                    dragSourceIndex = -1
                    dragTargetIndex = -1
                }
            ) {
                Text(text = "Reset")
            }
        }
    }
}