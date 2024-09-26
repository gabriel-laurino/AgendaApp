package com.ti4all.agendaapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ti4all.agendaapp.data.Agenda

@Dao
interface AgendaDao {

    @Insert
    suspend fun inserir(contato: Agenda)

    @Query("SELECT * FROM agenda")
    suspend fun listarTodos() : List<Agenda>

    @Query("DELETE FROM agenda WHERE id = :id")
    suspend fun deletar(id: Int)

    @Query("UPDATE agenda SET nome = :nome, telefone = :telefone WHERE id = :id")
    suspend fun atualizar(id: Int, nome: String, telefone: String)
}