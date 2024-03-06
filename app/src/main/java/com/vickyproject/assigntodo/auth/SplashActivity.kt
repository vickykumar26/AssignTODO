package com.vickyproject.assigntodo.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.vickyproject.assigntodo.R
import com.vickyproject.assigntodo.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim)
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_anim)

        binding.lottie.setAnimation(topAnim)
        binding.appname.setAnimation(bottomAnim)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,SigninActivity::class.java))
            finish()
        },4000)
    }
}

