package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import java.lang.IllegalArgumentException

// Create Factory to generate ElectionViewModel with provided election datasource
class ElectionsViewModelFactory(private val dataSource: ElectionDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)){
            return ElectionsViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unable to create ElectionViewModel")
    }
}