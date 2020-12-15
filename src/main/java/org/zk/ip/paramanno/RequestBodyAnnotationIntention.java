package org.zk.ip.paramanno;

import com.intellij.codeInspection.util.IntentionName;
import org.jetbrains.annotations.NotNull;

/**
 * 描述: 生成参数@RequestBody
 *
 * @author kun.zhu
 * @date 2020/12/11 15:51
 */
public class RequestBodyAnnotationIntention extends GenericIntentionAction {

    public RequestBodyAnnotationIntention() {
        super(Annotation.REQUESTBODY);
    }

    @Override
    @IntentionName
    @NotNull
    public String getText() {
        return "[UkidX] Generate @RequestBody";
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

}
