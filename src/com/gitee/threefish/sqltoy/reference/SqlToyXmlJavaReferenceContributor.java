package com.gitee.threefish.sqltoy.reference;

import com.gitee.threefish.sqltoy.util.SqlToyXmlUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/13
 */
public class SqlToyXmlJavaReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar psiReferenceRegistrar) {
        psiReferenceRegistrar.registerReferenceProvider(PlatformPatterns.psiElement(XmlTag.class), new XmlInjectPsiReferenceProvider());
    }

    class XmlInjectPsiReferenceProvider extends PsiReferenceProvider {

        @NotNull
        @Override
        public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
            if (psiElement.getContainingFile().getName().endsWith(SqlToyXmlUtil.EXT)) {
                XmlTag xmlTag = (XmlTag) psiElement;
                if (xmlTag.getName().equals("sql")) {
                    XmlAttribute xmlAttribute = xmlTag.getAttribute("id");
                    if (Objects.nonNull(xmlAttribute)) {
                        String id = xmlAttribute.getValue();


                        System.out.println(id);
                    }
                }
            }
            return PsiReference.EMPTY_ARRAY;
        }
    }

}
