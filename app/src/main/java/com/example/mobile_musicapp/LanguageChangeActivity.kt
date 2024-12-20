package com.example.mobile_musicapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile_musicapp.databinding.ActivityLanguageChangeBinding
import com.example.mobile_musicapp.helpers.LocalHelper
import com.example.mobile_musicapp.helpers.SharedPreferencesUtils

class LanguageChangeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageChangeBinding
    private var isChoiceVN = false
    private var isChoiceUs = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLanguageChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnDone.setOnClickListener {
            if (isChoiceUs || isChoiceVN) {
                LocalHelper.setLanguageLocale(this)
                recreate()
            }else {
                Toast.makeText(this, getString(R.string.tv_choice_language), Toast.LENGTH_SHORT).show()
            }
        }
        binding.americanTvLanguage.setOnClickListener {
            binding.bgUs.setBackgroundResource(R.drawable.bg_color_yellow_language)
            binding.bgVi.setBackgroundResource(R.drawable.bg_color_white_language)
            SharedPreferencesUtils.setLanguageCode (this,"en")
            isChoiceUs = true
        }
        binding.vietnamTvLanguage.setOnClickListener {
            binding.bgUs.setBackgroundResource(R.drawable.bg_color_white_language)
            binding.bgVi.setBackgroundResource(R.drawable.bg_color_yellow_language)
            SharedPreferencesUtils.setLanguageCode (this,"vi")
            isChoiceVN = true
        }
    }
}