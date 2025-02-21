package com.example.yuplayer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Server {
     abstract val name : String
     abstract fun onClick()
}

// 本地文件夹
@Serializable
@SerialName("local")
data class LocalServer(
    override val name: String,
    val path: String
) : Server() {
    override fun onClick() {
        TODO("Not yet implemented")
    }
}

// SMB 服务器
@Serializable
@SerialName("smb")
data class SmbServer(
    override val name: String,
    val address: String,
    val port: Int,
    val username: String,
    val password: String
) : Server() {
    override fun onClick() {
        TODO("Not yet implemented")
    }
}

// Emby 服务器
@Serializable
@SerialName("emby")
data class EmbyServer(
    override val name: String,
    val address: String,
    val port: Int,
    val username: String,
    val password: String
) : Server() {
    override fun onClick() {
        TODO("Not yet implemented")
    }
}


@Serializable
enum class ServerType {
    Local, Smb, Emby
}