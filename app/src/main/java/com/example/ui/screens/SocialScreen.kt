package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SocialPost
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    posts: List<SocialPost>,
    onLikePost: (SocialPost) -> Unit,
    onAddPost: (String, String, String, Double, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPostDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Header Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, GymElectricLime.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = GymElectricLime,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "COMMUNITY ACCOUNTABILITY HUB",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = GymElectricLime,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Connect with gym partners, compare workout volume, celebrate personal records, and keep each other consistent.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Feed Posts
            items(posts) { post ->
                SocialPostCard(
                    post = post,
                    onLike = { onLikePost(post) },
                    onShareExternal = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "🔥 ${post.authorName} crushed ${post.workoutTitle} on IronPulse Gym!\nTotal Volume: ${post.volumeKg.toInt()} kg | Sets: ${post.setsCount} | Top PR: ${post.highlightPr}\n\"${post.caption}\""
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Workout Post"))
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // FAB to create post
        FloatingActionButton(
            onClick = { showPostDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 70.dp, end = 20.dp)
                .testTag("create_social_post_fab"),
            containerColor = GymElectricLime,
            contentColor = Color.Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Post")
        }
    }

    if (showPostDialog) {
        CreatePostDialog(
            onDismiss = { showPostDialog = false },
            onSave = { caption, routine, volume, sets, pr ->
                onAddPost("You (IronPulse Athlete)", "user_me", caption, volume, sets, pr)
                showPostDialog = false
            }
        )
    }
}

@Composable
fun SocialPostCard(
    post: SocialPost,
    onLike: () -> Unit,
    onShareExternal: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd • HH:mm", Locale.US).format(Date(post.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
            .testTag("social_post_${post.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GymElectricLime.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1).uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = GymElectricLime
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = post.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = dateStr,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // PR Badge if present
                if (post.highlightPr.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GymElectricAmber.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GymElectricAmber, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "PR SET", fontSize = 10.sp, fontWeight = FontWeight.Black, color = GymElectricAmber)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Workout Accomplishment Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = post.workoutTitle,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Volume: ${post.volumeKg.toInt()} kg",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GymElectricLime
                        )
                        Text(
                            text = "${post.setsCount} Sets Completed",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (post.highlightPr.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🏆 ${post.highlightPr}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GymElectricAmber
                        )
                    }
                }
            }

            if (post.caption.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Likes / Fistbumps and Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onLike,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("like_post_button_${post.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Hype Post",
                            tint = GymElectricAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${post.likesCount} Hypes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(
                    onClick = onShareExternal,
                    modifier = Modifier.testTag("external_share_post_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Post Externally",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Int, String) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var workoutTitle by remember { mutableStateOf("Hypertrophy Push Workout") }
    var volumeText by remember { mutableStateOf("10500") }
    var setsText by remember { mutableStateOf("18") }
    var prText by remember { mutableStateOf("Barbell Bench Press: 100kg x 6") }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GymElectricLime.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "POST TO GYM COMMUNITY",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = GymElectricLime
                )

                OutlinedTextField(
                    value = workoutTitle,
                    onValueChange = { workoutTitle = it },
                    label = { Text("Workout Routine") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = volumeText,
                        onValueChange = { volumeText = it },
                        label = { Text("Volume (kg)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = setsText,
                        onValueChange = { setsText = it },
                        label = { Text("Sets") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = prText,
                    onValueChange = { prText = it },
                    label = { Text("PR Highlight") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Caption & Motivation") },
                    placeholder = { Text("Crushed heavy sets today! How's your split going?") },
                    modifier = Modifier.fillMaxWidth().testTag("post_caption_input"),
                    maxLines = 3
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onSave(
                                caption,
                                workoutTitle,
                                volumeText.toDoubleOrNull() ?: 10000.0,
                                setsText.toIntOrNull() ?: 16,
                                prText
                            )
                        },
                        modifier = Modifier.weight(1f).testTag("save_community_post_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GymElectricLime, contentColor = Color.Black)
                    ) {
                        Text("Post", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
