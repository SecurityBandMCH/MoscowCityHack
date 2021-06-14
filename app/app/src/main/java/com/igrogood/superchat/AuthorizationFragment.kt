package com.igrogood.superchat

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.igrogood.superchat.utilits.*
import java.util.concurrent.TimeUnit


class AuthorizationFragment: Fragment(R.layout.fragment_authorization) {
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val TAG = "myLog"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var phone = view.findViewById<EditText>(R.id.phoneEditText)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
                if (e is FirebaseAuthInvalidCredentialsException) {

                } else if (e is FirebaseTooManyRequestsException) {

                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                val dialogEnterCode = AlertDialog.Builder(context)
                dialogEnterCode.setTitle("Введите полученый смс с кодом подтверждения")
                val codeEditText = EditText(context)
                codeEditText.inputType = InputType.TYPE_CLASS_NUMBER
                dialogEnterCode.setView(codeEditText)
                dialogEnterCode.setPositiveButton("Войти") { dialogInterface: DialogInterface, i: Int ->
                    val code = codeEditText.text.toString()
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
                    AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val uid = AUTH.currentUser?.uid.toString()
                            val dateMap = mutableMapOf<String, Any>()
                            dateMap[CHILD_ID] = uid
                            dateMap[CHILD_PHONE] = phone.text.toString()
                            dateMap[CHILD_USERNAME] = phone.text.toString()
                            REF_DATABASE_ROOT.child(NODE_USERS).child(phone.text.toString()).updateChildren(dateMap)
                                .addOnCompleteListener { task2 ->
                                    if(task2.isSuccessful) {
                                        (parentFragment as ILoadForm).chatListForm()
                                        showToast("Добро пожаловать")
                                    } else
                                        showToast(task2.exception?.message.toString())
                                }
                        } else
                            showToast("Неверный код")
                    }

                }
                dialogEnterCode.show()
            }
        }

        view.findViewById<Button>(R.id.loginBtn).setOnClickListener {
            if(phone.text.isEmpty()) {
                showToast("Введите номер телефона")
                return@setOnClickListener
            }

            val options = activity?.let {
                PhoneAuthOptions.newBuilder(AUTH)
                    .setPhoneNumber(phone.text.toString())
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(it)
                    .setCallbacks(callbacks)
                    .build()
            }
            if (options != null) {
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        activity?.let {
            AUTH.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        //val user = task.result?.user
                        (parentFragment as ILoadForm).chatListForm()
                        showToast("Вход выполнен")
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
        }
    }
}