package com.gitee.threefish.sqltoy.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/12
 */
public class SqlToyXmlUtil {
    /**
     * 判断java字符串是否是sql
     *
     * @param bindingElement
     * @return
     */
    public static boolean isSqToyXml(PsiElement bindingElement) {
        if (bindingElement instanceof PsiLiteralExpression) {

        }
        if (bindingElement instanceof PsiReferenceExpression) {
            PsiReferenceExpressionImpl psiReferenceExpression = (PsiReferenceExpressionImpl) bindingElement;
            String fieldName = psiReferenceExpression.getReference().getCanonicalText();
            PsiTreeUtil.getParentOfType(bindingElement, PsiClass.class).getFields();

            PsiElement elementAtOffset = PsiUtilBase.getElementAtOffset(psiReferenceExpression.getContainingFile(), 0);
            System.out.println(elementAtOffset);
        }
        if (bindingElement instanceof PsiMethodCallExpression) {
            System.out.println(((PsiMethodCallExpression) bindingElement));
        }
        //这里判断java中的语言片段
        return false;
    }

    /**
     * 判断是否是sqltoy xml文件
     *
     * @param containingFile
     * @return
     */
    public static boolean isSqlToyXmlFile(PsiFile containingFile) {
        if (!isXmlFile(containingFile)) {
            return false;
        }
        XmlTag rootTag = ((XmlFile) containingFile).getRootTag();
        return null != rootTag && "sqltoy".equals(rootTag.getName());
    }

    /**
     * 判断是否是xml文件
     *
     * @param file
     * @return
     */
    static boolean isXmlFile(@NotNull PsiFile file) {
        return file instanceof XmlFile;
    }

    /**
     * 查询符合条件的xml
     *
     * @param psiElements
     * @return
     */
    public static List<PsiElement> findXmlTexts(PsiElement[] psiElements) {
        List<PsiElement> xmlTexts = new ArrayList<>();
        for (PsiElement psiElement : psiElements) {
            if (psiElement instanceof XmlText) {
                xmlTexts.add(psiElement);
            }
        }
        return xmlTexts;
    }

    public static boolean isInjectXml(PsiLiteralExpression literalExpression, List<String> fields) {
        PsiElement p1 = literalExpression.getParent();
        if (!(p1 instanceof PsiExpressionList)) {
            return false;
        }
        PsiElement p2 = p1.getParent();
        if (!(p2 instanceof PsiMethodCallExpression)) {
            return false;
        }
        String text = p2.getText();
        return fields.stream().filter(s -> text.startsWith(s + ".")).findAny().isPresent();
    }

    public static List<PsiElement> findXmlPsiElement(Project project, Collection<VirtualFile> virtualFiles, String key) {
        List<PsiElement> result = new ArrayList<>();
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile instanceof XmlFileImpl) {
                XmlFileImpl xmlFile = (XmlFileImpl) psiFile;
                PsiElement navigationElement = xmlFile.getNavigationElement();
                if (Objects.nonNull(navigationElement)) {
                    if (Objects.nonNull(navigationElement.getChildren())) {
                        PsiElement child = navigationElement.getChildren()[0];
                        if (Objects.nonNull(child)) {
                            if (Objects.nonNull(child.getChildren()) && child.getChildren().length > 0) {
                                PsiElement child1 = child.getChildren()[1];
                                if (Objects.nonNull(child1)) {
                                    PsiElement[] psiElements = child1.getChildren();
                                    if (Objects.nonNull(psiElements) && psiElements.length > 0) {
                                        List<XmlTag> xmlTags = PsiTreeUtil.getChildrenOfAnyType(child1, XmlTag.class);
                                        for (XmlTag xmlTag : xmlTags) {
                                            if ("sql".equals(xmlTag.getName())) {
                                                String id = xmlTag.getAttribute("id").getValue();
                                                if (key.equals(id)) {
                                                    result.add(xmlTag.getNavigationElement());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
