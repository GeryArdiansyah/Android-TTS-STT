package com.geryardiansyah.aplikasitts_stt.Fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.geryardiansyah.aplikasitts_stt.R
import java.util.*

class SttFragment : Fragment() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var resultTextView: TextView
    private var isListening = false
    private var isCooldown = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_stt, container, false)
        resultTextView = rootView.findViewById(R.id.textView)
        val speakButton: ImageView = rootView.findViewById(R.id.speakButton)
        speakButton.setOnClickListener {
            if (!isListening && !isCooldown) {
                startSpeechToText()
                resultTextView.text = "Merekam..."
                isCooldown = true
                Handler().postDelayed({ isCooldown = false }, 2000)
            } else if (isListening) {
                stopSpeechToText()
                resultTextView.text = "Katakan sesuatu..."
            }
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())

        return rootView
    }

    private fun startSpeechToText() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        } else {
            startSpeechRecognition()
        }
    }

    private fun startSpeechRecognition() {
        val bahasaIndonesia = Locale("id", "ID")
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> resultTextView.text = "Tidak ada suara yang terdeteksi."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        resultTextView.text = "Waktu koneksi habis. Coba lagi."
                        Handler().postDelayed({ startSpeechRecognition() }, 1000)
                    }
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                        resultTextView.text = "Izin mikrofon diperlukan untuk pengenalan ucapan."
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.RECORD_AUDIO),
                            PERMISSIONS_REQUEST_RECORD_AUDIO
                        )
                    }
                    else -> resultTextView.text = "Error: $error"
                }
                isListening = false
            }

            override fun onResults(results: Bundle?) {
                val hasilUcapan = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!hasilUcapan.isNullOrEmpty()) {
                    resultTextView.text = hasilUcapan[0]
                } else {
                    resultTextView.text = "Tidak ada ucapan yang terdeteksi."
                }
                isListening = false
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, bahasaIndonesia)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Mulai Berbicara")
        }
        speechRecognizer.startListening(intent)

        isListening = true
    }

    private fun stopSpeechToText() {
        speechRecognizer.stopListening()
        isListening = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechRecognition()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Izin mikrofon diperlukan untuk pengenalan ucapan.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer.destroy()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }
}