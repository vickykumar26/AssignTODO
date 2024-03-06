package com.vickyproject.assigntodo.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vickyproject.assigntodo.BossMainActivity
import com.vickyproject.assigntodo.EmployeeMainActivity
import com.vickyproject.assigntodo.R
import com.vickyproject.assigntodo.Users
import com.vickyproject.assigntodo.databinding.ActivitySigninBinding
import com.vickyproject.assigntodo.utils.Utils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupOpt.setOnClickListener {
            val intent = Intent(this@SigninActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.loginBtn.setOnClickListener{
            val email = binding.etEmail.text.toString()
            val password = binding.etPaswd.text.toString()

            loginUser(email,password)
        }
    }

    private fun loginUser(email: String, password: String){
        Utils.showDialog(this)
        val firebaseAuth = FirebaseAuth.getInstance()
        lifecycleScope.launch {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val currentUser = authResult.user?.uid

                if (currentUser != null) {

                    FirebaseDatabase.getInstance().getReference("Users").child(currentUser)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val currentUserData = snapshot.getValue(Users::class.java)
                                when (currentUserData?.userType) {
                                    "Boss" -> {

                                        startActivity(Intent(this@SigninActivity, BossMainActivity::class.java))
                                        finish()

                                    }

                                    "Employee" -> {
                                        startActivity(Intent(this@SigninActivity, EmployeeMainActivity::class.java))
                                        finish()

                                    }

                                    else -> {
                                        startActivity(Intent(this@SigninActivity, EmployeeMainActivity::class.java))
                                        finish()

                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Utils.showToast(this@SigninActivity, error.message)
                                Utils.hideDialog()
                            }

                        })
                } else {
                    Utils.showToast(this@SigninActivity, "User Not Found Please Sign UP first")
                    Utils.hideDialog()
                }
            } catch (e: Exception) {

                Utils.showToast(this@SigninActivity, e.message!!)
                Utils.hideDialog()

            }
        }
    }
}