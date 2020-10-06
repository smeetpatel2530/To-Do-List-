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

class MainActivity : AppCompatActivity() {
    private var email: EditText? = null
    private var password: EditText? = null
    private var btnSingIn: Button? = null
    private var btnSignUp: Button? = null
    private var mAuth: FirebaseAuth? = null
    private var mDialog: ProgressDialog? = null

    // for directly log in for user once sign up
    override fun onStart() {
        super.onStart()
        if (mAuth!!.currentUser != null) {
            finish()
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mDialog = ProgressDialog(this)
        email = findViewById(R.id.email_login)
        password = findViewById(R.id.password_login)
        btnSingIn = findViewById(R.id.btnsignin)
        btnSignUp = findViewById(R.id.btnsignup)
        btnSingIn.setOnClickListener(View.OnClickListener {
            val mEmail = email.getText().toString().trim { it <= ' ' }
            val mPass = password.getText().toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(mEmail)) {
                email.setError("Required Field..")
                return@OnClickListener
            }
            if (TextUtils.isEmpty(mPass)) {
                password.setError("Required Field..")
                return@OnClickListener
            }
            mDialog!!.setMessage("Processing..")
            mDialog!!.show()
            mAuth!!.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mDialog!!.dismiss()
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    Toast.makeText(applicationContext, "Login  Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_SHORT).show()
                    mDialog!!.dismiss()
                }
            }
        })


        //for sign up page of the login creditentials
        btnSignUp.setOnClickListener(View.OnClickListener { startActivity(Intent(applicationContext, RegistrationActivity::class.java)) })
    }
}