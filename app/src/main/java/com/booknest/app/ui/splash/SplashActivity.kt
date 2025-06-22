package com.booknest.app.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.booknest.app.MainActivity
import com.booknest.app.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Scale animation on "Book Nest" text
        val scaleX = ObjectAnimator.ofFloat(binding.splashText, View.SCALE_X, 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.splashText, View.SCALE_Y, 0f, 1f)
        val animatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 1500
            interpolator = AccelerateDecelerateInterpolator()
        }

        animatorSet.start()

        // Delay and navigate to HomeActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("destination", if (currentUser != null) "home" else "signin")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }, 2000) // 1500ms animation + 500ms delay
    }

}
