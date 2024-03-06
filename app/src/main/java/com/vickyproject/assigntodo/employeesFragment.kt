package com.vickyproject.assigntodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.vickyproject.assigntodo.databinding.FragmentEmployeesBinding

class employeesFragment : Fragment() {

    private lateinit var binding: FragmentEmployeesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEmployeesBinding.inflate(layoutInflater)

        binding.btn1.setOnClickListener{
            findNavController().navigate(R.id.action_employeesFragment_to_worksFragment)
        }

        return binding.root
    }

}