package com.smeet.todolist

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {
    private var email: EditText? = null
    private var pass: EditText? = null
    private var btnSignup: Button? = null
    private var btnSignIn: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var mDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        mAuth = FirebaseAuth.getInstance()
        mDialog = ProgressDialog(this)
        email = findViewById(R.id.email_registration)
        pass = findViewById(R.id.password_registration)
        btnSignIn = findViewById(R.id.btnsignin_reg)
        btnSignup = findViewById(R.id.btnsignup_reg)
        //for login
        btnSignIn.setOnClickListener(View.OnClickListener { startActivity(Intent(applicationContext, MainActivity::class.java)) })
        //for sign up
        btnSignup.setOnClickListener(View.OnClickListener {
            val mEmail = email.getText().toString().trim { it <= ' ' }
            val mPass = pass.getText().toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(mEmail)) {
                email.setError("Required Field...")
                return@OnClickListener
            }
            if (TextUtils.isEmpty(mPass)) {
                pass.setError("Required Field...")
                return@OnClickListener
            }
            mDialog!!.setMessage("Processing..")
            mDialog!!.show()
            mAuth!!.createUserWithEmailAndPassword(mEmail, mPass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mDialog!!.dismiss()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    Toast.makeText(applicationContext, "Registration Completed Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Registration  Failed", Toast.LENGTH_SHORT).show()
                    mDialog!!.dismiss()
                }
            }
        })
    }
}