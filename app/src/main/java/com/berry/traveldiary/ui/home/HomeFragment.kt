package com.berry.traveldiary.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.berry.traveldiary.DiaryEntryActivity
import com.berry.traveldiary.PhotoGalleryActivity
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.FragmentHomeBinding
import com.berry.traveldiary.model.DiaryEntries
import com.berry.traveldiary.model.User
import com.berry.traveldiary.uitility.CommonUtils
import com.berry.traveldiary.uitility.CommonUtils.getStringPref
import com.berry.traveldiary.uitility.CommonUtils.setStringPref
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var diaryEntriesList: MutableList<DiaryEntries> = mutableListOf()
    private lateinit var myDatabase: MyDatabase
    private lateinit var listAdapter: ListAdapter


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        myDatabase = MyDatabase.getDatabase(requireContext())

        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    // Handle the Intent
                    getDiaryList()
                }
            }



        binding.floatingActionButton.setOnClickListener {
            startForResult.launch(Intent(requireContext(), DiaryEntryActivity::class.java))
        }


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDiaryList()

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listAdapter.filter(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                listAdapter.filter(p0.toString())
            }

        })
    }


    private fun getItemsFromDb(searchText: String) {
        var searchText = searchText
        searchText = "%$searchText%"

        CoroutineScope(Dispatchers.IO).launch {
            diaryEntriesList = myDatabase.diaryEntryDao().getSearchResults(searchText)
        }.invokeOnCompletion { }


    }


    private fun getDiaryList() {

        val loginData = getStringPref(CommonUtils.PREF_LOGIN, requireContext())
        Log.d("TAG", "loginData>>$loginData")
        if (!TextUtils.isEmpty(loginData)) {
            val user: User = Gson().fromJson(loginData, User::class.java)


        val recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        CoroutineScope(Dispatchers.IO).launch {
            diaryEntriesList = myDatabase.diaryEntryDao().getDiaryEntryForUser(user.id)
        }.invokeOnCompletion {
            listAdapter = ListAdapter(diaryEntriesList, itemClickListener)
            recyclerView.adapter = listAdapter
        }
        }
    }

    private val itemClickListener: ListAdapter.OnItemClickListener =
        object : ListAdapter.OnItemClickListener {
            override fun monItemClickListener(position: Int, entryId: Int) {
                val intent = Intent(requireContext(), PhotoGalleryActivity::class.java)
                startActivity(intent)
                setStringPref(CommonUtils.PREF_ENTRY_ID, entryId.toString(), requireContext())
            }

        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}