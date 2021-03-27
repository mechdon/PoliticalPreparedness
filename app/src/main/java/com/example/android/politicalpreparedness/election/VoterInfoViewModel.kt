package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionDao,
                         private val electionId: Int,
                         private val division: Division) : ViewModel() {

    var electionfromDb: Election? = null

    private val _isElectionSaved = MutableLiveData<Boolean>()
    val isElectionSaved: LiveData<Boolean> get() = _isElectionSaved

    // Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse> get() = _voterInfo

    init {
        getVoterInfo()
        getElectionFromDb()
    }

    // Add var and methods to populate voter info
    private fun getVoterInfo() {
        viewModelScope.launch {
            var address = "country:${division.country}"

            if (division.state.isNotEmpty()) {
                address += "/state:${division.state}"
            }

            _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(address, electionId)
        }
    }

    // Add var and methods to support loading URLs
    private val _votingLocationsUrl = MutableLiveData<String>()
    val votingLocationsUrl: LiveData<String> get() = _votingLocationsUrl

    fun onVotingLocationsClick() {
        _votingLocationsUrl.value = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
    }

    fun onVotingLocationNavigated() {
        _votingLocationsUrl.value = null
    }

    private val _ballotInformationUrl = MutableLiveData<String>()
    val ballotInformationUrl: LiveData<String> get() = _ballotInformationUrl

    fun onBallotInformationClick() {
        _votingLocationsUrl.value = _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
    }

    fun onBallotInformationNavigated() {
        _ballotInformationUrl.value = null
    }


    // Add var and methods to save and remove elections to local database

    private fun saveElectionToDb() {
        viewModelScope.launch {
            voterInfo.value?.let {
                voterInfo.value?.let { dataSource.insert(it.election) }
                _isElectionSaved.value = true
            }
        }
    }

    private fun removedElectionFromDb() {
        viewModelScope.launch {
            voterInfo.value?.let { dataSource.deleteElection(it.election.id)}
            _isElectionSaved.value = false
        }
    }

    // cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    fun onSaveButtonClick() {
        if (_isElectionSaved.value == true) {
            removedElectionFromDb()
        } else {
            saveElectionToDb()
        }
    }

    fun getElectionFromDb() {
        viewModelScope.launch {
            electionfromDb = dataSource.getElectionById(electionId)
            if (electionfromDb != null) {
                _isElectionSaved.value = true
            } else {
                _isElectionSaved.value = false
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}