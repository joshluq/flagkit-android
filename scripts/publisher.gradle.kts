import java.util.Properties
import java.net.URI

apply(plugin = "maven-publish")

fun getNexusUrl(): URI {
    val envUrl = System.getenv("NEXUS_URL")
    return if (!envUrl.isNullOrEmpty()) {
        println("Publisher: Usando URL de entorno (CI): $envUrl")
        URI.create(envUrl)
    } else {
        println("Publisher: Usando URL local por defecto")
        URI.create("http://localhost:8081/repository/android-releases/")
    }
}

fun getLocalProperty(key: String, project: Project): String? {
    val propertiesFile = project.rootProject.file("local.properties")
    if (propertiesFile.exists()) {
        val properties = Properties()
        properties.load(propertiesFile.inputStream())
        return properties.getProperty(key)
    }
    return null
}

fun getNexusUser(project: Project): String {
    return System.getenv("NEXUS_USER")
        ?: getLocalProperty("nexusUser", project)
        ?: "admin"
}

fun getNexusPassword(project: Project): String? {
    return System.getenv("NEXUS_PASSWORD")
        ?: getLocalProperty("nexusPassword", project)
}

afterEvaluate {
    configure<PublishingExtension> {

        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "es.joshluq.flagkit"
                artifactId = project.name

                version = project.version.toString()

                pom {
                    name.set(project.name)
                    description.set("Librería de Android publicada desde GitHub Actions")
                }
            }
        }

        repositories {
            maven {
                name = "NexusOnPremise"
                url = getNexusUrl()
                isAllowInsecureProtocol = true

                credentials {
                    username = getNexusUser(project)
                    password = getNexusPassword(project)
                }
            }
        }
    }
}
