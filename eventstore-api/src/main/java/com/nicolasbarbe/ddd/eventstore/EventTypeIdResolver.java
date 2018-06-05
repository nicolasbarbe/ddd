package com.nicolasbarbe.ddd.eventstore;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Description
 */
public class EventTypeIdResolver extends TypeIdResolverBase {

    protected JavaType baseType;

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
        String typeId = null;
        switch (subType.getSimpleName()) {
            case "NewLibraryCreated":
                typeId = "NewLibraryCreated";
                break;
            case "BookReferenceAdded":
                typeId = "BookReferenceAdded";
        }
        return typeId;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<?> subType = null;
        switch (id) {
            case "BookReferenceAdded":
                try {
                    subType = Class.forName("com.nicolasbarbe.library.event.BookReferenceAdded");
                } catch (ClassNotFoundException e) {
                    return TypeFactory.unknownType();
                }
                break;
            case "NewLibraryCreated":
                try {
                    subType = Class.forName("com.nicolasbarbe.library.event.NewLibraryCreated");
                } catch (ClassNotFoundException e) {
                    return TypeFactory.unknownType();
                }
        }
        return context.constructSpecializedType(baseType, subType);
    }
}