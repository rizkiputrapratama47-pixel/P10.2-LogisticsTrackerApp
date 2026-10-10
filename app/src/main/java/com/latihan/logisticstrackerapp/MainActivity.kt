package com.latihan.logisticstrackerapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    // 1. Deklarasi elemen antarmuka pengguna
    private lateinit var editTextTrackingNumber: EditText
    private lateinit var buttonTrack: Button
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var cardResult: CardView
    private lateinit var textViewErrorMessage: TextView

    // Elemen informasi paket
    private lateinit var textViewResiTitle: TextView
    private lateinit var textViewStatusBadge: TextView
    private lateinit var textViewCourier: TextView
    private lateinit var textViewLocation: TextView
    private lateinit var textViewRecipient: TextView
    private lateinit var textViewEta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // 2. Inisialisasi elemen tampilan
        editTextTrackingNumber =
            findViewById(R.id.editTextTrackingNumber)
        buttonTrack = findViewById(R.id.buttonTrack)
        progressBarLoading = findViewById(R.id.progressBarLoading)
        cardResult = findViewById(R.id.cardResult)
        textViewErrorMessage =
            findViewById(R.id.textViewErrorMessage)

        textViewResiTitle = findViewById(R.id.textViewResiTitle)
        textViewStatusBadge = findViewById(R.id.textViewStatusBadge)
        textViewCourier = findViewById(R.id.textViewCourier)
        textViewLocation = findViewById(R.id.textViewLocation)
        textViewRecipient = findViewById(R.id.textViewRecipient)
        textViewEta = findViewById(R.id.textViewEta)

        // 3. Aksi tombol Lacak
        buttonTrack.setOnClickListener {
            val nomorResi =
                editTextTrackingNumber.text.toString().trim()

            if (nomorResi.isNotEmpty()) {
                lacakPaket(nomorResi)
            } else {
                Toast.makeText(
                    this,
                    "Silakan ketik nomor resi terlebih dahulu (contoh: EXP-8801)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun lacakPaket(nomorResi: String) {
        // STATE 1: LOADING
        progressBarLoading.visibility = View.VISIBLE
        cardResult.visibility = View.GONE
        textViewErrorMessage.visibility = View.GONE
        buttonTrack.isEnabled = false

        // Panggilan jaringan berjalan di background thread
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ApiClient.apiService.getTrackingDetail(nomorResi)

                // Kembali ke Main Thread untuk memperbarui UI
                withContext(Dispatchers.Main) {
                    progressBarLoading.visibility = View.GONE
                    buttonTrack.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {
                        // STATE 2: SUCCESS
                        val dataPaket = response.body()!!
                        tampilkanDataPaket(dataPaket)
                    } else {
                        // STATE 3: SERVER ERROR
                        textViewErrorMessage.text =
                            "Nomor resi [$nomorResi] tidak ditemukan pada database server (HTTP ${response.code()})"
                        textViewErrorMessage.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                // STATE 4: NETWORK FAILURE
                withContext(Dispatchers.Main) {
                    progressBarLoading.visibility = View.GONE
                    buttonTrack.isEnabled = true

                    textViewErrorMessage.text =
                        "Koneksi internet bermasalah: ${e.localizedMessage ?: "Gagal terhubung ke server"}"
                    textViewErrorMessage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun tampilkanDataPaket(dataPaket: TrackingResponse) {
        cardResult.visibility = View.VISIBLE

        textViewResiTitle.text =
            "Resi: ${dataPaket.trackingNumber}"

        textViewCourier.text =
            "Kurir: ${dataPaket.serviceType} (${dataPaket.courierName})"

        textViewLocation.text =
            "Posisi Terakhir: ${dataPaket.lastLocation}"

        textViewRecipient.text =
            "Penerima: ${dataPaket.recipientName}"

        textViewEta.text =
            "Estimasi Tiba: ${dataPaket.estimatedDelivery}"

        // Pengaturan warna badge status
        textViewStatusBadge.text = dataPaket.status

        when (dataPaket.status.uppercase()) {
            "DELIVERED" -> {
                textViewStatusBadge.setBackgroundColor(
                    Color.parseColor("#DCFCE7")
                )
                textViewStatusBadge.setTextColor(
                    Color.parseColor("#15803D")
                )
            }

            "IN_TRANSIT" -> {
                textViewStatusBadge.setBackgroundColor(
                    Color.parseColor("#FEF3C7")
                )
                textViewStatusBadge.setTextColor(
                    Color.parseColor("#B45309")
                )
            }

            else -> {
                textViewStatusBadge.setBackgroundColor(
                    Color.parseColor("#F1F5F9")
                )
                textViewStatusBadge.setTextColor(
                    Color.parseColor("#475569")
                )
            }
        }
    }
}