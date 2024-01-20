import org.jetbrains.compose.desktop.application.dsl.TargetFormat


plugins {
    id("java")
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://repository.aspose.com/repo/")
    google()
}


val coroutinesVersion = "1.6.1"
val log4jVersion = "2.17.0"
val composeVersion = "1.1.1"
val decomposeVersion = "1.0.0"
val kmongoVersion = "4.7.2"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    val jar by getting(Jar::class) {

        manifest {
            attributes["Main-Class"] = "MainKt"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}


sourceSets {

    val main by getting {

        resources.srcDirs("src/main/resources")
        dependencies {
            implementation(compose.desktop.currentOs)
            implementation("com.microsoft.sqlserver:mssql-jdbc:9.4.0.jre11")
            implementation("org.jetbrains.kotlin:kotlin-stdlib")
            implementation("io.insert-koin:koin-core:3.1.5")

            implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.5.10")
            implementation("com.aspose:aspose-cells:21.3")
            implementation(kotlin("reflect"))

            // Compose
            implementation("org.jetbrains.compose.foundation:foundation:$composeVersion")
            implementation("org.jetbrains.compose.material:material:$composeVersion")
            implementation("org.jetbrains.compose.ui:ui:$composeVersion")
            implementation("org.jetbrains.compose.ui:ui-geometry-desktop:$composeVersion")
            implementation("org.jetbrains.compose.ui:ui-graphics-desktop:$composeVersion")
            implementation("org.litote.kmongo:kmongo:$kmongoVersion")
            implementation("org.litote.kmongo:kmongo-coroutine:$kmongoVersion")

            // Coroutine
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

            implementation("commons-codec:commons-codec:1.15")
            implementation("com.auth0:java-jwt:3.18.1")


            implementation("com.auth0:auth0:1.35.0")

            //secure tokens on windows
            implementation("net.java.dev.jna:jna:5.10.0")
            implementation("net.java.dev.jna:jna-platform:5.10.0")

            //Retrofit
            implementation("com.squareup.retrofit2:retrofit:2.9.0")
            implementation("com.squareup.retrofit2:converter-gson:2.9.0")
            implementation("com.squareup.okhttp3:okhttp:4.9.3") // Change this to the latest compatible version

            // JWT
            implementation("io.jsonwebtoken:jjwt-api:0.11.2")
            runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
            runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")
            implementation("com.auth0:java-jwt:3.18.2")
            implementation("com.auth0:jwks-rsa:0.9.0")

            //tentando resolver problema
            implementation("javax.xml.bind:jaxb-api:2.3.1")
            implementation("jakarta.platform:jakarta.jakartaee-api:8.0.0")

            //slf4j
            implementation("org.slf4j:slf4j-api:1.7.32")
            implementation("org.slf4j:slf4j-simple:1.7.32")
            //Gson
            implementation("com.google.code.gson:gson:2.8.9")

            //jose4j
            implementation("org.bitbucket.b_c:jose4j:0.7.7")
            implementation("com.nimbusds:nimbus-jose-jwt:9.16.1")

            //JNA
            implementation("net.java.dev.jna:jna:5.10.0")
            implementation("net.java.dev.jna:jna-platform:5.10.0")

            /*Navigation*/
//                implementation ("com.arkivanov.decompose:decompose:$decomposeVersion")
//                implementation ("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")
        }
    }
    val test by getting
}

tasks.named<Copy>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "composeExcel"
            packageVersion = "1.0.0"
            includeAllModules = true
            //modules("java.sql", "java.naming","jdk.unsupported", "java.management","jdk.naming.dns") // Adicione esta linha
        }
    }
}