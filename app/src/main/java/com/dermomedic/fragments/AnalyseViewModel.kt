package com.dermomedic.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.dermomedic.database.AnalyseData
import com.dermomedic.database.AnalyseDatabaseDao
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import dermomedic.ai.service.ktor.AnalyseJson
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.streams.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Thread.sleep


class AnalyseViewModel(val database: AnalyseDatabaseDao,
                       application: Application) : AndroidViewModel(application) {

    private val _malignantEvaluation = MutableLiveData<String>()
    val malignantEvaluation: LiveData<String>
        get() = _malignantEvaluation

    private var _currentFileName: String? = null

    init {
        _malignantEvaluation.value = ""
    }


    fun clearAnalyse() {
        _currentFileName = null
        _malignantEvaluation.postValue("")
    }

    fun updateCurrentAnalyse(imageName: String) {
        _currentFileName = imageName
        updateAnalyseView(imageName)
    }

    private fun updateAnalyseView(imageName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val analyseData = database.findByName(imageName)
            if (_currentFileName.equals(imageName)) {
                if (analyseData?.malingantEvaluation != null) {
                    _malignantEvaluation.postValue(analyseData.malingantEvaluation.toString())
                } else {
                    _malignantEvaluation.postValue("")
                }
            }
        }
    }

    fun onAnalyse(imageFile: File) {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
        val baseUrl = defaultSharedPreferences.getString("base_url_preference", "http://192.168.1.106:8080")

        val headersBuilder = HeadersBuilder()
        headersBuilder[HttpHeaders.ContentType] = ContentType.Image.JPEG.toString()
        headersBuilder[HttpHeaders.ContentDisposition] = "filename=${imageFile.name}"

        val formData = formData {
            appendInput("image",
                    headersBuilder.build()
            ) { imageFile.inputStream().asInput() }
        }

        val client = HttpClient()
        GlobalScope.launch(Dispatchers.IO) {
            val json: String = client.submitFormWithBinaryData("${baseUrl}/upload", formData)
            val gson = GsonBuilder()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                    .create()
            val uploadAnalyse = gson.fromJson(json, AnalyseJson::class.java)

            var analyseEnded = false
            var analyse: AnalyseJson? = null
            var nbRequest = 0
            while (!analyseEnded) {
                sleep(300)
                println("requesting ${baseUrl}/analyse/json/${uploadAnalyse.idx}")
                analyse = gson.fromJson(client.get<String>("${baseUrl}/analyse/json/${uploadAnalyse.idx}"), AnalyseJson::class.java)
                println("nbRequest $nbRequest: $analyse analysed:${analyse.analysed()}")
                analyseEnded = analyse.analysed()
                nbRequest++
                println(analyse.malingantEvaluation)
            }
            analyse?.let {
                val analysedFileName = "${analyse.fileName}.${analyse.extension}"
                upsertAnalyseData(analysedFileName, analyse)
                updateAnalyseView(analysedFileName)
            }
        }
    }

    private suspend fun upsertAnalyseData(analysedFileName: String, analyse: AnalyseJson) {
        val analyseData = database.findByName(analysedFileName)
        if (analyseData != null) {
            analyseData.malingantEvaluation = analyse.malingantEvaluation
            database.update(analyseData)
        } else {
            database.insert(AnalyseData(fileName = analysedFileName, malingantEvaluation = analyse.malingantEvaluation))
        }
    }

}
