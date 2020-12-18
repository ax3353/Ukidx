package org.zk.ip.annotation;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class Annotation implements Cloneable {

    public static final Annotation REQUESTPARAM = new Annotation("@RequestParam", "org.springframework.web.bind.annotation.RequestParam");

    public static final Annotation REQUESTBODY = new Annotation("@RequestBody", "org.springframework.web.bind.annotation.RequestBody");

    public static final Annotation APIMODELPROPERTY = new Annotation("@ApiModelProperty", "io.swagger.annotations.ApiModelProperty");

    private final String label;

    private final String qualifiedName;

    private Map<String, AnnotationValue> attributePairs;

    public Annotation(@NotNull String label, @NotNull String qualifiedName) {
        this.label = label;
        this.qualifiedName = qualifiedName;
        this.attributePairs = Maps.newHashMap();
    }

    public String getQualifiedName() {
        return this.qualifiedName;
    }

    public Annotation withValue(@NotNull AnnotationValue value) {
        return this.withAttribute("value", value);
    }

    public Annotation withAttribute(@NotNull String key, @NotNull AnnotationValue value) {
        Annotation copy = this.clone();
        copy.attributePairs = Maps.newHashMap(this.attributePairs);
        return copy.addAttribute(key, value);
    }

    private Annotation addAttribute(String key, AnnotationValue value) {
        this.attributePairs.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(label);
        if (!Iterables.isEmpty(attributePairs.entrySet())) {
            builder.append(this.setupAttributeText());
        }
        return builder.toString();
    }

    private String setupAttributeText() {
        Optional<String> singleValue = this.getSingleValue();
        return singleValue.orElseGet(this::getComplexValue);
    }

    private String getComplexValue() {
        if (this.qualifiedName.equals(REQUESTBODY.getQualifiedName())) {
            return "";
        }

        StringBuilder builder = new StringBuilder("(");
        for (String key : attributePairs.keySet()) {
            builder.append(key);
            builder.append(" = ");
            builder.append(attributePairs.get(key).toString());
            builder.append(", ");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }

    private Optional<String> getSingleValue() {
        if (this.qualifiedName.equals(REQUESTBODY.getQualifiedName())) {
            return Optional.empty();
        }

        try {
            String value = Iterables.getOnlyElement(attributePairs.keySet());
            String builder = "(" + attributePairs.get(value).toString() + ")";
            return Optional.of(builder);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    protected Annotation clone() {
        try {
            return (Annotation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException();
        }
    }

    public interface AnnotationValue {
    }

    public static class StringValue implements AnnotationValue {
        private final String value;

        public StringValue(@NotNull String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "\"" + value + "\"";
        }
    }
}