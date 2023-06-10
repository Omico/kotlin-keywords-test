import com.intellij.psi.tree.IElementType
import me.omico.elucidator.addClass
import me.omico.elucidator.addEnum
import me.omico.elucidator.addFunction
import me.omico.elucidator.addProperty
import me.omico.elucidator.initializer
import me.omico.elucidator.ktFile
import me.omico.elucidator.writeTo
import org.jetbrains.kotlin.lexer.KtTokens
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.walk
import kotlin.io.path.writeText

@OptIn(ExperimentalPathApi::class)
public fun main() {
    val keywords = KtTokens.KEYWORDS.types.map(IElementType::toString)
    val softKeywords = KtTokens.SOFT_KEYWORDS.types.map(IElementType::toString)
    val outputDirectory = Path("src/main/kotlin")
    listOf("properties", "functions", "classes", "enums").forEach { type ->
        mapOf(
            "Keywords" to keywords,
            "SoftKeywords" to softKeywords,
        ).forEach { (name, keywords) ->
            ktFile(packageName = "test.$type", fileName = name) {
                keywords.forEach { keyword ->
                    when (type) {
                        "properties" -> addProperty<String>(keyword) { initializer("\"\"") }
                        "functions" -> addFunction(keyword) {}
                        "classes" -> addClass(keyword) {}
                    }
                }
                if (type == "enums") addEnum(name) { keywords.forEach { keyword -> builder.addEnumConstant(keyword) } }
                writeTo(outputDirectory)
            }
        }
    }
    outputDirectory.resolve("test").walk()
        .filter { it.name == "SoftKeywords.kt" }
        .forEach { file ->
            file.readText()
                .replace("`", "")
                .let(file::writeText)
        }
    outputDirectory.resolve("test/enums/SoftKeywords.kt").let { file ->
        file.readText()
            .replace("  constructor,", "  `constructor`,")
            .replace("  init,", "  `init`,")
            .let(file::writeText)
    }
}
