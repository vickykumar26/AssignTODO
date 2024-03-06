package com.vickyproject.assigntodo.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.vickyproject.assigntodo.Users
import com.vickyproject.assigntodo.databinding.ActivitySignUpBinding
import com.vickyproject.assigntodo.utils.Utils
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private var userImageUri : Uri? = null
    private var userType : String = ""
    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        userImageUri = it
        binding.ivUserImage.setImageURI(userImageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginOpt.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.apply {
            ivUserImage.setOnClickListener(){
                selectImage.launch("image/*")
            }
            binding.radioGroup.setOnCheckedChangeListener{_, checkedId ->
                userType = findViewById<RadioButton>(checkedId).text.toString()
                Log.d("TT",userType)
            }

            nameFocusListener()
            emailFocusListener()
            passwordFocusListener()
            cnfpasswordFocusListener()

            binding.signupBtn.setOnClickListener{createUser()}
        }
    }

    private fun createUser() {
        Utils.showDialog(this)

        binding.textInputName.helperText = validName()
        binding.textInputEmail.helperText = validEmail()
        binding.textInputPassword.helperText = validPassword()
        binding.textInputCnfpassword.helperText = validCnfPassword()

        val validName = binding.textInputName.helperText == null
        val validEmail = binding.textInputEmail.helperText == null
        val validPassword = binding.textInputPassword.helperText == null
        val validCnfPassword = binding.textInputCnfpassword.helperText == null

        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPaswd.text.toString()
        val confirmPassword = binding.etCnfpaswd.text.toString()

        if (validName && validEmail && validPassword && validCnfPassword) {

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {

                if (userImageUri == null) {
                    Utils.hideDialog()
                    Utils.showToast(this, "Please select one image")
                } else if (password == confirmPassword) {
                     if (userType.isNotEmpty()) {
                        uploadImageUri(name, email, password)
                    } else {
                         Utils.hideDialog()
                         Utils.showToast(this,"Please select your user type")
                     }
                }
            } else {
                Utils.hideDialog()
            }
        }else{
            Utils.hideDialog()
        }
    }

    private fun uploadImageUri(name: String, email: String, password: String) {

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val storageReference = FirebaseStorage.getInstance().getReference("profile").child(currentUserUid).child("profile.jpg")

        lifecycleScope.launch {
            try {
               val uploadTask = storageReference.putFile(userImageUri!!).await()
                if(uploadTask.task.isSuccessful){
                    val downloadURL = storageReference.downloadUrl.await()
                    saveUserData(name,email,password,downloadURL)
                }
                else{
                    Utils.hideDialog()
                    showToast("Upload failed: ${uploadTask.task.exception?.message}")
                }
            }catch (e : Exception){
                Utils.hideDialog()
                showToast("Upload failed: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread{
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData(name: String, email: String, password: String, downloadURL: Uri?) {

                lifecycleScope.launch {
                    val db = FirebaseDatabase.getInstance().getReference("Users")

                    try {
                        val firebaseAuth = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()

                        if (firebaseAuth.user != null) {
                            val uId = firebaseAuth.user?.uid.toString()
                            val saveUserType = if (userType == "Boss") "Boss" else "Employee"
                            val boss = Users(saveUserType, uId, name, email, password, downloadURL.toString())
                            db.child(uId).setValue(boss).await()
                            Utils.hideDialog()
                            Utils.showToast(this@SignUpActivity, "Signed Up Successfully")
                            val intent = Intent(this@SignUpActivity, SigninActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Utils.hideDialog()
                            Utils.showToast(this@SignUpActivity, "Signed Up Failed!")
                        }

                    } catch (e: Exception) {
                        Utils.hideDialog()
                        Utils.showToast(this@SignUpActivity, e.message.toString())

                    }

            }

    }

    private fun nameFocusListener()
    {
        binding.etName.setOnFocusChangeListener { _, focused ->
            if(!focused)
            {
                binding.textInputName.helperText = validName()
            }
        }
    }

    private fun validName(): String?
    {
        val nameText = binding.etName.text.toString()
        if(nameText.isEmpty())
        {
            return "Please enter your name"
        }
        return null
    }


    private fun emailFocusListener()
    {
        binding.etEmail.setOnFocusChangeListener { _, focused ->
            if(!focused)
            {
                binding.textInputEmail.helperText = validEmail()
            }
        }
    }

    private fun validEmail(): String?
    {
        val emailText = binding.etEmail.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
            return "Please enter valid email"
        }
        return null
    }

    private fun passwordFocusListener()
    {
        binding.etPaswd.setOnFocusChangeListener { _, focused ->
            if(!focused)
            {
                binding.textInputPassword.helperText = validPassword()
            }
        }
    }

    private fun validPassword(): String?
    {
        val passwordText = binding.etPaswd.text.toString()
        if(passwordText.isEmpty())
        {
            return "Please new enter password"
        }
        if(passwordText.length < 8)
        {
            return "Minimum 8 Character Password"
        }
        if(!passwordText.matches(".*[0-9].*".toRegex()))
        {
            return "Must Contain 1 Number"
        }
        if(!passwordText.matches(".*[A-Z].*".toRegex()))
        {
            return "Must Contain 1 Upper-case Character"
        }
        if(!passwordText.matches(".*[a-z].*".toRegex()))
        {
            return "Must Contain 1 Lower-case Character"
        }
        if(!passwordText.matches(".*[@#\$%^&+=].*".toRegex()))
        {
            return "Must Contain 1 Special Character (@#\$%^&+=)"
        }

        return null
    }

    private fun cnfpasswordFocusListener()
    {
        binding.etCnfpaswd.setOnFocusChangeListener { _, focused ->
            if(!focused)
            {
                binding.textInputCnfpassword.helperText = validCnfPassword()
            }
        }
    }

    private fun validCnfPassword(): String?
    {
        val cnfpasswordText = binding.etCnfpaswd.text.toString()
        val passwordText = binding.etPaswd.text.toString()
        if(cnfpasswordText.isEmpty())
        {
            return "Please enter confirm password"
        }
        if(!cnfpasswordText.equals(passwordText))
        {
            return "Must match both password"
        }
        return null
    }

}