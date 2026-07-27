package me.bottdev.kern.meta.core.models;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public final class AnnotationProxyFactory {

    public static <A extends Annotation> A create(Class<A> type, AnnotationModel model) {
        return type.cast(
                Proxy.newProxyInstance(
                    type.getClassLoader(),
                    new Class<?>[]{type},
                    (_, method, _) -> invoke(model, type, method)
                )
        );
    }

    private static Object invoke(AnnotationModel model, Class<?> annotationType, Method method) {
        if (method.getName().equals("annotationType")) return annotationType;
        if (method.getName().equals("toString")) return "@" + annotationType.getName();
        if (method.getName().equals("hashCode")) return System.identityHashCode(model);
        if (method.getName().equals("equals")) return false; // упрощённо — идентичность прокси не поддерживаем

        AnnotationValueModel value = model.values().get(method.getName());
        if (value == null) {
            Object defaultValue = method.getDefaultValue();
            if (defaultValue == null) {
                throw new IllegalStateException("нет значения и нет default для " + method.getName()
                        + " в " + annotationType.getCanonicalName());
            }
            return defaultValue;
        }
        return unwrap(value, method.getReturnType());
    }

    @SuppressWarnings("unchecked")
    private static Object unwrap(AnnotationValueModel value, Class<?> targetType) {
        return switch (value) {
            case AnnotationValueModel.OfPrimitive p -> p.value();
            case AnnotationValueModel.OfString s -> s.value();
            case AnnotationValueModel.OfType t -> resolveClass(t.value());
            case AnnotationValueModel.OfEnumConstant e -> Enum.valueOf(
                    (Class<? extends Enum>) targetType, e.constantName());
            case AnnotationValueModel.OfAnnotation a ->
                    create((Class<? extends Annotation>) targetType, a.value());
            case AnnotationValueModel.OfArray arr -> unwrapArray(arr, targetType);
        };
    }

    private static Class<?> resolveClass(TypeRef ref) {
        try {
            return Class.forName(ref.qualifiedName());
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                    "атрибут-Class '" + ref.qualifiedName() + "' недоступен как java.lang.Class во время "
                            + "annotation processing (тип ещё не скомпилирован в этом раунде) — читайте его через "
                            + "AnnotationModel.values(), а не через типизированный .with(...)", e);
        }
    }

    private static Object unwrapArray(AnnotationValueModel.OfArray arr, Class<?> targetType) {
        Class<?> componentType = targetType.getComponentType();
        Object array = Array.newInstance(componentType, arr.values().size());
        for (int i = 0; i < arr.values().size(); i++) {
            Array.set(array, i, unwrap(arr.values().get(i), componentType));
        }
        return array;
    }
}