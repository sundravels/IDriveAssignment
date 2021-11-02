package com.example.pinboardassignment.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

/**
 * A simple [BaseFragment]
 */
open class BaseFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun navigateToFragment(bundle: Bundle, id: Int) {
        findNavController().navigate(id, bundle)
    }

}