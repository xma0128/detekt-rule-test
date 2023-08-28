package org.example.detekt

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.hasCommentInside
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

class TestRule(config: Config) : Rule(config) {
    private val targetClassName = "DynamicValueController"

    override val issue = Issue(
        javaClass.simpleName,
        Severity.CodeSmell,
        "Custom Test Rule",
        Debt.FIVE_MINS,
    )

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (klass.name == targetClassName) {
            klass.acceptChildren(object : KtTreeVisitorVoid() {
                override fun visitProperty(property: KtProperty) {
                    if (property.isPropertyOfObjectDeclaration() && property.hasCommentInside()) {
                        val comment = property.getCommentInside()
                        comment ?.let {
                            if (!JIRA_TICKET_URL_PATTERN.containsMatchIn(it.text)) {
                                report(CodeSmell(issue, Entity.atName(property), "Custom message"))
                            }
                        }
                    }
                }
            })
        }
    }

    private fun PsiElement.getCommentInside(): PsiComment? {
        val commentKey = Key<PsiComment>("comment")
        this.acceptChildren(object : KtTreeVisitorVoid() {
            override fun visitComment(comment: PsiComment) {
                putUserData(commentKey, comment)
            }
        })
        return getUserData(commentKey)
    }

    private fun KtProperty.isPropertyOfObjectDeclaration(): Boolean =
        this.isMember && this.getNonStrictParentOfType<KtClassOrObject>() is KtObjectDeclaration

    companion object {
        private val JIRA_TICKET_URL_PATTERN = Regex("Cleanup: http://www.jira.com/issues/mxplat-[0-9]+")
    }
}
