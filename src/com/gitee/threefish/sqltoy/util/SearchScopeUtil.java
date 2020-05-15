package com.gitee.threefish.sqltoy.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/14
 */
public class SearchScopeUtil {
    public static GlobalSearchScope getSearchScope(Project project, @NotNull PsiElement element) {
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
        Module module = ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(element.getContainingFile().getVirtualFile());
        if (module != null) {
            searchScope = GlobalSearchScope.moduleScope(module);
        }
        return searchScope;
    }
}
