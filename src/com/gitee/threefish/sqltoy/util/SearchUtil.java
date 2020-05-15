package com.gitee.threefish.sqltoy.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaShortClassNameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/14
 */
public class SearchUtil {

    public static GlobalSearchScope getSearchScope(Project project, @NotNull PsiElement element) {
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
        Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(element.getContainingFile().getVirtualFile());
        if (module != null) {
            searchScope = GlobalSearchScope.moduleScope(module);
        }
        return searchScope;
    }

    public static List<PsiField> getExtendsClassFields(PsiClass psiClass) {
        List<PsiField> psiFields = new ArrayList<>();
        List<PsiReferenceList> childrenOfAnyType = PsiTreeUtil.getChildrenOfAnyType(psiClass, PsiReferenceList.class);
        for (PsiReferenceList psiReferenceList : childrenOfAnyType) {
            PsiJavaCodeReferenceElement[] referenceElements = psiReferenceList.getReferenceElements();
            for (PsiJavaCodeReferenceElement referenceElement : referenceElements) {
                String qualifiedName = referenceElement.getQualifiedName();
                GlobalSearchScope globalSearchScope = GlobalSearchScope.projectScope(psiClass.getProject());
                Collection<PsiClass> psiClasses = JavaShortClassNameIndex.getInstance().get(referenceElement.getReferenceName(), psiClass.getProject(), globalSearchScope);
                for (PsiClass aClass : psiClasses) {
                    if (aClass.getQualifiedName().equals(qualifiedName)) {
                        psiFields.addAll(Arrays.asList(aClass.getFields()));
                        psiFields.addAll(getExtendsClassFields(aClass));
                    }
                }
            }
        }
        return psiFields;
    }
}
