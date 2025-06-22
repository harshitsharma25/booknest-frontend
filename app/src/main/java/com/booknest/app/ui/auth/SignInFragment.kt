package com.booknest.app.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.booknest.app.R
import com.booknest.app.databinding.FragmentSigninBinding
import com.booknest.app.ui.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : BaseFragment<FragmentSigninBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSigninBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnSignIn.setOnClickListener {
            signInUser()
        }
        binding.tvSignUpLink.setOnClickListener {
            gotoSignUp()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun signInUser(){
        val email : String = binding.etEmailSignin.text.toString().trim{ it <= ' '}
        val password : String = binding.etPasswordSignin.text.toString().trim{ it <= ' '}
        val progressBar : ProgressBar = binding.pbSeries

        if(validForm(email,password)){
            showProgressBar(progressBar)

            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    hideProgressBar(progressBar)
                    if(task.isSuccessful){
                        findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                    } else{
                        Toast.makeText(
                                requireContext(),
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,).show()
                    }
                }.addOnFailureListener { exception ->
                    hideProgressBar(progressBar)
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun validForm(email : String, password : String) : Boolean {
        return when{
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

    private fun gotoSignUp(){
        findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
    }
}