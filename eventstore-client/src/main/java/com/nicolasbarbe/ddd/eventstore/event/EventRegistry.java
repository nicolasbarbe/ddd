package com.nicolasbarbe.ddd.eventstore.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description
 */
@Component
public class EventRegistry {
    
    private Map<String, Class> registry;

    public EventRegistry(@Value("${eventsource.events.package}") String basePackage, ApplicationContext context) {

        this.registry = new HashMap<>(10);

        Assert.notNull(basePackage, "eventsource.events.package property must be set.");
        Assert.notNull(context, "Application context cannot be null.");

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.setEnvironment(context.getEnvironment());
        provider.setResourceLoader(context);
        provider.addIncludeFilter(new AnnotationTypeFilter(DomainEvent.class));

        final Set<BeanDefinition> classes = provider.findCandidateComponents(basePackage);

        for (BeanDefinition bean: classes) {
            Class<?> eventClass = null;
            try {
                eventClass = Class.forName(bean.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Cannot load class from the classpath.", e);
            }
            this.registry.putIfAbsent(buildEventId(eventClass), eventClass);
        }
    }

    public Class getEventById(String id) {
        return registry.get(id);
    }

    public boolean hasEvent(String id) {
        return this.registry.containsKey(id);
    }

    public static String buildEventId(Class eventClass) {
        String simpleClassName = eventClass.getSimpleName();
        return simpleClassName.substring(0,1).toLowerCase() + simpleClassName.substring(1);
    }
}