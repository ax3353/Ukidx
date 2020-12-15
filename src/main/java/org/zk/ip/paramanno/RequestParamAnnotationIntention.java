package org.zk.ip.paramanno;

import com.intellij.codeInspection.util.IntentionName;
import org.jetbrains.annotations.NotNull;

/**
 * 描述: 生成参数@RequestParam注解
 *
 * @author kun.zhu
 * @date 2020/12/9 19:36
 */
public class RequestParamAnnotationIntention extends GenericIntentionAction {

    public RequestParamAnnotationIntention() {
        super(Annotation.REQUESTPARAM);
    }

    @Override
    @IntentionName
    @NotNull
    public String getText() {
        return "[UkidX] Generate @RequestParam";
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
