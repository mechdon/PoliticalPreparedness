package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var _viewModel: VoterInfoViewModel
    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val bundle = VoterInfoFragmentArgs.fromBundle(requireArguments())
        val electionId = bundle.argElectionId
        val division = bundle.argDivision


        // Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = VoterInfoViewModelFactory(dataSource, electionId, division)
        _viewModel = ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

        // Add binding values
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        // Handle loading of URLs
        _viewModel.votingLocationsUrl.observe(viewLifecycleOwner, Observer {

            it?.let {
                loadUrl(it)
                _viewModel.onVotingLocationNavigated()
            }
        })

        _viewModel.ballotInformationUrl.observe(viewLifecycleOwner, Observer {

            it?.let {
                loadUrl(it)
                _viewModel.onBallotInformationNavigated()
            }
        })


        // Handle save button UI state
        // cont'd Handle save button clicks
        _viewModel.isElectionSaved.observe(viewLifecycleOwner, Observer {  isElectionSaved ->
            if (isElectionSaved) {
                binding.saveElectionButton.text = getString(R.string.unfollow_election)
            } else {
                binding.saveElectionButton.text = getString(R.string.follow_election)
            }
        })

        return binding.root

    }

    // Create method to load URL intents
    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}