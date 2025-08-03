#!/usr/bin/env python3

import os
from pathlib import Path

def create_module_pom(module_name: str, description: str, dependencies: list = None):
    """Create a basic POM file for a module"""
    if dependencies is None:
        dependencies = []
    
    pom_content = f'''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.ostelco</groupId>
        <artifactId>ostelco-core</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>{module_name}</artifactId>
    <name>{module_name.replace('-', ' ').title()}</name>
    <description>{description}</description>

    <dependencies>
        <!-- Common dependencies -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
'''
    
    # Add specific dependencies
    for dep in dependencies:
        pom_content += f'''        <dependency>
            <groupId>{dep['groupId']}</groupId>
            <artifactId>{dep['artifactId']}</artifactId>
        </dependency>
'''
    
    pom_content += '''    </dependencies>
</project>'''
    
    return pom_content

def main():
    project_root = Path("/workspace/project/telco-core")
    
    # Module definitions with their dependencies
    modules = {
        'prime-modules': {
            'description': 'Core interfaces and utilities',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'model'},
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-core'},
                {'groupId': 'com.fasterxml.jackson.core', 'artifactId': 'jackson-databind'},
            ]
        },
        'logging': {
            'description': 'Logging utilities and configuration',
            'dependencies': [
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-logging'},
                {'groupId': 'ch.qos.logback', 'artifactId': 'logback-classic'},
            ]
        },
        'jersey': {
            'description': 'Jersey REST framework utilities',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-jersey'},
                {'groupId': 'org.glassfish.jersey.core', 'artifactId': 'jersey-server'},
            ]
        },
        'data-store': {
            'description': 'Data access layer',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'model'},
                {'groupId': 'com.google.cloud', 'artifactId': 'google-cloud-datastore'},
            ]
        },
        'neo4j-store': {
            'description': 'Neo4j graph database access',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'model'},
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.neo4j.driver', 'artifactId': 'neo4j-java-driver'},
            ]
        },
        'document-data-store': {
            'description': 'Document storage services',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'model'},
                {'groupId': 'com.google.cloud', 'artifactId': 'google-cloud-firestore'},
            ]
        },
        'auth-server': {
            'description': 'Authentication server',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.ostelco', 'artifactId': 'jersey'},
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-core'},
            ]
        },
        'prime': {
            'description': 'Main application',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.ostelco', 'artifactId': 'jersey'},
                {'groupId': 'org.ostelco', 'artifactId': 'logging'},
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-core'},
            ]
        },
        'customer-endpoint': {
            'description': 'Customer API endpoints',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.ostelco', 'artifactId': 'jersey'},
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-jersey'},
            ]
        },
        'admin-endpoint': {
            'description': 'Admin API endpoints',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.ostelco', 'artifactId': 'jersey'},
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-jersey'},
            ]
        },
        'customer-support-endpoint': {
            'description': 'Customer support endpoints',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.ostelco', 'artifactId': 'jersey'},
            ]
        },
        'payment-processor': {
            'description': 'Payment processing services',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'com.stripe', 'artifactId': 'stripe-java'},
            ]
        },
        'ocs-ktc': {
            'description': 'Online Charging System',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.ostelco', 'artifactId': 'diameter-stack'},
            ]
        },
        'ocsgw': {
            'description': 'OCS Gateway',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'diameter-stack'},
                {'groupId': 'io.grpc', 'artifactId': 'grpc-netty-shaded'},
            ]
        },
        'ekyc': {
            'description': 'eKYC services',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
            ]
        },
        'imei-lookup': {
            'description': 'IMEI lookup services',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
            ]
        },
        'app-notifier': {
            'description': 'Application notification services',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'com.google.firebase', 'artifactId': 'firebase-admin'},
            ]
        },
        'email-notifier': {
            'description': 'Email notification services',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
            ]
        },
        'slack': {
            'description': 'Slack integration',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
            ]
        },
        'publisher-extensions': {
            'description': 'Publisher extensions',
            'dependencies': [
                {'groupId': 'com.google.cloud', 'artifactId': 'google-cloud-pubsub'},
            ]
        },
        'appleid-auth-service': {
            'description': 'Apple ID authentication service',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.ostelco', 'artifactId': 'jersey'},
            ]
        },
        'ext-auth-provider': {
            'description': 'External authentication provider',
            'dependencies': [
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-core'},
            ]
        },
        'ext-myinfo-emulator': {
            'description': 'MyInfo emulator',
            'dependencies': [
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-core'},
            ]
        },
        'firebase-extensions': {
            'description': 'Firebase extensions',
            'dependencies': [
                {'groupId': 'com.google.firebase', 'artifactId': 'firebase-admin'},
            ]
        },
        'kts-engine': {
            'description': 'Kotlin script engine',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
            ]
        },
        'secure-archive': {
            'description': 'Secure archive services',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
            ]
        },
        'scaninfo-datastore': {
            'description': 'Scan info data store',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'com.google.cloud', 'artifactId': 'google-cloud-datastore'},
            ]
        },
        'scaninfo-shredder': {
            'description': 'Scan info shredder',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
            ]
        },
        'analytics-module': {
            'description': 'Analytics services',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
            ]
        },
        'tracing': {
            'description': 'Distributed tracing',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'org.ostelco', 'artifactId': 'jersey'},
            ]
        },
        'graphql': {
            'description': 'GraphQL API',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'com.graphql-java', 'artifactId': 'graphql-java'},
            ]
        },
        'diameter-stack': {
            'description': 'Diameter protocol stack',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'model'},
            ]
        },
        'diameter-ha': {
            'description': 'Diameter high availability',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'diameter-stack'},
            ]
        },
        'diameter-test': {
            'description': 'Diameter testing utilities',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'diameter-stack'},
            ]
        },
        'ocs-grpc-api': {
            'description': 'OCS gRPC API',
            'dependencies': [
                {'groupId': 'io.grpc', 'artifactId': 'grpc-stub'},
                {'groupId': 'io.grpc', 'artifactId': 'grpc-protobuf'},
            ]
        },
        'prime-customer-api': {
            'description': 'Prime customer API',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'model'},
            ]
        },
        'acceptance-tests': {
            'description': 'Acceptance tests',
            'dependencies': [
                {'groupId': 'org.ostelco', 'artifactId': 'prime-modules'},
                {'groupId': 'io.dropwizard', 'artifactId': 'dropwizard-testing'},
            ]
        }
    }
    
    # Create POMs for all modules
    for module_name, config in modules.items():
        module_dir = project_root / module_name
        if module_dir.exists():
            pom_path = module_dir / "pom.xml"
            if not pom_path.exists():  # Don't overwrite existing POMs
                pom_content = create_module_pom(
                    module_name, 
                    config['description'], 
                    config.get('dependencies', [])
                )
                
                with open(pom_path, 'w', encoding='utf-8') as f:
                    f.write(pom_content)
                
                print(f"Created POM for {module_name}")
            else:
                print(f"POM already exists for {module_name}")
        else:
            print(f"Module directory not found: {module_name}")

if __name__ == "__main__":
    main()