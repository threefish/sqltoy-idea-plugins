package com.gitee.threefish.sqltoy.reference;

import com.gitee.threefish.sqltoy.util.SearchUtil;
import com.gitee.threefish.sqltoy.util.SqlToyXmlUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/12
 */
public class SqlToyJava2XmlReferenceContributor extends PsiReferenceContributor {
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
                PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
                PsiField[] fields = psiClass.getFields();
                List<PsiField> extendsClassFields = SearchUtil.getExtendsClassFields(psiClass);
                extendsClassFields.addAll(Arrays.asList(fields));
                List<String> fieldStrings = new ArrayList<>();
                for (PsiField field : extendsClassFields) {
                    if (SqlToyXmlUtil.isSqlToyLazyDao(field)) {
                        fieldStrings.add(field.getName());
                    }
                }
                boolean a = SqlToyXmlUtil.isInjectXml(literalExpression, fieldStrings);
                boolean b = SqlToyXmlUtil.isNewQueryExecutor(literalExpression,fieldStrings);
                if (a || b) {
                    Project project = element.getProject();
                    final Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, SqlToyXmlUtil.EXT, SearchUtil.getSearchScope(project, element));
                    final List<PsiElement> elements = SqlToyXmlUtil.findXmlPsiElement(project, virtualFiles, value);
                    return elements.stream().map(psiElement -> new PsiJavaInjectReference(element, psiElement)).toArray(PsiReference[]::new);
                }
            }
            return PsiReference.EMPTY_ARRAY;
        }


    }

}
