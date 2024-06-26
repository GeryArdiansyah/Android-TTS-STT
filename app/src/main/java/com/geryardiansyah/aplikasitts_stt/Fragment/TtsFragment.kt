package com.geryardiansyah.aplikasitts_stt.Fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.geryardiansyah.aplikasitts_stt.R
import java.util.Locale

class TtsFragment : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tts, container, false)
        editText = rootView.findViewById(R.id.editText)
        val buttonSpeak: ImageView = rootView.findViewById(R.id.buttonSpeak)

        buttonSpeak.setOnClickListener { speak() }

        // Periksa apakah TextToSpeech sudah terinstal
        val checkIntent = Intent()
        checkIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        startActivityForResult(checkIntent, 1)

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // TextToSpeech sudah terinstal, inisialisasi
                textToSpeech = TextToSpeech(requireContext(), this)
            } else {
                // TextToSpeech belum terinstal, tampilkan pesan ke pengguna
                Toast.makeText(
                    requireContext(),
                    "Text-to-Speech belum terinstal, silakan instal terlebih dahulu.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set the language to Indonesian (ID)
            val result = textToSpeech.setLanguage(Locale("id", "ID"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                editText.isEnabled = false
                speakOut("Bahasa Indonesia tidak didukung pada perangkat ini.")
            } else {
                editText.isEnabled = true
            }
        } else {
            editText.isEnabled = false
            speakOut("Text-to-Speech tidak tersedia pada perangkat ini.")
        }
    }

    private fun speak() {
        val teksTerucap = editText.text.toString().trim()
        if (teksTerucap.isNotEmpty()) {
            speakOut(teksTerucap)
        } else {
            Toast.makeText(requireContext(), "Teks tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            @Suppress("DEPRECATION")
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}