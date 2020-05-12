package com.gitee.threefish.sqltoy.linemarker;

import com.gitee.threefish.sqltoy.linemarker.function.FunctionTooltip;
import com.gitee.threefish.sqltoy.linemarker.navigation.SqlToyXmlNavigationHandler;
import com.gitee.threefish.sqltoy.util.SqlToyXmlUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/12
 */
public class Java2XmlLineMarkerProvider implements LineMarkerProvider {

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        try {
            if (SqlToyXmlUtil.isSqToyXml(psiElement)) {
                Icon icon = AllIcons.FileTypes.Xml;
                return new LineMarkerInfo<>(psiElement, psiElement.getTextRange(), icon,
                        new FunctionTooltip(), new SqlToyXmlNavigationHandler(),
                        GutterIconRenderer.Alignment.LEFT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
