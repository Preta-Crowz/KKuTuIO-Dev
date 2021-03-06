/*
 * KKuTu-Web (https://github.com/horyu1234/KKuTu-Web)
 * Copyright (C) 2020. horyu1234(admin@horyu.me)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"

    id("com.github.node-gradle.node") version "2.2.4"
}

group = "me.horyu"
version = "1.0.2"

java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<ProcessResources> {
    dependsOn("appNpmBuild")
}

tasks.register<NpmTask>("appNpmInstall") {
    description = "Installs all dependencies from package.json"
    setWorkingDir(file(project.projectDir))
    setArgs(listOf("install"))
}

tasks.register<NpmTask>("appNpmBuild") {
    dependsOn("appNpmInstall")
    description = "Builds project"
    setWorkingDir(file(project.projectDir))
    setArgs(listOf("run", "build"))
}

sourceSets {
    main {
        resources {
            exclude("kkutu.json", "kkutu.default.json")
            exclude("oauth.json", "oauth.default.json")
            exclude("**/in_game_kkutu_help.js", "**/in_login.js", "**/in_portal.js", "**/oauth-buttons.js")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.neovisionaries:nv-websocket-client:2.10")
    implementation("com.github.scribejava:scribejava-apis:7.1.1")
    implementation("org.postgresql:postgresql:42.2.16")
    implementation("com.googlecode.htmlcompressor:htmlcompressor:1.5.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}
