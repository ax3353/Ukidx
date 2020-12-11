package org.zk.ip.paramanno;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class GenericIntentionAction implements IntentionAction {

    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile)) {
            return false;
        }

        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        if (element == null) {
            return false;
        }

        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (!Optional.ofNullable(type).isPresent()) {
            return false;
        }

        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return (null != parameter && !JavaUtils.isAnnotationPresent(parameter, Annotation.REQUESTPARAM)) ||
                (null != method && !JavaUtils.isAllParameterWithAnnotation(method, Annotation.REQUESTPARAM));
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

}
