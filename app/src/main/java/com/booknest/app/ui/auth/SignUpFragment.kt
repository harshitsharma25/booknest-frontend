package com.booknest.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.booknest.app.R
import com.booknest.app.databinding.FragmentSignUpBinding
import com.booknest.app.databinding.FragmentSigninBinding
import com.booknest.app.ui.base.BaseFragment
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment :  BaseFragment<FragmentSignUpBinding>() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnSignUp.setOnClickListener {
            registerUser()
        }

        binding.tvSignInLink.setOnClickListener {
            gotoSignIn()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun registerUser(){
        val name : String = binding.etName.text.toString().trim{ it <= ' '}
        val email : String = binding.etEmail.text.toString().trim{ it <= ' '}
        val password : String = binding.etPassword.text.toString().trim{ it <= ' '}
        val progressBar : ProgressBar = binding.pbSeries

        if(validateForm(name,email,password)){
            showProgressBar(progressBar)
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    hideProgressBar(progressBar)
                    if(task.isSuccessful){
                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                    } else{
                        Toast.makeText(
                            requireContext(),
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,).show()
                    }
                }.addOnFailureListener {
                    hideProgressBar(progressBar)
                }
        }

    }

    private fun gotoSignIn(){
        findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
    }

    private fun validateForm(name:String, email: String, password: String) : Boolean {
        return when{
            name.isEmpty() -> {
                showErrorSnackBar("Please Enter Name")
                false
            }
            email.isEmpty() -> {
                showErrorSnackBar("Please Enter Email")
                false
            }
            password.isEmpty() -> {
                showErrorSnackBar("Please Enter Password")
                false
            }
            else -> {
                true
            }
        }
    }
}