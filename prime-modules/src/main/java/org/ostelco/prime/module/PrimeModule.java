package org.ostelco.prime.module;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.Discoverable;

/**
 * Prime is a multi-module component, wherein each module is a separate library.
 * Module is such a library which needs access to the Dropwizard's Environment
 * for actions like registering Managed objects, Resources,
 * HealthCheck etc. and/or has some configuration.
 * Each Module has to implement this interface.
 * That class will then get Environment object on overriding the init method.
 * Same class may also accept module specific configuration.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface PrimeModule extends Discoverable {
    
    /**
     * Initialize the module with the Dropwizard environment
     * @param env the Dropwizard environment
     */
    default void init(Object env) {
        // Default empty implementation - using Object instead of Environment for now
    }
}