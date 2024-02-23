package org.zk.ip.intention;

import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.utils.NamingConverter;

/**
 * 描述: 类字段名驼峰转下划线
 *
 * @author kun.zhu
 * @date 2020/12/17 17:33
 */
public class ToUnderlineIntention extends NamingConvertIntention {

    @Override
    @IntentionName
    @NotNull
    public String getText() {
        return "[UkidX] To Underline";
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    @Override
    void convert(@NotNull Project project, PsiField psiField) {
        String underline = NamingConverter.camelToUnderline(psiField.getName());
        WriteCommandAction.runWriteCommandAction(project,
                (Computable<PsiElement>) () -> psiField.setName(underline));
    }
}
