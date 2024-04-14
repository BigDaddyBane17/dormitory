package com.example.tradeit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tradeit.R
import com.example.tradeit.databinding.FragmentEditProfileBinding
import com.example.tradeit.databinding.FragmentProductCardBinding

class ProductCardFragment : Fragment() {

    private var _binding: FragmentProductCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductCardBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}