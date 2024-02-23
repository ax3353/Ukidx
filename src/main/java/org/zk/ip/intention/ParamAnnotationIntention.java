package org.zk.ip.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.annotation.Annotation;
import org.zk.ip.service.AnnotationService;
import org.zk.ip.utils.JavaUtils;

import java.util.Optional;

/**
 * 描述: <>
 * @author kun.zhu
 * @date 2020/12/16 17:43
 */
public abstract class ParamAnnotationIntention implements IntentionAction {

    private final Annotation annotation;

    public ParamAnnotationIntention(Annotation annotation) {
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
        if (Optional.ofNullable(type).isEmpty()) {
            return false;
        }

        PsiCodeBlock codeBlock = PsiTreeUtil.getParentOfType(element, PsiCodeBlock.class);
        if (Optional.ofNullable(codeBlock).isPresent()) {
            return false;
        }

        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return (null != parameter && !JavaUtils.isAnnotationPresent(parameter, Annotation.REQUESTPARAM)
                && !JavaUtils.isAnnotationPresent(parameter, Annotation.REQUESTBODY)) ||
                (null != method && !JavaUtils.isAllParameterWithAnnotation(method, Annotation.REQUESTPARAM)
                        && !JavaUtils.isAllParameterWithAnnotation(method, Annotation.REQUESTBODY));
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        WriteCommandAction.runWriteCommandAction(project, () -> {
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
        });
    }

}
