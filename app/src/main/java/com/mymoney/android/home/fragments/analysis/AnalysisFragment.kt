package com.mymoney.android.home.fragments.analysis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mymoney.android.R
import com.mymoney.android.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment(R.layout.fragment_analysis) {

    private lateinit var binding: FragmentAnalysisBinding

    companion object {
        fun newInstance(): AnalysisFragment {
            return AnalysisFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

}