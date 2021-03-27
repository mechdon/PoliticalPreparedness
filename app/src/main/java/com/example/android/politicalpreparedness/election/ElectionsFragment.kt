package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment: Fragment() {

    // Declare ViewModel
    private lateinit var _viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dataSource)
        _viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        val binding: FragmentElectionBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)

        // Add binding values
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        // Link elections to voter info
        _viewModel.navigateToElection.observe(viewLifecycleOwner, Observer {  election ->

            election?.let {
                this.findNavController().navigate(
                        ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                                it.id, it.division))
                _viewModel.onElectionNavigated()
            }
        })

        // Initiate recycler adapters
        val clickListener = { election: Election -> _viewModel.onElectionClicked(election)}
        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener(clickListener))
        binding.upcomingRecyclerView.adapter = upcomingElectionsAdapter
        val savedElectionListAdapter = ElectionListAdapter(ElectionListener(clickListener))
        binding.savedRecyclerView.adapter = savedElectionListAdapter

        // Populate recycler adapters
        _viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                upcomingElectionsAdapter!!.submitList(it)
            }
        })

        _viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                savedElectionListAdapter!!.submitList(it)
            }
        })

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewModel.getSavedElections()
    }

}