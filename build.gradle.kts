plugins {
    application
    `embedded-kotlin`
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("GeneratorKt")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(embeddedKotlin("compiler"))
    implementation("com.squareup:kotlinpoet:1.14.2")
    implementation("me.omico.elucidator:elucidator:0.9.0")
}
