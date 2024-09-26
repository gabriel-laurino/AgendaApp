package com.ti4all.agendaapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4all.agendaapp.AgendaApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgendaViewModel(application: AgendaApplication) : ViewModel() {
    private val agendaDao = application.database.agendaDao()
    private val _agendaList = MutableStateFlow<List<Agenda>>(emptyList())
    val agendaList : StateFlow<List<Agenda>> = _agendaList

    init {
        listarTodos()
    }

    // Construtor padrão necessário para ViewModelProvider
    @Suppress("unused")
    constructor() : this(AgendaApplication.instance)

    fun listarTodos() {
        viewModelScope.launch {
            _agendaList.value = agendaDao.listarTodos()
        }
    }

    fun inserir(agenda: Agenda) {
        viewModelScope.launch {
            agendaDao.inserir(agenda)
            listarTodos()
        }
    }

    fun deletar(id: Int) {
        viewModelScope.launch {
            agendaDao.deletar(id)
            listarTodos()
        }
    }

    fun atualizar(id: Int, nome: String, telefone: String) {
        viewModelScope.launch {
            agendaDao.atualizar(id, nome, telefone)
            listarTodos()
        }
    }
}