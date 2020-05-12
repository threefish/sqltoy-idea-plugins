package com.gitee.threefish.sqltoy.linemarker.navigation;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.Arrays;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/12
 */
public class SqlToyXmlNavigationHandler  extends AbstractNavigationHandler {
    @Override
    public boolean canNavigate(PsiElement psiElement) {
        return false;
    }

    @Override
    public List<VirtualFile> findTemplteFileList(PsiElement psiElement) {
        return Arrays.asList();
    }
}
