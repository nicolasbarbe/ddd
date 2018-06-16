package com.nicolasbarbe.ddd.eventstore.event;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.stereotype.Component;

import static com.nicolasbarbe.ddd.eventstore.event.EventRegistry.buildEventId;

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