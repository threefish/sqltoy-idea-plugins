package com.gitee.threefish.sqltoy.reference;

import com.gitee.threefish.sqltoy.util.SearchScopeUtil;
import com.gitee.threefish.sqltoy.util.SqlToyXmlUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/12
 */
public class SqlToyJavaXmlReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class), new JavaInjectPsiReferenceProvider());
    }

    class JavaInjectPsiReferenceProvider extends PsiReferenceProvider {


        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext processingContext) {
            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
            Object literalExpressionValue = literalExpression.getValue();
            String value = literalExpressionValue instanceof String ? (String) literalExpressionValue : null;
            if (Objects.nonNull(value) && !value.contains(" ")) {
                PsiField[] fields = PsiTreeUtil.getParentOfType(element, PsiClass.class).getFields();
                List<String> fieldStrings = new ArrayList<>();
                for (PsiField field : fields) {
                    if (SqlToyXmlUtil.isSqlToyLazyDao(field)) {
                        fieldStrings.add(field.getName());
                    }
                }
                if (SqlToyXmlUtil.isInjectXml(literalExpression, fieldStrings)) {
                    Project project = element.getProject();
                    final Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, SqlToyXmlUtil.EXT, SearchScopeUtil.getSearchScope(project, element));
                    final List<PsiElement> elements = SqlToyXmlUtil.findXmlPsiElement(project, virtualFiles, value);
                    return elements.stream().map(psiElement -> new PsiJavaInjectReference(element, psiElement)).toArray(PsiReference[]::new);
                }
            }
            return PsiReference.EMPTY_ARRAY;
        }


    }
}
