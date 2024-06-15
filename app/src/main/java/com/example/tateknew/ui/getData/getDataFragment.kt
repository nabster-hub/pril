package com.example.tateknew.ui.getData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tateknew.NetworkManager
import com.example.tateknew.TokenManager
import com.example.tateknew.databinding.FragmentGetdataBinding
import com.google.gson.Gson
import com.google.gson.JsonObject


class getDataFragment : Fragment() {

    private var _binding: FragmentGetdataBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val getDataViewModel =
            ViewModelProvider(this).get(getDataViewModel::class.java)

        _binding = FragmentGetdataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGetData
        val button = binding.getTasks
        getDataViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        button.setOnClickListener {
            getDatas();
        }
        return root
    }

    private fun getDatas() {
        val networkManager = NetworkManager()
        val token = TokenManager(this.requireContext()).getToken()
        val jsonObject = Gson().fromJson<JsonObject>(token, JsonObject::class.java)
        val accessToken = jsonObject.get("access_token").asString

        networkManager.getTasks(accessToken)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}