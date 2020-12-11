package org.zk.ip.paramanno;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * 描述: 生成参数@RequestParam注解
 * @author kun.zhu
 * @date 2020/12/9 19:36
 */
public class GenerateRequestParamAnnotation extends GenericIntentionAction {

    @Override
    @IntentionName
    @NotNull
    public String getText() {
        return "[SpringX] Generate @RequestParam";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiParameter parameter = PsiTreeUtil.getParentOfType(element, PsiParameter.class);
        AnnotationService annotationService = AnnotationService.getInstance(project);
        if (null != parameter) {
            annotationService.addAnnotationWithParameterName(parameter, Annotation.REQUESTPARAM);
        } else {
            PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            if (null != method) {
                annotationService.addAnnotationWithParameterNames(method, Annotation.REQUESTPARAM);
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
