package com.xinchaongaymoi.hotelbookingapp.components

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xinchaongaymoi.hotelbookingapp.App
import com.xinchaongaymoi.hotelbookingapp.R
import com.xinchaongaymoi.hotelbookingapp.adapter.LanguageAdapter
import java.util.Locale

class LanguageBottomSheet : BottomSheetDialogFragment() {
    private var selectedPosition: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View?
    {
        val view = inflater.inflate(R.layout.language_bottom_sheet_layout, container, false)
        return view
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languages = listOf(getString(R.string.english), getString(R.string.vietnamese))
        val languageAdapter = LanguageAdapter(languages)
        { language ->
            if (language == getString(R.string.english))
            {
                setLocale("en")
                App.instance.language = getString(R.string.english)
            }
            else if (language == getString(R.string.vietnamese))
            {
                setLocale("vi")
                App.instance.language = getString(R.string.vietnamese)
            }
            dismiss()
        }
        val languageRecyclerView = view.findViewById<RecyclerView>(R.id.language_recycler_view)
        languageRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        languageRecyclerView?.adapter = languageAdapter
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val behavior = (dialog as? BottomSheetDialog)?.behavior
            behavior?.peekHeight = view.height
        }
        languageRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
    }
    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
        requireActivity().recreate()
    }
}