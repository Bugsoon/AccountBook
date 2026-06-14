package com.example.accountbook.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.accountbook.R
import com.example.accountbook.ui.activity.AutoRecordActivity
import com.example.accountbook.ui.activity.ManageCategoriesActivity

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManageCategories: LinearLayout = view.findViewById(R.id.layout_manage_categories)
        layoutManageCategories.setOnClickListener {
            val intent = Intent(requireContext(), ManageCategoriesActivity::class.java)
            startActivity(intent)
        }

        val layoutAutoRecord: LinearLayout = view.findViewById(R.id.layout_auto_record)
        layoutAutoRecord.setOnClickListener {
            val intent = Intent(requireContext(), AutoRecordActivity::class.java)
            startActivity(intent)
        }
    }
}