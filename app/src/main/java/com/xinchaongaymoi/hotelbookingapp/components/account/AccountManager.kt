package com.xinchaongaymoi.hotelbookingapp.components.account

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xinchaongaymoi.hotelbookingapp.model.UserAccount

object AccountManager {
    private const val ACCOUNTS_KEY = "accounts"
    private const val LAST_USED_ACCOUNT_KEY = "lastUsedAccount"

    fun saveAccounts(context: Context, accounts: List<UserAccount>) {
        val sharedPreferences = context.getSharedPreferences("UserAccounts", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        editor.putString(ACCOUNTS_KEY, gson.toJson(accounts)) // Save a list of UserAccount objects
        editor.apply()
        Log.e("user saved", "Accounts saved to UserAccounts")
    }

    fun getAccounts(context: Context): List<UserAccount> {
        val sharedPreferences = context.getSharedPreferences("UserAccounts", Context.MODE_PRIVATE)
        val accountsJson = sharedPreferences.getString(ACCOUNTS_KEY, "[]")
        val gson = Gson()
        val type = object : TypeToken<List<UserAccount>>() {}.type
        return gson.fromJson(accountsJson, type)
    }

    fun setLastUsedAccount(context: Context, userId: String) {
        val sharedPreferences = context.getSharedPreferences("UserAccounts", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(LAST_USED_ACCOUNT_KEY, userId).apply()
    }

    fun getLastUsedAccount(context: Context): UserAccount? {
        val userId = context.getSharedPreferences("UserAccounts", Context.MODE_PRIVATE)
            .getString(LAST_USED_ACCOUNT_KEY, null)
        return getAccounts(context).find { it.userId == userId }
    }
}
