package org.example.mlanalis.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.opDataStore by preferencesDataStore(name = "op_data_store")
val Context.metaDataStore by preferencesDataStore(name = "meta_data_store")
val Context.underDataStore by preferencesDataStore(name = "under_data_store")


object DataStoreManager {
    private val OP_KEY = stringPreferencesKey("op_data")
    private val META_KEY = stringPreferencesKey("meta_data")
    private val UNDER_KEY = stringPreferencesKey("under_data")

    suspend fun saveOP(context: Context, jsonData: String) {
        context.opDataStore.edit { prefs -> prefs[OP_KEY] = jsonData }
    }

    suspend fun getOP(context: Context): String? {
        return context.opDataStore.data.map { it[OP_KEY] }.first()
    }

    suspend fun clearOP(context: Context) {
        context.opDataStore.edit { it.remove(OP_KEY) }
    }

    suspend fun saveMeta(context: Context, jsonData: String) {
        context.metaDataStore.edit { prefs -> prefs[META_KEY] = jsonData }
    }

    suspend fun getMeta(context: Context): String? {
        return context.metaDataStore.data.map { it[META_KEY] }.first()
    }

    suspend fun clearMeta(context: Context) {
        context.metaDataStore.edit { it.remove(META_KEY) }
    }

    suspend fun saveUnder(context: Context, jsonData: String) {
        context.underDataStore.edit { prefs -> prefs[UNDER_KEY] = jsonData }
    }

    suspend fun getUnder(context: Context): String? {
        return context.underDataStore.data.map { it[UNDER_KEY] }.first()
    }

    suspend fun clearUnder(context: Context) {
        context.underDataStore.edit { it.remove(UNDER_KEY) }
    }
}



