package com.gitee.threefish.sqltoy.injector;

import com.gitee.threefish.sqltoy.util.SqlToyXmlUtil;
import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/12
 */
public class SqlToyXmlMultiHostInjector implements MultiHostInjector {


    static final String SQL_VALUE_TAG = "value";

    ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar multiHostRegistrar, @NotNull PsiElement psiElement) {
        try {
            String productCode = applicationInfo.getBuild().getProductCode();
            if ("IU".equals(productCode)) {
                //旗舰版才支持该功能
                if (SqlToyXmlUtil.isSqlToyXmlFile(psiElement.getContainingFile())) {
                    if (psiElement instanceof XmlTag) {
                        XmlTag tag = (XmlTag) psiElement;
                        if (SQL_VALUE_TAG.equals(tag.getName())) {
                            registrarInjecting(Language.findLanguageByID("SQL"), multiHostRegistrar, SqlToyXmlUtil.findXmlTexts(psiElement.getChildren()), "", "");
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void registrarInjecting(Language language, MultiHostRegistrar registrar, List<PsiElement> els, String prefix, String suffix) {
        if (els.size() > 0) {
            registrar.startInjecting(language);
            els.forEach(el -> registrar.addPlace(prefix, suffix, (PsiLanguageInjectionHost) el, ElementManipulators.getValueTextRange(el)));
            registrar.doneInjecting();
        }
    }


    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(XmlTag.class);
    }
}
