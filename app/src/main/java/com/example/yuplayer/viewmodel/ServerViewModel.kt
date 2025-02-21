package com.example.yuplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yuplayer.data.ServerRepository
import com.example.yuplayer.model.Server
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ServerRepository(application)

    val servers: StateFlow<List<Server>> = repository.getServersFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addServer(server: Server) {
        viewModelScope.launch {
            repository.saveServer(server)
        }
    }

    fun deleteServer(server: Server) {
        viewModelScope.launch {
            repository.deleteServer(server)
        }
    }
} 