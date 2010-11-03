package org.restlet.ext.guice;

import java.lang.annotation.Annotation;

import org.restlet.resource.Finder;

/**
 * Factory for dependency-injecting Finders.
 */
public interface DependencyInjection {

    /**
     * Returns a Finder that will obtain a dependency-injected instance of
     * the ServerResource subtype bound to the type associated with the given class.
     */
    Finder inject(Class<?> cls);

    /**
     * Returns a Finder that will obtain a dependency-injected instance of
     * the ServerResource subtype bound to the type and qualifier associated
     * with the given class.
     */
    Finder inject(Class<?> cls, Class<? extends Annotation> qualifier);
}