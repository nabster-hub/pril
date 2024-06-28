package com.example.tateknew.ui.getData

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.tateknew.DatabaseHelper
import com.example.tateknew.NetworkManager
import com.example.tateknew.TokenManager
import com.example.tateknew.databinding.FragmentGetdataBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class getDataFragment : Fragment() {

    private var _binding: FragmentGetdataBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var loadingIndicator: ProgressBar

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
        loadingIndicator = binding.loadingIndicator

        getDataViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        button.setOnClickListener {
            if (isNetworkAvailable(requireContext())) {
                getDatas()
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun getDatas() {
        loadingIndicator.visibility = View.VISIBLE
        var dbHelper = DatabaseHelper(this.requireContext())
        val networkManager = NetworkManager()
        val token = TokenManager(this.requireContext()).getToken()
        val jsonObject = Gson().fromJson<JsonObject>(token, JsonObject::class.java)
        val accessToken = jsonObject.get("access_token").asString

        lifecycleScope.launch{
            val success = withContext(Dispatchers.IO){
                networkManager.getTasks(accessToken, dbHelper)
            }

            loadingIndicator.visibility = View.GONE
            if (success) {
                Toast.makeText(requireContext(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Не удалось сохранить данные", Toast.LENGTH_SHORT).show()
            }
        }
//        networkManager.getTasks(accessToken, dbHelper){ success ->
//            activity?.runOnUiThread{
//                loadingIndicator.visibility = View.GONE
//                if(success){
//                    Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireContext(), "Failed to save data", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}