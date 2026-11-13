/*
 * Copyright 2010-2026 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import org.gradle.api.artifacts.dsl.RepositoryHandler

fun RepositoryHandler.githubTag(ghUser: String, repo: String, revisionPrefix: String = "v", groupAlias: String? = null) {
}

fun RepositoryHandler.githubRelease(ghUser: String, repo: String, revisionPrefix: String = "v", groupAlias: String? = null) {
}

fun RepositoryHandler.githubCommit(ghUser: String, repo: String, groupAlias: String? = null) {
}

fun RepositoryHandler.kotlinDependencies() {
}

fun RepositoryHandler.intellijRepository(intellijSdkVersion: String) {
}

fun RepositoryHandler.intellijDependencies() {
}

fun RepositoryHandler.teamcityRepository() {
}

fun RepositoryHandler.googleAndroidRepository() {
}

fun RepositoryHandler.gradleLibsReleases() {
}

fun RepositoryHandler.gradlePluginPortalRepository() {
}

fun RepositoryHandler.litmuskt() {
}

fun RepositoryHandler.kotlinIdePluginDependencies() {
}

fun RepositoryHandler.mozillaReleases() {
}

fun RepositoryHandler.kotlinFileDependenciesJsc() {
}

fun RepositoryHandler.nodeJs() {
}

fun RepositoryHandler.yarnDistributions() {
}

fun RepositoryHandler.binaryenDistributions() {
}

fun RepositoryHandler.d8Distributions() {
}

fun RepositoryHandler.androidRepository() {
}

fun RepositoryHandler.androidSystemImages() {
}
