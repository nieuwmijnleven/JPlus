plugins {
    id("java")
    id("antlr")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "jplus.plugin.intellij"
version = "0.1-mvp-alpha"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
         bundledPlugin("com.intellij.java")
    }

    implementation(project(":app"))
    antlr("org.antlr:antlr4:4.12.0") { // use ANTLR version 4
        exclude(group="com.ibm.icu", module="icu4j")
    }
    implementation("org.antlr:antlr4-intellij-adaptor:0.1")
    implementation("org.bitbucket.cowwoc:diff-match-patch:1.2")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

tasks.named("generateGrammarSource", AntlrTask::class.java).configure {
    arguments.addAll(listOf("-visitor", "-package", "jplus.plugin.intellij"))
    outputDirectory = file("src/main/antlr/generated-src/jplus/plugin/intellij")
}

sourceSets {
    main {
        java {
            //srcDir("src/main/antlr/generated-src")
            srcDirs("src/main/antlr/generated-src")
        }
    }
}

gradle.taskGraph.whenReady {
    if (hasTask(":intellij-plugin:runIde")) {
        project(":app").tasks.named("generateGrammarSource").configure {
            setEnabled(false)
        }
        project(":app").tasks.named("generateTestGrammarSource").configure {
            setEnabled(false)
        }
        project(":intellij-plugin").tasks.named("generateGrammarSource").configure {
            setEnabled(false)
        }
        project(":intellij-plugin").tasks.named("generateTestGrammarSource").configure {
            setEnabled(false)
        }
    } else if (hasTask(":intellij-plugin:buildPlugin")) {
        project(":app").tasks.named("generateGrammarSource").configure {
            setEnabled(false)
        }
        project(":app").tasks.named("generateTestGrammarSource").configure {
            setEnabled(false)
        }
        project(":intellij-plugin").tasks.named("generateGrammarSource").configure {
            setEnabled(false)
        }
        project(":intellij-plugin").tasks.named("generateTestGrammarSource").configure {
            setEnabled(false)
        }
    }
}
