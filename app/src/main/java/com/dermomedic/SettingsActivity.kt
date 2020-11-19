package com.dermomedic

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.dermomedic.database.AnalyseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val resetButton = preferenceManager.findPreference<Preference>("databaseResetButton");

            resetButton?.setOnPreferenceClickListener {
                val application = requireNotNull(this.activity).application
                GlobalScope.launch(Dispatchers.IO) {
                    AnalyseDatabase.getInstance(application).analyseDatabaseDao.clear()
                }
                Toast.makeText(activity, "Database Reset!", Toast.LENGTH_SHORT).show() // TODO a faire apres analyseDatabaseDao.clear()
                true
            }
        }
    }



}