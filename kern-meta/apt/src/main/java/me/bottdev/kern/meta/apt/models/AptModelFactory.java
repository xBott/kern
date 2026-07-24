package me.bottdev.kern.meta.apt.models;

import me.bottdev.kern.meta.apt.models.executable.AptExecutableModelReader;
import me.bottdev.kern.meta.apt.models.type.*;
import me.bottdev.kern.meta.apt.models.variable.AptVariableModelReader;
import me.bottdev.kern.meta.core.models.ElementModel;
import me.bottdev.kern.meta.core.models.ModelFactory;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Optional;

public class AptModelFactory implements ModelFactory<Element> {

    @Override
    public Optional<ElementModel> create(Element element) {
        ElementModel model = switch (element.getKind()) {
            case CLASS -> AptClassModel.of((TypeElement) element);
            case INTERFACE -> AptInterfaceModel.of((TypeElement) element);
            case ENUM -> AptEnumModel.of((TypeElement) element);
            case RECORD -> AptRecordModel.of((TypeElement) element);
            case ANNOTATION_TYPE -> AptAnnotationTypeModel.of((TypeElement) element);
            case METHOD -> AptExecutableModelReader.readMethod((ExecutableElement) element);
            case CONSTRUCTOR -> AptExecutableModelReader.readConstructor((ExecutableElement) element);
            case FIELD -> AptVariableModelReader.readField((VariableElement) element);
            case PARAMETER -> AptVariableModelReader.readParameter((VariableElement) element);
            default -> null;
        };
        return Optional.ofNullable(model);
    }

}
