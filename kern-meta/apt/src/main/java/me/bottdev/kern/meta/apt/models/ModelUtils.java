package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.core.models.AnnotationModel;
import me.bottdev.kern.meta.core.models.Modifier;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ModelUtils {

    public static String readPackageName(TypeElement typeElement) {
        Element enclosing = typeElement.getEnclosingElement();

        while (enclosing != null && enclosing.getKind() != ElementKind.PACKAGE) {
            enclosing = enclosing.getEnclosingElement();
        }
        if (enclosing instanceof PackageElement packageElement) {
            return packageElement.getQualifiedName().toString();
        }
        int lastDot = typeElement.getQualifiedName().toString().lastIndexOf('.');
        return lastDot > 0 ? typeElement.getQualifiedName().toString().substring(0, lastDot) : "";

    }

    public static List<AnnotationModel> readAnnotations(Element element) {
        return element.getAnnotationMirrors().stream()
                .map(AptAnnotationReader::read)
                .collect(Collectors.toList());
    }

    public static Set<Modifier> readModifiers(Element element) {
        return element.getModifiers().stream()
                .map(modifier -> new Modifier(modifier.name()))
                .collect(Collectors.toUnmodifiableSet());
    }

}
