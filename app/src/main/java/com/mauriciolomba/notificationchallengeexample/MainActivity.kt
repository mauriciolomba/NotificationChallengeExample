package com.mauriciolomba.notificationchallengeexample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.mauriciolomba.notificationchallengeexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainNotification = MainNotification()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//       binding.buttonShowNotification.setOnClickListener {
//            mainNotification.createNotificationChannel(this)
//            mainNotification.showNotification(this)
//        }
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainNotification.createNotificationChannel(this)

        val notificationLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                mainNotification.showNotification(this)
            } else {
                toast("Sem permissão, sem notificação...")
            }
        }

        binding.buttonShowNotification.setOnClickListener {
            checkNotificationPermission(notificationLauncher)
        }

        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                toast("Permissão recebida")
            } else {
                toast("Permissão não concedida")
            }
        }

        binding.buttonRequestPermission.setOnClickListener {
            requestPermission(permissionLauncher, STORAGE_PERMISSION)
        }

    }

    private fun checkNotificationPermission(requestPermission: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(requestPermission, NOTIFICATION_PERMISSION)
        } else {
            mainNotification.showNotification(this)
        }
    }

    private fun requestPermission(
        requestPermission: ActivityResultLauncher<String>,
        permission: String
    ) = when {
        ContextCompat
            .checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
            // Usar a API
        }

        shouldShowRequestPermissionRationale(permission) -> {
            toast("Explicar para o usuário o porquê da permissão")
        }

        else -> {
            requestPermission.launch(permission)
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        private val STORAGE_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        private const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    }
}