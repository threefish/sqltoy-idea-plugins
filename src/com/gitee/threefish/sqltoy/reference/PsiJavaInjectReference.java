package com.gitee.threefish.sqltoy.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/9/30
 */
public class PsiJavaInjectReference implements PsiReference {

    PsiElement formElemnet;
    PsiElement bingdElement;

    public PsiJavaInjectReference(PsiElement formElemnet, PsiElement bingdElement) {
        this.formElemnet = formElemnet;
        this.bingdElement = bingdElement;
    }

    @Override
    public PsiElement getElement() {
        return this.formElemnet;
    }

    @Override
    public TextRange getRangeInElement() {
        String text = this.formElemnet.getText();
        boolean match = text.startsWith("\"") && text.endsWith("\"");
        final int len = 2;
        if (match && text.length() > len) {
            return new TextRange(1, this.formElemnet.getTextLength() - 1);
        }
        return new TextRange(0, this.formElemnet.getTextLength());
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return bingdElement;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return bingdElement.getText();
    }

    @Override
    public PsiElement handleElementRename(String s) throws IncorrectOperationException {
        return formElemnet;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isReferenceTo(PsiElement psiElement) {
        return psiElement == resolve();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
