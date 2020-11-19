package dermomedic.ai.service.ktor

import kotlinx.serialization.Serializable

@Serializable
data class AnalyseJson(val idx: Long, val extension: String, val fileName: String, var malingantEvaluation: Float? = null) {

    fun analysed(): Boolean {
        return malingantEvaluation != null
    }

}
