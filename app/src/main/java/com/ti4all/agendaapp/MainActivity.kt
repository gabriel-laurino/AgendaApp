package com.ti4all.agendaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ti4all.agendaapp.data.Agenda
import com.ti4all.agendaapp.data.AgendaViewModel
import com.ti4all.agendaapp.ui.theme.AgendaAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AgendaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgendaAppTheme {
                AgendaScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AgendaList(agenda: Agenda, onEditClick: (Agenda) -> Unit, onDeleteClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Nome: ${agenda.nome}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Telefone: ${agenda.telefone}")

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { onEditClick(agenda) }) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDeleteClick(agenda.id) }) {
                    Text("Excluir")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(viewModel: AgendaViewModel) {
    val agendaList by viewModel.agendaList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingAgenda by remember { mutableStateOf<Agenda?>(null) }

    LaunchedEffect(Unit) {
        viewModel.listarTodos()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Agenda Telefônica") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(agendaList) { agenda ->
                    AgendaList(
                        agenda = agenda,
                        onEditClick = {
                            editingAgenda = it
                            showDialog = true
                        },
                        onDeleteClick = { id ->
                            viewModel.deletar(id)
                        }
                    )
                }
            }
        }

        if (showDialog) {
            AgendaFormDialog(
                agenda = editingAgenda,
                onDismissRequest = {
                    showDialog = false
                    editingAgenda = null
                }
            ) { nome, telefone ->
                if (editingAgenda == null) {
                    viewModel.inserir(Agenda(nome = nome, telefone = telefone))
                } else {
                    viewModel.atualizar(editingAgenda!!.id, nome, telefone)
                }
                showDialog = false
                editingAgenda = null
            }
        }
    }
}

@Composable
fun AgendaFormDialog(
    agenda: Agenda? = null,
    onDismissRequest: () -> Unit,
    onAddClick: (String, String) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var nome by remember { mutableStateOf(agenda?.nome ?: "") }
                var telefone by remember { mutableStateOf(agenda?.telefone ?: "") }

                val isButtonEnabled = nome.isNotBlank() && telefone.isNotBlank()

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    label = { Text("Telefone") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onAddClick(nome, telefone)
                    },
                    enabled = isButtonEnabled  // Desabilitar botão se os campos não estiverem preenchidos
                ) {
                    Text(if (agenda == null) "Adicionar contato" else "Atualizar contato")
                }
            }
        }
    }
}