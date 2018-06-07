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

    // todo turn into generic code
    @Override
    public String idFromValueAndType(Object obj, Class<?> subType) {
        String typeId = null;
        switch (subType.getSimpleName()) {
            case "NewLibraryCreated":
                typeId = "NewLibraryCreated";
                break;
            case "BookReferenceAdded":
                typeId = "BookReferenceAdded";
                break;
            case "BookCopyBorrowed":
                typeId = "BookCopyBorrowed";
                break;
            case "BookCopyReturned":
                typeId = "BookCopyReturned";
                break;
            default:
                return null;
        }
        return typeId;
    }

    // todo turn into generic code
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
                break;
            case "BookCopyBorrowed":
                try {
                    subType = Class.forName("com.nicolasbarbe.library.event.BookCopyBorrowed");
                } catch (ClassNotFoundException e) {
                    return TypeFactory.unknownType();
                }
                break;
            case "BookCopyReturned":
                try {
                    subType = Class.forName("com.nicolasbarbe.library.event.BookCopyReturned");
                } catch (ClassNotFoundException e) {
                    return TypeFactory.unknownType();
                }
                break;

            default:
                return TypeFactory.unknownType();
        }
        return context.constructSpecializedType(baseType, subType);
    }
}