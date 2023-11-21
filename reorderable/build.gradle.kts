plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
}

group = "sh.calvin.reorderable"
version = "1.0.0"

android {
    namespace = project.group.toString()
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")

    testImplementation("junit:junit:4.13.2")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group.toString()
            artifactId = "reorderable"
            version = project.version.toString()
            afterEvaluate {
                from(components["release"])
            }

            pom {
                name = "Reorderable"
                description = "A library for reordering items in a LazyColumn"
                url = "https://github.com/Calvin-LL/Reorderable"

                licenses {
                    license {
                        name = "The Apache Software License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                        distribution = "repo"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/Calvin-LL/Reorderable.git"
                    developerConnection = "scm:git:ssh://github.com/Calvin-LL/Reorderable.git"
                    url = "https://github.com/Calvin-LL/Reorderable"
                }
                developers {
                    developer {
                        name = "Calvin Liang"
                        email = "me@calvin.sh"
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.properties["ossrhUsername"].toString()
                password = project.properties["ossrhPassword"].toString()
            }
        }
    }
}

signing {
    sign(publishing.publications)
}