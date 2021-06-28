package org.zk.ip.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
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
 * 描述: Mybatis Mapper接口的方法上生成@Param注解
 *
 * @author kun.zhu
 * @date 2021/6/25 14:31
 */
public class MybatisParamIntention implements IntentionAction {

    @Override
    @IntentionName
    @NotNull
    public String getText() {
        return "[UkidX] Generate @Param";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @Override
    @NotNull
    @IntentionFamilyName
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
        if (type == null || !type.isInterface()) {
            return false;
        }

        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return (null != parameter && !JavaUtils.isAnnotationPresent(parameter, Annotation.PARAM))
                || (null != method && !JavaUtils.isAllParameterWithAnnotation(method, Annotation.PARAM));
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
            PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
            AnnotationService annotationService = AnnotationService.getInstance(project);
            if (null != parameter) {
                annotationService.addAnnotationWithParameterName(parameter, Annotation.PARAM);
            } else {
                PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
                if (null != method) {
                    annotationService.addAnnotationWithParameterNames(method, Annotation.PARAM);
                }
            }
        });
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
