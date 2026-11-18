package com.example.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "GoogleAuthHelper"
const val BOOTSTRAPPED_ADMIN_EMAIL = "nihatfahimbd@gmail.com"

data class AuthUserSummary(
  val uid: String = "",
  val displayName: String = "",
  val email: String = "",
  val isEmailVerified: Boolean = false,
  val isAdmin: Boolean = false,
  val photoUrl: String = ""
)

object GoogleAuthHelper {

  fun createFirestore(context: Context): FirebaseFirestore {
    val appCtx = context.applicationContext
    if (FirebaseApp.getApps(appCtx).isEmpty()) {
      FirebaseApp.initializeApp(appCtx)
    }
    val databaseId = appCtx.getString(R.string.firestore_database_id)
    return FirebaseFirestore.getInstance(databaseId)
  }

  fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
      trySend(firebaseAuth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
  }

  fun toSummary(user: FirebaseUser?): AuthUserSummary? {
    if (user == null) return null
    val email = user.email.orEmpty()
    val adminMatch = user.isEmailVerified && email.equals(BOOTSTRAPPED_ADMIN_EMAIL, ignoreCase = true)
    return AuthUserSummary(
      uid = user.uid,
      displayName = user.displayName.orEmpty().ifBlank { email.substringBefore("@").ifBlank { "গুগল ইউজার" } },
      email = email,
      isEmailVerified = user.isEmailVerified,
      isAdmin = adminMatch,
      photoUrl = user.photoUrl?.toString().orEmpty()
    )
  }

  /**
   * Explicit interactive Google Sign-In flow via Jetpack Credential Manager.
   */
  suspend fun signInInteractive(context: Context): Result<FirebaseUser> {
    return try {
      val webClientId = context.getString(R.string.default_web_client_id)
      val credentialManager = CredentialManager.create(context)
      val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId).build()
      val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

      val response = credentialManager.getCredential(
        context = context,
        request = request
      )
      val credential = response.credential
      if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
      ) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val firebaseCred = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        val authResult = Firebase.auth.signInWithCredential(firebaseCred).await()
        val user = authResult.user ?: error("FirebaseAuth returned null user after credential sign-in.")
        Result.success(user)
      } else {
        Result.failure(IllegalStateException("অসমর্থিত Google ক্রেডেনশিয়াল পাওয়া গেছে।"))
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: GetCredentialCancellationException) {
      Result.failure(Exception("সাইন-ইন বাতিল করা হয়েছে।"))
    } catch (e: NoCredentialException) {
      Result.failure(Exception("এই ডিভাইসে কোনো Google অ্যাকাউন্ট লগইন করা নেই।"))
    } catch (e: Exception) {
      Log.w(TAG, "Interactive Google Sign-In failed", e)
      Result.failure(Exception(e.localizedMessage ?: "Google Sign-In ব্যর্থ হয়েছে।"))
    }
  }

  /**
   * Silent / authorized-only credential lookup if user previously authorized the app.
   */
  suspend fun trySilentSignIn(context: Context): Result<FirebaseUser?> {
    return try {
      val existing = Firebase.auth.currentUser
      if (existing != null) return Result.success(existing)
      val webClientId = context.getString(R.string.default_web_client_id)
      val credentialManager = CredentialManager.create(context)
      val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(webClientId)
        .setAutoSelectEnabled(true)
        .build()
      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
      val response = credentialManager.getCredential(context = context, request = request)
      val credential = response.credential
      if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
      ) {
        val tokenCred = GoogleIdTokenCredential.createFrom(credential.data)
        val firebaseCred = GoogleAuthProvider.getCredential(tokenCred.idToken, null)
        val authResult = Firebase.auth.signInWithCredential(firebaseCred).await()
        Result.success(authResult.user)
      } else {
        Result.success(null)
      }
    } catch (e: CancellationException) {
      throw e
    } catch (_: Exception) {
      Result.success(null)
    }
  }

  suspend fun signOut(context: Context) {
    try {
      Firebase.auth.signOut()
      val credentialManager = CredentialManager.create(context)
      credentialManager.clearCredentialState(ClearCredentialStateRequest())
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.w(TAG, "Credential state clear failed", e)
    }
  }
}
