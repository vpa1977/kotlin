/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.report

import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.gradle.tooling.events.task.TaskFinishEvent
import org.jetbrains.kotlin.build.report.metrics.getAllMetrics
import org.jetbrains.kotlin.build.report.statistics.*
import org.jetbrains.kotlin.build.report.statistics.file.ReadableFileReportData
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.statistics.GradleFileReportService
import org.jetbrains.kotlin.gradle.report.data.BuildExecutionData
import org.jetbrains.kotlin.gradle.report.data.BuildOperationRecord
import org.jetbrains.kotlin.gradle.utils.nextKotlinLanguageVersion
import java.io.File
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit

//Because of https://github.com/gradle/gradle/issues/23359 gradle issue, two build services interaction is not reliable at the end of the build
//Switch back to proper BuildService as soon as this issue is fixed
class BuildReportsService {

    private val log = Logging.getLogger(this.javaClass)
    private val loggerAdapter = GradleLoggerAdapter(log)

    private val startTime = System.nanoTime()
    internal val buildUuid = UUID.randomUUID().toString()

    private val httpReportService = HttpReportService()

    private val tags = LinkedHashSet<StatTag>()

    init {
        log.info("Build report service is registered. Unique build id: $buildUuid")
    }

    fun close(
        buildOperationRecords: Collection<BuildOperationRecord>,
        failureMessages: List<String>,
        parameters: BuildReportParameters,
    ) {
        val buildData = BuildExecutionData(
            metrics = getAllMetrics(),
            startParameters = parameters.startParameters,
            failureMessages = failureMessages,
            buildOperationRecord = buildOperationRecords.sortedBy { it.startTimeMs }
        )

        val reportingSettings = parameters.reportingSettings

        parameters.httpReportParameters?.also {
            httpReportService.sendData(it, loggerAdapter) {
                reportBuildFinish(parameters)
            }
        }
        reportingSettings.fileReportSettings?.also {
            GradleFileReportService(
                it.buildReportDir,
                parameters.projectName,
                it.includeMetricsInReport,
            ).process(
                ReadableFileReportData(
                    transformOperationRecordsToCompileStatisticsData(buildOperationRecords, parameters, onlyKotlinTask = false),
                    parameters.startParameters,
                    failureMessages.filter { it.isNotEmpty() },
                ),
                loggerAdapter
            )
        }

        reportingSettings.singleOutputFile?.also { singleOutputFile ->
            MetricsWriter(singleOutputFile.absoluteFile).process(buildData, log)
        }

        if (reportingSettings.experimentalTryNextConsoleOutput) {
            reportTryNextToConsole(buildData)
        }

        reportingSettings.jsonOutputDir?.also {
            JsonReportService(it, parameters.projectName).process(buildData, loggerAdapter)
        }

        //It's expected that bad internet connection can cause a significant delay for big project
        parameters.httpReportParameters?.also {
            httpReportService.close(it, loggerAdapter)
        }
    }

    private fun transformOperationRecordsToCompileStatisticsData(
        buildOperationRecords: Collection<BuildOperationRecord>,
        parameters: BuildReportParameters,
        onlyKotlinTask: Boolean,
        metricsToShow: Set<String>? = null,
    ) = buildOperationRecords.mapNotNull {
        prepareData(
            taskResult = null,
            it.path,
            it.startTimeMs,
            it.totalTimeMs + it.startTimeMs,
            parameters.projectName,
            buildUuid,
            parameters.label,
            parameters.kotlinVersion,
            it,
            onlyKotlinTask = onlyKotlinTask,
            parameters.additionalTags,
            metricsToShow = metricsToShow
        )
    }

    fun onFinish(
        event: TaskFinishEvent, buildOperation: BuildOperationRecord,
        parameters: BuildReportParameters,
    ) {
        addHttpReport(event, buildOperation, parameters)
    }

    private fun reportBuildFinish(parameters: BuildReportParameters): BuildFinishStatisticsData? {
        val httpReportSettings = parameters.reportingSettings.httpReportSettings
            ?: return log.debug("Unable to send build finish event, httpReportSettings is null ").let { null }

        val branchName = if (httpReportSettings.includeGitBranchName) {
            val process = ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")
                .directory(parameters.projectDir)
                .start().also {
                    it.waitFor(5, TimeUnit.SECONDS)
                }
            process.inputStream.reader().readText()
        } else "is not set"

        return BuildFinishStatisticsData(
            projectName = parameters.projectName,
            startParameters = parameters.startParameters
                .includeVerboseEnvironment(parameters.reportingSettings.httpReportSettings.verboseEnvironment),
            buildUuid = buildUuid,
            label = parameters.label,
            totalTime = TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)),
            finishTime = System.currentTimeMillis(),
            hostName = hostName,
            tags = tags,
            gitBranch = branchName
        )
    }

    private fun BuildStartParameters.includeVerboseEnvironment(verboseEnvironment: Boolean): BuildStartParameters {
        return if (verboseEnvironment) {
            this
        } else {
            BuildStartParameters(
                tasks = this.tasks,
                excludedTasks = this.excludedTasks,
                currentDir = null,
                projectProperties = emptyList(),
                systemProperties = emptyList()
            )
        }
    }

    private fun addHttpReport(
        event: TaskFinishEvent,
        buildOperationRecord: BuildOperationRecord,
        parameters: BuildReportParameters,
    ) {
        parameters.httpReportParameters?.also { httpService ->
            httpReportService.sendData(httpService, loggerAdapter) {
                prepareData(
                    event,
                    parameters.projectName,
                    buildUuid,
                    parameters.label,
                    parameters.kotlinVersion,
                    buildOperationRecord,
                    onlyKotlinTask = true,
                    parameters.additionalTags
                )
            }

        }

    }

    private fun reportTryNextToConsole(
        data: BuildExecutionData,
    ) {
        val tasksData = data.buildOperationRecord
            .filterIsInstance<TaskRecord>()
            .filter {
                // Filtering by only KGP tasks and by those that actually do compilation
                it.isFromKotlinPlugin && it.kotlinLanguageVersion != null
            }
        log.warn("##### 'kotlin.experimental.tryNext' results #####")
        if (tasksData.isEmpty()) {
            log.warn("No Kotlin compilation tasks have been run")
            log.warn("#####")
        } else {
            val tasksCountWithKotlinNext = tasksData.count {
                it.kotlinLanguageVersion != null && it.kotlinLanguageVersion >= KotlinVersion.nextKotlinLanguageVersion
            }
            val taskWithNextPercent = (tasksCountWithKotlinNext * 100) / tasksData.count()
            val statsData = tasksData.map { it.path to it.kotlinLanguageVersion?.version }
            statsData.sortedBy { it.first }.forEach { record ->
                log.warn("${record.first}: ${record.second} language version")
            }
            log.warn(
                "##### $taskWithNextPercent% ($tasksCountWithKotlinNext/${tasksData.count()}) tasks have been compiled with " +
                        "Kotlin ${KotlinVersion.nextKotlinLanguageVersion.version} #####"
            )
        }
    }

    companion object {

        fun getStartParameters(project: Project) = project.gradle.startParameter.let {
            BuildStartParameters(
                tasks = it.taskRequests.flatMap { it.args },
                excludedTasks = it.excludedTaskNames,
                currentDir = it.currentDir.path,
                projectProperties = it.projectProperties.map { (key, value) -> "$key: $value" },
                systemProperties = it.systemPropertiesArgs.map { (key, value) -> "$key: $value" },
            )
        }

        val hostName: String? = try {
            InetAddress.getLocalHost().hostName
        } catch (_: Exception) {
            //do nothing
            null
        }
    }

}

enum class TaskExecutionState {
    SKIPPED,
    FAILED,
    UNKNOWN,
    SUCCESS,
    FROM_CACHE,
    UP_TO_DATE
    ;
}

data class BuildReportParameters(
    val startParameters: BuildStartParameters,
    val reportingSettings: ReportingSettings,
    val httpReportParameters: HttpReportParameters?,

    val projectDir: File,
    val label: String?,
    val projectName: String,
    val kotlinVersion: String,
    val additionalTags: Set<StatTag>,
)