package org.ostelco.prime.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Use this class to get implementation objects to interfaces in prime-modules using ServiceLoader.
 * The libraries which have implementation classes should then add definition file to META-INF/services.
 * The name of the file should be name of Interface including package name.
 * The content of the file should be name of the implementing class including the package name.
 * Implementing class should have public no-args constructor.
 */
public class ResourceRegistry {
    
    /**
     * Get a service implementation by type
     * @param serviceClass the service interface class
     * @param <T> the service type
     * @return the service implementation
     * @throws RuntimeException if no implementation is found
     */
    public static <T> T getResource(Class<T> serviceClass) {
        return getResource(serviceClass, null);
    }
    
    /**
     * Get a named service implementation by type
     * @param serviceClass the service interface class
     * @param name the name of the implementation (using @Named annotation)
     * @param <T> the service type
     * @return the service implementation
     * @throws RuntimeException if no implementation is found
     */
    public static <T> T getResource(Class<T> serviceClass, String name) {
        ServiceLoader<T> services = ServiceLoader.load(serviceClass);
        Logger logger = LoggerFactory.getLogger(serviceClass);
        
        List<T> providers;
        if (name != null) {
            // For now, just return all providers when name is specified
            // TODO: Implement proper @Named annotation support
            providers = StreamSupport.stream(services.spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            providers = StreamSupport.stream(services.spliterator(), false)
                    .collect(Collectors.toList());
        }
        
        switch (providers.size()) {
            case 0:
                String nameMsg = name != null ? "named " + name : "";
                throw new RuntimeException("No implementations " + nameMsg + " found for interface " + serviceClass.getSimpleName());
            case 1:
                return providers.get(0);
            default:
                String nameMsg2 = name != null ? "named " + name : "";
                logger.warn("Multiple implementations {} found for interface {}", nameMsg2, serviceClass.getSimpleName());
                return providers.get(0);
        }
    }
}