package com.nicolasbarbe.ddd.domain;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.nicolasbarbe.ddd.domain.EventRegistry.buildEventId;

/**
 * Description
 */
@Component
public class EventTypeIdResolver extends TypeIdResolverBase {

    protected JavaType baseType;
    
    private EventRegistry registry;

    public EventTypeIdResolver(EventRegistry registry) {
        super();
        this.registry = registry;
    }


    @Override
    public void init(JavaType baseType) {
        this.baseType = baseType;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public String idFromValue(Object obj) {
        return idFromValueAndType(obj, obj.getClass());
    }

    @Override
    public String idFromValueAndType(Object obj, Class<?> subType) {
        return buildEventId(subType);
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class eventClass = registry.getEventById(id);
        if(null != eventClass) {
            return context.constructSpecializedType(baseType, eventClass);
        } else {
                return TypeFactory.unknownType();
        }
    }
}