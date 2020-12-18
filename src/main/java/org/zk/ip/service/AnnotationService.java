package org.zk.ip.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;
import org.zk.ip.annotation.Annotation;
import org.zk.ip.utils.JavaUtils;

import java.util.Optional;

public class AnnotationService {

    private final Project project;

    private final JavaPsiFacade javaPsiFacade;

    private final EditorService editorService;

    /**
     * Instantiates a new Annotation service.
     *
     * @param project the project
     */
    public AnnotationService(Project project) {
        this.project = project;
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        this.editorService = EditorService.getInstance(project);
    }

    /**
     * Gets instance.
     *
     * @param project the project
     * @return the instance
     */
    public static AnnotationService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, AnnotationService.class);
    }

    /**
     * Add annotation.
     *
     * @param parameter  the parameter
     * @param annotation the annotation
     */
    public void addAnnotation(@NotNull PsiParameter parameter, @NotNull Annotation annotation) {
        PsiModifierList modifierList = parameter.getModifierList();
        if (null == modifierList) {
            return;
        }

        // import package
        this.importClazz((PsiJavaFile) parameter.getContainingFile(), annotation.getQualifiedName());

        PsiElementFactory elementFactory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiAnnotation psiAnnotation = elementFactory.createAnnotationFromText(annotation.toString(), parameter);
        modifierList.add(psiAnnotation);
        JavaCodeStyleManager.getInstance(project).shortenClassReferences(psiAnnotation.getParent());
    }

    public void importClazz(PsiJavaFile file, String clazzName) {
        if (!JavaUtils.hasImportClazz(file, clazzName)) {
            Optional<PsiClass> clazz = JavaUtils.findClazz(project, clazzName);
            PsiImportList importList = file.getImportList();
            if (clazz.isPresent() && null != importList) {
                PsiElementFactory elementFactory = javaPsiFacade.getElementFactory();
                PsiImportStatement statement = elementFactory.createImportStatement(clazz.get());
                importList.add(statement);
                editorService.format(file, statement);
            }
        }
    }

    /**
     * Add annotation with parameter name for method parameters.
     */
    public void addAnnotationWithParameterNames(@NotNull PsiMethod method, @NotNull Annotation annotation) {
        PsiParameterList parameterList = method.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        for (PsiParameter param : parameters) {
            addAnnotationWithParameterName(param, annotation);
        }
    }

    /**
     * Add annotation with parameter name.
     */
    public void addAnnotationWithParameterName(@NotNull PsiParameter parameter, @NotNull Annotation annotation) {
        String name = parameter.getName();
        AnnotationService.getInstance(parameter.getProject())
                .addAnnotation(parameter, annotation.withValue(new Annotation.StringValue(name)));
    }
}
