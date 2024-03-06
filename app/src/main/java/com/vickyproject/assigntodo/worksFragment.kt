package com.vickyproject.assigntodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vickyproject.assigntodo.databinding.FragmentWorksBinding

class worksFragment : Fragment() {

    private lateinit var binding: FragmentWorksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWorksBinding.inflate(layoutInflater)
        binding.fabAssignWork.setOnClickListener {
            findNavController().navigate(R.id.action_worksFragment_to_assignWorkFragment)
        }
        return binding.root
    }

}