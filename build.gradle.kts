import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding.UNIX

plugins {
    id("com.android.application") version "8.1.0-rc01" apply false
    id("org.jetbrains.kotlin.android") version "1.8.22" apply false
    id("com.google.devtools.ksp") version "1.8.22-1.0.11" apply false
    id("com.google.dagger.hilt.android") version "2.46.1" apply false

    id("com.diffplug.spotless") version "6.19.0"
}

configure<SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        ktlint("0.49.1")
    }

    kotlinGradle {
        target("**/*.kts")
        ktlint("0.49.1")
    }

    format("xml") {
        target("**/*.xml")

        lineEndings = UNIX
        trimTrailingWhitespace()
        endWithNewline()
        indentWithSpaces(2)
    }
}
