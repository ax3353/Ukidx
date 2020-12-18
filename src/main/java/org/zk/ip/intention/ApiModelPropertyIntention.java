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
import org.jetbrains.annotations.NotNull;
import org.zk.ip.annotation.Annotation;
import org.zk.ip.service.AnnotationService;

import java.util.Optional;

/**
 * 描述: 类属性上生成@ApiModelProperty注解
 *
 * @author kun.zhu
 * @date 2020/12/17 17:33
 */
public class ApiModelPropertyIntention implements IntentionAction {

    @Override
    @IntentionName
    @NotNull
    public String getText() {
        return "[UkidX] Generate @ApiModelProperty";
    }

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

        PsiCodeBlock codeBlock = PsiTreeUtil.getParentOfType(element, PsiCodeBlock.class);
        if (codeBlock != null) {
            return false;
        }

        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        if (parameter != null) {
            return false;
        }

        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method != null) {
            return false;
        }

        PsiField psiField = PsiTreeUtil.getParentOfType(element, PsiField.class);
        if (psiField != null) {
            PsiModifierList modifierList = psiField.getModifierList();
            if (null == modifierList || modifierList.hasAnnotation(Annotation.APIMODELPROPERTY.getQualifiedName())
                    || modifierList.hasModifierProperty(PsiModifier.STATIC)
                    || modifierList.hasModifierProperty(PsiModifier.FINAL)) {
                return false;
            }
        }

        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return Optional.ofNullable(psiClass).isPresent();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        final AnnotationService annotationService = AnnotationService.getInstance(project);
        final PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();

        final PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        if (element == null) {
            return;
        }

        final PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        if (psiClass == null) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            // 导包
            annotationService.importClazz((PsiJavaFile) psiFile.getContainingFile()
                    , Annotation.APIMODELPROPERTY.getQualifiedName());
        });

        // 单个字段加ApiModelProperty注解
        PsiField singlePsiField = PsiTreeUtil.getParentOfType(element, PsiField.class);
        if (singlePsiField != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    this.addApiModelProperty(singlePsiField, elementFactory);
                } catch (Exception e) {
                    // nothing
                }
            });
            return;
        }

        // 类下所有的字段加ApiModelProperty注解
        WriteCommandAction.runWriteCommandAction(project, () -> {
            try {
                for (PsiField psiField : psiClass.getFields()) {
                    this.addApiModelProperty(psiField, elementFactory);
                }
            } catch (Exception e) {
                // nothing
            }
        });
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    /**
     * 描述: 添加注解
     *
     * @author kun.zhu
     * @date 2020/12/18 11:39
     */
    private void addApiModelProperty(PsiField psiField, PsiElementFactory elementFactory) {
        PsiModifierList modifierList = psiField.getModifierList();
        if (null == modifierList
                || modifierList.hasAnnotation(Annotation.APIMODELPROPERTY.getQualifiedName())
                || modifierList.hasModifierProperty(PsiModifier.STATIC)
                || modifierList.hasModifierProperty(PsiModifier.FINAL)) {
            return;
        }

        String annotation = "@ApiModelProperty(\"" + psiField.getName() + "\")";
        PsiAnnotation psiAnnotation = elementFactory.createAnnotationFromText(annotation, psiField);
        modifierList.addBefore(psiAnnotation, modifierList.findElementAt(0));
    }
}
