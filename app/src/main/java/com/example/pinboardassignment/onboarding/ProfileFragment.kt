package com.example.pinboardassignment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.pinboardassignment.base.BaseFragment
import com.example.pinboardassignment.databinding.FragmentSecondBinding
import com.example.pinboardassignment.model.PinBoardResponse
import com.example.pinboardassignment.network.ErrorResponse
import com.example.pinboardassignment.network.JsonArrayResponse
import com.example.pinboardassignment.utils.AppUtils
import com.example.pinboardassignment.utils.LoggerClass
import com.example.pinboardassignment.viewmodels.PinBoardViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class ProfileFragment : BaseFragment() {

    lateinit var binding: FragmentSecondBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    lateinit var pinBoardResponse: PinBoardResponse

    private val pinBoardViewModel by activityViewModels<PinBoardViewModel>()

    var sUserId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get arguments
        arguments?.let {
            sUserId = it.getString("id") ?: ""
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //observer for pin board response
        pinBoardViewModel.arrPinBoardResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is JsonArrayResponse -> {
                    pinBoardResponse = it.data.find { it.id.equals(sUserId, false) }!!
                    populateData()
                }
                is ErrorResponse -> LoggerClass.getLoggerClass().verbose(data = it.sData)
            }

        })

        //observer for getting bitmap from firt fragment
        pinBoardViewModel.bitmap.observe(viewLifecycleOwner, {

            if (it != null) {
                AppUtils.populateGlide(
                    requireActivity(),
                    it,
                    binding.sivFragmentSecondBanner
                )
            }


        })

    }

    //populate data
    private fun populateData() {

        AppUtils.populateGlideWithDiskCache(
            requireContext(),
            pinBoardResponse.user.profile_image.large,
            binding.sivFragmentSecondProfile,
        )
        binding.mtvSecondFragmentLink.text = pinBoardResponse.links.html
        binding.mtvSecondFragmentUserName.text = pinBoardResponse.user.name

    }


}