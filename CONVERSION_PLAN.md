# Kotlin to Java Conversion Plan

## Current State
- 358 Kotlin files to convert
- Dropwizard-based microservices architecture
- Gradle build system (to be converted to Maven)
- Java 12 (to be upgraded to Java 17)

## Conversion Strategy

### Phase 1: Infrastructure Setup
1. ✅ Create Maven parent POM with Java 17
2. ✅ Create module POMs for all 50 modules
3. ✅ Update dependency versions for Java 17 compatibility
4. ✅ Configure Kotlin compilation for transition period

### Phase 2: Systematic Kotlin to Java Conversion
1. **Start with core/model modules** (foundation classes)
2. **Convert utility and common modules** 
3. **Convert service interfaces and implementations**
4. **Convert REST endpoints and resources**
5. **Convert test classes**

### Phase 3: Framework Considerations
- **Keep Dropwizard**: It's already well-established in the codebase
- **Alternative**: Could migrate to Spring Boot for better Java ecosystem integration
- **Decision**: Start with Dropwizard, evaluate Spring Boot migration later

### Phase 4: Validation and Testing
1. Ensure each module compiles after conversion
2. Run existing tests to verify functionality
3. Update any Kotlin-specific test utilities

## Conversion Priorities (Order of modules)

### High Priority (Core/Foundation)
1. `model` - Core data models
2. `prime-modules` - Core interfaces and utilities
3. `logging` - Logging utilities
4. `jersey` - REST framework utilities

### Medium Priority (Services)
5. `data-store` - Data access layer
6. `neo4j-store` - Graph database access
7. `document-data-store` - Document storage
8. `auth-server` - Authentication services
9. `prime` - Main application

### Lower Priority (Endpoints and Tools)
10. `customer-endpoint` - Customer API
11. `admin-endpoint` - Admin API
12. `payment-processor` - Payment services
13. Tools and utilities modules

## Conversion Approach per File
1. **Data Classes** → Java classes with getters/setters, equals, hashCode, toString
2. **Object/Companion Object** → Static methods and constants
3. **Extension Functions** → Static utility methods
4. **Nullable Types** → Optional<T> or @Nullable annotations
5. **Lambda Expressions** → Java lambda expressions or method references
6. **Coroutines** → CompletableFuture or reactive streams
7. **Kotlin Collections** → Java Collections with appropriate generics

## Tools and Scripts
- Custom Kotlin-to-Java converter script
- Maven POM generator
- Dependency analyzer
- Test runner for validation