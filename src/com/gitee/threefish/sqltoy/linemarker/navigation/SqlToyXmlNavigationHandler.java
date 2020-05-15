package com.gitee.threefish.sqltoy.linemarker.navigation;

import com.gitee.threefish.sqltoy.util.SearchUtil;
import com.gitee.threefish.sqltoy.util.SqlToyXmlUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/5/12
 */
public class SqlToyXmlNavigationHandler  extends AbstractNavigationHandler {
    @Override
    public boolean canNavigate(PsiElement psiElement) {
        return true;
    }

    @Override
    public List<PsiElement> findReferences(PsiElement psiElement) {
        XmlTag xmlTag = (XmlTag) psiElement;
        XmlAttribute xmlAttribute= xmlTag.getAttribute("id");
        if (xmlTag.getName().equals("sql") && Objects.nonNull(xmlAttribute)) {
            String id = xmlAttribute.getValue();
            Project project = psiElement.getProject();
            final Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, "java", SearchUtil.getSearchScope(project, psiElement));
            return SqlToyXmlUtil.findJavaPsiElement(project, virtualFiles, id);
        }
        return Arrays.asList();
    }
}
