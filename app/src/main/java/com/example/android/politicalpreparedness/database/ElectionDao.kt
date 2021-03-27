package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    // Insert query
    @Insert
    suspend fun insert(election: Election)

    // Select all election query
    @Query("SELECT * FROM election_table")
    suspend fun getElections(): List<Election>

    // Single election query
    @Query("SELECT * FROM election_table where id = :electionId")
    suspend fun getElectionById(electionId: Int): Election?

    // Delete query with Id
    @Query("DELETE FROM election_table where id = :electionId")
    suspend fun deleteElection(electionId: Int)

    // Clear query
    @Query("DELETE FROM election_table")
    suspend fun clear()

}