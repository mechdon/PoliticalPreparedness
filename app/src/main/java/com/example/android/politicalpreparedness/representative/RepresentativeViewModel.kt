package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel: ViewModel() {

    // Establish live data for representatives and address
    private var _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> get() = _representatives

    private var _address = MutableLiveData<Address>()
    val address: LiveData<Address> get() = _address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    // Create function to fetch representatives from API from a provided address
    fun getRepresentatives(address: String) {
        viewModelScope.launch {
            val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address)
            _representatives.value = offices.flatMap { office ->
                office.getRepresentatives(officials)
            }
        }
    }

    init {
        _address.value = Address("", "", "", "Alabama", "")
    }

    // Create function get address from geo location
    fun setAddressFromLocation(address: Address) {
        _address.value = address
    }


}
