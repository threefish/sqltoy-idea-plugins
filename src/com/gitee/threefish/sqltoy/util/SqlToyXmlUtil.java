package com.gitee.threefish.sqltoy.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiLiteralExpressionImpl;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import org.apache.commons.collections.CollectionUtils;
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

    public static final String EXT = "sql.xml";
    private static final String JAVA_DAO_REF = "org.sagacity.sqltoy.dao.SqlToyLazyDao";

    /**
     * 判断是sqltoyxml
     *
     * @param bindingElement
     * @return
     */
    public static boolean isSqToyXml(PsiElement bindingElement) {
        if (bindingElement.getContainingFile().getName().endsWith(EXT) && bindingElement instanceof XmlTag) {
            XmlTag xmlTag = (XmlTag) bindingElement;
            XmlAttribute id = xmlTag.getAttribute("id");
            if (xmlTag.getName().equals("sql") && Objects.nonNull(id)) {
                return true;
            }
        }
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

    /**
     * 可以跳转到xml
     *
     * @param literalExpression
     * @param fields
     * @return
     */
    public static boolean isInjectXml(PsiElement literalExpression, List<String> fields) {
        PsiElement p1 = literalExpression.getParent();
        if (!(p1 instanceof PsiExpressionList)) {
            return false;
        }
        PsiElement p2 = p1.getParent();
        if (!(p2 instanceof PsiMethodCallExpression)) {
            return false;
        }
        String text = p2.getText();
        return fields.stream().filter(s -> (text.startsWith(s + ".") || text.startsWith("super." + s + ".") || text.startsWith("this." + s + "."))).findAny().isPresent();
    }

    public static boolean isNewQueryExecutor (PsiLiteralExpression literalExpression,List<String> fields) {
        PsiElement p1 = literalExpression.getParent();
        if (!(p1 instanceof PsiExpressionList)) {
            return false;
        }
        PsiElement p2 = p1.getParent();
        if (!(p2 instanceof PsiNewExpression)) {
            return false;
        }
        if(!(p2.getText().startsWith("new QueryExecutor"))){
            return false;
        }
        return isInjectXml(p2,fields);
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
                                                XmlAttribute xmlAttribute = xmlTag.getAttribute("id");
                                                String id = xmlAttribute.getValue();
                                                if (key.equals(id)) {
                                                    result.add(xmlAttribute.getValueElement());
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

    public static boolean isSqlToyLazyDao(PsiField field) {
        PsiTypeElement typeElement = field.getTypeElement();
        PsiJavaCodeReferenceElement innermostComponentReferenceElement = typeElement.getInnermostComponentReferenceElement();
        if (Objects.nonNull(innermostComponentReferenceElement)) {
            String canonicalText = innermostComponentReferenceElement.getCanonicalText();
            if (JAVA_DAO_REF.equals(canonicalText)) {
                return true;
            }
        }
        return false;
    }

    public static List<PsiElement> findJavaPsiElement(Project project, Collection<VirtualFile> virtualFiles, String id) {
        List<PsiElement> result = new ArrayList<>();
        for (VirtualFile virtualFile : virtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile instanceof PsiJavaFile) {
                PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                PsiElement originalElement = psiJavaFile.getOriginalElement();
                List<PsiClass> psiClassList = PsiTreeUtil.getChildrenOfAnyType(originalElement, PsiClass.class);
                for (PsiClass psiClass : psiClassList) {
                    List<String> keys = new ArrayList<>();
                    List<PsiField> childrenOfAnyType = PsiTreeUtil.getChildrenOfAnyType(psiClass, PsiField.class);
                    childrenOfAnyType.addAll(SearchUtil.getExtendsClassFields(psiClass));
                    for (PsiField field : childrenOfAnyType) {
                        if (SqlToyXmlUtil.isSqlToyLazyDao(field)) {
                            keys.add(field.getName());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(keys)) {
                        List<PsiMethod> psiMethods = PsiTreeUtil.getChildrenOfAnyType(psiClass, PsiMethod.class);
                        List<PsiMethodCallExpression> psiMethodCallExpressions = new ArrayList<>();
                        for (PsiMethod psiMethod : psiMethods) {
                            List<PsiCodeBlock> psiCodeBlocks = PsiTreeUtil.getChildrenOfAnyType(psiMethod, PsiCodeBlock.class);
                            for (PsiCodeBlock psiCodeBlock : psiCodeBlocks) {
                                List<PsiDeclarationStatement> psiDeclarationStatements = PsiTreeUtil.getChildrenOfAnyType(psiCodeBlock, PsiDeclarationStatement.class);
                                for (PsiDeclarationStatement psiDeclarationStatement : psiDeclarationStatements) {
                                    List<PsiLocalVariable> psiLocalVariables = PsiTreeUtil.getChildrenOfAnyType(psiDeclarationStatement, PsiLocalVariable.class);
                                    for (PsiLocalVariable psiLocalVariable : psiLocalVariables) {
                                        psiMethodCallExpressions.addAll(PsiTreeUtil.getChildrenOfAnyType(psiLocalVariable, PsiMethodCallExpression.class));
                                    }
                                }
                                List<PsiExpressionStatement> psiExpressionStatements = PsiTreeUtil.getChildrenOfAnyType(psiCodeBlock, PsiExpressionStatement.class);
                                for (PsiExpressionStatement psiExpressionStatement : psiExpressionStatements) {
                                    psiMethodCallExpressions.addAll(PsiTreeUtil.getChildrenOfAnyType(psiExpressionStatement, PsiMethodCallExpression.class));
                                }
                            }
                        }
                        for (PsiMethodCallExpression psiMethodCallExpression : psiMethodCallExpressions) {
                            PsiElement xmlIdBindPsiElement = getXmlIdBindPsiElement(psiMethodCallExpression, keys, id);
                            if (Objects.nonNull(xmlIdBindPsiElement)) {
                                result.add(xmlIdBindPsiElement);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }


    public static PsiElement getXmlIdBindPsiElement(PsiMethodCallExpression psiMethodCallExpression, List<String> keys, String id) {
        for (String key : keys) {
            String text = psiMethodCallExpression.getText();
            if (text.startsWith(key + ".")
                    || text.startsWith("super." + key + ".")
                    || text.startsWith("this." + key + ".")) {
                PsiExpressionList psiExpressionList = PsiTreeUtil.getChildOfAnyType(psiMethodCallExpression, PsiExpressionList.class);
                if (Objects.nonNull(psiExpressionList)) {
                    List<PsiLiteralExpression> psiLiteralExpressions = PsiTreeUtil.getChildrenOfAnyType(psiExpressionList, PsiLiteralExpression.class);
                    for (PsiLiteralExpression psiLiteralExpression : psiLiteralExpressions) {
                        if (!psiLiteralExpression.getText().contains(" ") && id.equals(((PsiLiteralExpressionImpl) psiLiteralExpression).getInnerText())) {
                            return psiLiteralExpression;
                        }
                    }
                }
            }
        }
        return null;
    }

}
