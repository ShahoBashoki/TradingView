package com.shaho.tradingview.ui.first

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shaho.tradingview.R
import com.shaho.tradingview.databinding.FragmentFirstBinding
import com.shaho.tradingview.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstFragment : Fragment(R.layout.fragment_first) {

    private val viewModel: FirstViewModel by viewModels()
    private lateinit var binding: FragmentFirstBinding
    private var startFragment = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (::binding.isInitialized.not())
            binding = FragmentFirstBinding.inflate(inflater)
        else
            startFragment = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (startFragment)
            viewModel.getAccounts().observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Failure -> Log.i("shahoLog", "Failure: ${it.message}")
                    Resource.Loading -> Log.i("shahoLog", "Loading: ")
                    is Resource.Success -> Log.i("shahoLog", "Success: ${it.data}")
                }
            }
    }
}
