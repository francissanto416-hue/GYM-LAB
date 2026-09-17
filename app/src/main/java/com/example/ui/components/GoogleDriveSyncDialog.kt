package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.drive.GoogleDriveUser
import com.example.ui.theme.GymElectricAmber
import com.example.ui.theme.GymElectricLime
import com.example.ui.theme.GymNeonCyan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleDriveSyncDialog(
    currentUser: GoogleDriveUser?,
    lastBackupTimestamp: Long,
    isSyncing: Boolean,
    syncMessage: String?,
    isWebSecurityVerified: Boolean,
    onSignIn: (email: String, name: String) -> Unit,
    onSignOut: () -> Unit,
    onBackupNow: () -> Unit,
    onVerifySecurity: () -> Unit,
    onDismiss: () -> Unit
) {
    var emailInput by remember { mutableStateOf(currentUser?.email ?: "athlete.drive@gmail.com") }
    var nameInput by remember { mutableStateOf(currentUser?.displayName ?: "IronPulse Athlete") }
    var showSignUpForm by remember { mutableStateOf(currentUser == null) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GymNeonCyan.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header with Google Drive & Security Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GymNeonCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = GymNeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GOOGLE DRIVE STORAGE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = GymNeonCyan,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Web Integration & Security",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Text("✕", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Security Status Badge Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isWebSecurityVerified) GymElectricLime.copy(alpha = 0.4f) else GymElectricAmber.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isWebSecurityVerified) Icons.Default.Shield else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isWebSecurityVerified) GymElectricLime else GymElectricAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isWebSecurityVerified) "TLS / OAuth 2.0 Web Security Active" else "Web Connection: Checking TLS Policy",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWebSecurityVerified) GymElectricLime else GymElectricAmber
                                )
                                Text(
                                    text = "AES-256 JSON Storage · Scope: drive.file",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(
                            onClick = onVerifySecurity,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Verify Connection",
                                tint = GymNeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // User Account / Google Sign-Up Box
                if (currentUser != null && !showSignUpForm) {
                    // Logged-in Google Drive Account
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(GymElectricLime),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = currentUser.displayName.take(1).uppercase(),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = Color.Black
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = currentUser.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = currentUser.email,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Text(
                                    text = "Connected",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymElectricLime
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val lastSyncText = if (lastBackupTimestamp > 0) {
                                SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.US).format(Date(lastBackupTimestamp))
                            } else {
                                "Never synced to Drive yet"
                            }

                            Text(
                                text = "Last Drive Backup: $lastSyncText",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { showSignUpForm = true },
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Switch Account", fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = onSignOut,
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Sign Out", fontSize = 10.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                } else {
                    // Google Sign-Up / Connection Form
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "SIGN IN / CONNECT GOOGLE ACCOUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = GymElectricLime,
                            letterSpacing = 0.5.sp
                        )

                        Text(
                            text = "Connect Google Drive storage to securely backup workout history, PR set records, custom splits, and body transformation measurements.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth().testTag("drive_name_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Google Account Email") },
                            placeholder = { Text("your.email@gmail.com") },
                            modifier = Modifier.fillMaxWidth().testTag("drive_email_input"),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (emailInput.isNotBlank()) {
                                    onSignIn(emailInput.trim(), nameInput.trim())
                                    showSignUpForm = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_google_signup_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GymNeonCyan, contentColor = Color.Black)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Connect Google Drive Storage", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Sync status banner if active
                if (syncMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, GymElectricLime.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GymElectricLime, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = syncMessage, fontSize = 11.sp, color = GymElectricLime, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Primary Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }

                    Button(
                        onClick = onBackupNow,
                        enabled = !isSyncing,
                        modifier = Modifier.weight(1.4f).testTag("backup_to_drive_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GymElectricLime, contentColor = Color.Black)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Backing up...", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Backup to Drive", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
