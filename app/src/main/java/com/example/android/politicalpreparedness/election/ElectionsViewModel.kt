package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

// Construct ViewModel and provide election datasource
class ElectionsViewModel (private val dataSource: ElectionDao): ViewModel() {

    private var _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>> get() = _upcomingElections

    private var _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>> get() = _savedElections

    init {
        getUpcomingElections()
        getSavedElections()
    }

    // retrieve upcoming elections from API
    fun getUpcomingElections() {
        viewModelScope.launch {
            _upcomingElections.value = CivicsApi.retrofitService.getElections().elections.filter { !it.division.state.isNullOrEmpty() }
        }
    }

    // retrieve saved elections from local database
    fun getSavedElections() {
        viewModelScope.launch {
            _savedElections.value = dataSource.getElections()
        }

    }

    // functions to navigate to saved or upcoming election voter info
    private val _navigateToElection = MutableLiveData<Election>()
    val navigateToElection: LiveData<Election>
        get() = _navigateToElection

    fun onElectionClicked(election: Election){
        _navigateToElection.value = election
    }

    fun onElectionNavigated() {
        _navigateToElection.value = null
    }

}