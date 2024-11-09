package com.mymoney.android.home.fragments.records

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mymoney.android.R
import com.mymoney.android.databinding.FragmentRecordsBinding

class RecordsFragment : Fragment(R.layout.fragment_records) {

    private lateinit var binding: FragmentRecordsBinding

    companion object {
        fun newInstance(): RecordsFragment {
            return RecordsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

}