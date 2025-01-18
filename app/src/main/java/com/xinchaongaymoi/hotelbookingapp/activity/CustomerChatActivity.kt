package com.xinchaongaymoi.hotelbookingapp.activity

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.applozic.mobicomkit.api.account.register.RegistrationResponse
import com.applozic.mobicomkit.uiwidgets.kommunicate.activities.LeadCollectionActivity.EMAIL_VALIDATION_REGEX
import com.applozic.mobicomkit.uiwidgets.kommunicate.activities.LeadCollectionActivity.PHONE_NUMBER_VALIDATION_REGEX
import com.applozic.mobicommons.commons.core.utils.Utils
import com.applozic.mobicommons.json.GsonUtils
import com.xinchaongaymoi.hotelbookingapp.R
import io.kommunicate.KmConversationBuilder
import io.kommunicate.Kommunicate
import io.kommunicate.callbacks.KMLoginHandler
import io.kommunicate.callbacks.KmCallback
import io.kommunicate.callbacks.KmPrechatCallback
import io.kommunicate.models.KmPrechatInputModel
import io.kommunicate.users.KMUser
import io.kommunicate.utils.KmConstants

class CustomerChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Kommunicate.init(applicationContext, "1bdf42819c9342166e4ba9199668e14b8");
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Kommunicate.loginAsVisitor(this, object : KMLoginHandler {
            override fun onSuccess(registrationResponse: RegistrationResponse, context: Context) {
                // You can perform operations such as opening the conversation, creating a new conversation or update user details on success
            }


            override fun onFailure(
                registrationResponse: RegistrationResponse,
                exception: Exception
            ) {
                // You can perform actions such as repeating the login call or throw an error message on failure
            }
        })
        val progressDialog = ProgressDialog(this).apply {
            setTitle("Logging in..")
            setMessage("Please wait...")
            setCancelable(false)
            show()
        }
        val inputModelList: MutableList<KmPrechatInputModel> = mutableListOf()

        val emailField = KmPrechatInputModel().apply {
            this.type = KmPrechatInputModel.KmInputType.EMAIL
            this.isRequired = true
            this.placeholder = "Enter email"
            this.validationRegex = EMAIL_VALIDATION_REGEX //create static value for email regex
            this.field = "Email" //This will be returned as key
            this.compositeRequiredField = "Phone" //optional: "Either Phone or Email is required" if you set another field as composite field
        }

        val nameField = KmPrechatInputModel().apply {
            this.type = KmPrechatInputModel.KmInputType.TEXT
            this.placeholder = "Enter Name"
            this.field = "Name"
        }

        val contactField = KmPrechatInputModel().apply {
            this.type = KmPrechatInputModel.KmInputType.NUMBER
            this.placeholder = "Enter Phone number"
            this.field = "Phone"
            this.validationRegex = PHONE_NUMBER_VALIDATION_REGEX
        }

        val dropdownField = KmPrechatInputModel().apply {
            this.options = mutableListOf("Male", "Female") //list of options to show
            this.placeholder = "Enter your gender"
            this.field = "Gender"
            this.element = "select" //element must be "select" for dropdown menu
        }

        inputModelList.add(emailField)
        inputModelList.add(nameField)
        inputModelList.add(contactField)
        inputModelList.add(dropdownField)


        Kommunicate.launchPrechatWithResult(
            this,
            inputModelList,
            object : KmPrechatCallback<Map<String, String>> {
                override fun onReceive(
                    data: Map<String, String>,
                    context: Context,
                    finishActivityReceiver: ResultReceiver
                ) {
                    Utils.printLog(
                        context, "TestPrechat", GsonUtils.getJsonFromObject(
                            data,
                            MutableMap::class.java
                        )
                    )


                    val user = KMUser().apply {
                        if (!data["Email"].isNullOrEmpty()) {
                            userId = data["Email"]
                            email = data["Email"]
                        }
                        if (!data["Phone"].isNullOrEmpty()) {
                            contactNumber = data["Phone"]
                        }
                        if (!data["Name"].isNullOrEmpty()) {
                            displayName = data["Name"]
                        }
                        if (!data["Gender"].isNullOrEmpty()) {
                            metadata = mapOf(
                                "Gender" to data["Gender"]
                            )
                        }
                    }


                    KmConversationBuilder(context).apply {
                        kmUser = user
                        launchConversation(object : KmCallback {
                            override fun onSuccess(message: Any) {
                                finishActivityReceiver.send(
                                    KmConstants.PRECHAT_RESULT_CODE,
                                    null
                                ) //To finish the Prechat activity
                                Log.d("Conversation", "Success : $message")
                            }


                            override fun onFailure(error: Any) {
                                finishActivityReceiver.send(
                                    1000,
                                    null
                                ) //To dismiss the loading progress bar
                                Log.d("Conversation", "Failure : $error")
                            }
                        })
                    }
                }

                override fun onError(error: String) {
                    Utils.printLog(
                        applicationContext, "TestPrechat",
                        "Error : $error"
                    )
                    Log.d("Kommunicate", "Error: $error")
                }
            })
    }
}