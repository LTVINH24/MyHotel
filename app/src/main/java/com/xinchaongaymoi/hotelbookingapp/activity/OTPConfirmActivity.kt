package com.xinchaongaymoi.hotelbookingapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.xinchaongaymoi.hotelbookingapp.R
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class OTPConfirmActivity : AppCompatActivity() {
    private lateinit var edt_otp:EditText
    private lateinit var verify:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_confirm)

        // Get email passed via intent
        val email = intent.getStringExtra("email")

        // Generate OTP and send it
        val otp = generateOTP()

        if (email != null) {
            sendOTPToEmail(email, otp)
        } else {
            Toast.makeText(this, "Email not found!", Toast.LENGTH_SHORT).show()
        }
        edt_otp = findViewById(R.id.otp)
        verify = findViewById(R.id.verifyBtn)
        verify.setOnClickListener {
            val enteredOTP = edt_otp.text.toString()
            if (enteredOTP == otp) {
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Generate a random OTP
    private fun generateOTP(): String {
        val random = Random()
        return (100000..999999).random().toString() // 6-digit OTP
    }
    private fun createEmailSession(): Session {
        val emailProperties = Properties()
        emailProperties["mail.smtp.host"] = "smtp.gmail.com"
        emailProperties["mail.smtp.socketFactory.port"] = "465"
        emailProperties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        emailProperties["mail.smtp.auth"] = "true"
        emailProperties["mail.smtp.port"] = "465"

        return Session.getInstance(emailProperties, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                return javax.mail.PasswordAuthentication("izunasorakami@gmail.com", "lgml sscx njej vqgk")
            }
        })
    }
    // Send OTP to the provided email address
    private fun sendOTPToEmail(email: String, otp: String) {
        val session = createEmailSession()
        val message = MimeMessage(session)
        try {
            message.setFrom(InternetAddress("your_email@gmail.com"))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
            message.subject = "Your OTP Code"
            message.setText("Your OTP code is: $otp")
            Log.e("OTP Verify","Success")
            Thread {
                try {
                    Transport.send(message)
                    runOnUiThread {
                        Toast.makeText(this, "OTP sent to your email!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        } catch (e: Exception) {
            Toast.makeText(this, "Error sending OTP", Toast.LENGTH_SHORT).show()
        }
    }
}
