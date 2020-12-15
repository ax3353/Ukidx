package org.zk.ip.paramanno;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class GenericIntentionAction implements IntentionAction {

    private final Annotation annotation;

    public GenericIntentionAction(Annotation annotation) {
        this.annotation = annotation;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return this.getText();
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

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        AnnotationService annotationService = AnnotationService.getInstance(project);
        if (null != parameter) {
            annotationService.addAnnotationWithParameterName(parameter, annotation);
        } else {
            PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            if (null != method) {
                annotationService.addAnnotationWithParameterNames(method, annotation);
            }
        }
    }

}
