package com.example.yuplayer.ui.dialog

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.yuplayer.model.*

sealed class ServerDialogState {
    data object SelectType : ServerDialogState()
    data class ConfigureSmb(val previousState: ServerDialogState? = null) : ServerDialogState()
    data class ConfigureEmby(val previousState: ServerDialogState? = null) : ServerDialogState()
}

@Composable
fun AddServerDialog(
    onDismiss: () -> Unit,
    onServerAdded: (Server) -> Unit
) {
    var dialogState by remember { mutableStateOf<ServerDialogState>(ServerDialogState.SelectType) }
    
    when (dialogState) {
        ServerDialogState.SelectType -> {
            ServerTypeDialog(
                onDismiss = onDismiss,
                onLocalSelected = { uri ->
                    if (uri != null) {
                        onServerAdded(
                            LocalServer(
                                name = uri.lastPathSegment ?: "本地文件夹",
                                path = uri.toString()
                            )
                        )
                    }
                    onDismiss()
                },
                onSmbSelected = {
                    dialogState = ServerDialogState.ConfigureSmb(ServerDialogState.SelectType)
                },
                onEmbySelected = {
                    dialogState = ServerDialogState.ConfigureEmby(ServerDialogState.SelectType)
                }
            )
        }
        is ServerDialogState.ConfigureSmb -> {
            SmbServerDialog(
                onDismiss = { dialogState = (dialogState as ServerDialogState.ConfigureSmb).previousState ?: ServerDialogState.SelectType },
                onConfirm = { server ->
                    onServerAdded(server)
                    onDismiss()
                }
            )
        }
        is ServerDialogState.ConfigureEmby -> {
            EmbyServerDialog(
                onDismiss = { dialogState = (dialogState as ServerDialogState.ConfigureEmby).previousState ?: ServerDialogState.SelectType },
                onConfirm = { server ->
                    onServerAdded(server)
                    onDismiss()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerTypeDialog(
    onDismiss: () -> Unit,
    onLocalSelected: (Uri?) -> Unit,
    onSmbSelected: () -> Unit,
    onEmbySelected: () -> Unit
) {
    val directoryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri -> onLocalSelected(uri) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择服务器类型") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { directoryPicker.launch(null) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("本地文件夹")
                }
                Button(
                    onClick = onSmbSelected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SMB 服务器")
                }
                Button(
                    onClick = onEmbySelected,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Emby 服务器")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmbServerDialog(
    onDismiss: () -> Unit,
    onConfirm: (SmbServer) -> Unit
) {
    var serverName by remember { mutableStateOf("") }
    var serverAddress by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("445") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加 SMB 服务器") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = serverName,
                    onValueChange = { serverName = it },
                    label = { Text("服务器名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = serverAddress,
                    onValueChange = { serverAddress = it },
                    label = { Text("服务器地址") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = serverPort,
                    onValueChange = { serverPort = it },
                    label = { Text("端口") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码（可选）") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        SmbServer(
                            name = serverName,
                            address = serverAddress,
                            port = serverPort.toIntOrNull() ?: 445,
                            username = username,
                            password = password
                        )
                    )
                },
                enabled = serverName.isNotBlank() && serverAddress.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmbyServerDialog(
    onDismiss: () -> Unit,
    onConfirm: (EmbyServer) -> Unit
) {
    var serverName by remember { mutableStateOf("") }
    var serverAddress by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("8096") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加 Emby 服务器") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = serverName,
                    onValueChange = { serverName = it },
                    label = { Text("服务器名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = serverAddress,
                    onValueChange = { serverAddress = it },
                    label = { Text("服务器地址") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = serverPort,
                    onValueChange = { serverPort = it },
                    label = { Text("端口") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码（可选）") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        EmbyServer(
                            name = serverName,
                            address = serverAddress,
                            port = serverPort.toIntOrNull() ?: 8096,
                            username = username,
                            password = password
                        )
                    )
                },
                enabled = serverName.isNotBlank() && serverAddress.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
} 