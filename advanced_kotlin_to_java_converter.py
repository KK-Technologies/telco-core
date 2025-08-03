#!/usr/bin/env python3

import os
import re
import subprocess
from pathlib import Path
from typing import List, Dict, Tuple

class KotlinToJavaConverter:
    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.conversion_stats = {
            'files_converted': 0,
            'files_failed': 0,
            'lines_converted': 0
        }
        
    def convert_project(self):
        """Convert all Kotlin files in the project to Java"""
        kotlin_files = list(self.project_root.rglob("*.kt"))
        print(f"Found {len(kotlin_files)} Kotlin files to convert")
        
        # Convert in priority order
        priority_modules = [
            'model', 'prime-modules', 'logging', 'jersey',
            'data-store', 'neo4j-store', 'document-data-store',
            'auth-server', 'prime'
        ]
        
        # Convert priority modules first
        for module in priority_modules:
            module_files = [f for f in kotlin_files if f.parts[1] == module]
            if module_files:
                print(f"\n=== Converting module: {module} ===")
                self.convert_files(module_files)
        
        # Convert remaining files
        remaining_files = [f for f in kotlin_files 
                          if f.parts[1] not in priority_modules]
        if remaining_files:
            print(f"\n=== Converting remaining modules ===")
            self.convert_files(remaining_files)
        
        self.print_stats()
    
    def convert_files(self, files: List[Path]):
        """Convert a list of Kotlin files to Java"""
        for kt_file in files:
            try:
                java_content = self.convert_file(kt_file)
                if java_content:
                    java_file = self.get_java_path(kt_file)
                    java_file.parent.mkdir(parents=True, exist_ok=True)
                    
                    with open(java_file, 'w', encoding='utf-8') as f:
                        f.write(java_content)
                    
                    print(f"✓ Converted: {kt_file.relative_to(self.project_root)}")
                    self.conversion_stats['files_converted'] += 1
                else:
                    print(f"✗ Failed: {kt_file.relative_to(self.project_root)}")
                    self.conversion_stats['files_failed'] += 1
                    
            except Exception as e:
                print(f"✗ Error converting {kt_file.relative_to(self.project_root)}: {e}")
                self.conversion_stats['files_failed'] += 1
    
    def convert_file(self, kt_file: Path) -> str:
        """Convert a single Kotlin file to Java"""
        try:
            with open(kt_file, 'r', encoding='utf-8') as f:
                content = f.read()
            
            self.conversion_stats['lines_converted'] += len(content.splitlines())
            
            # Apply conversion rules
            java_content = self.apply_conversion_rules(content, kt_file)
            return java_content
            
        except Exception as e:
            print(f"Error reading {kt_file}: {e}")
            return None
    
    def apply_conversion_rules(self, content: str, kt_file: Path) -> str:
        """Apply Kotlin to Java conversion rules"""
        
        # Add conversion header
        java_content = f"// Converted from Kotlin: {kt_file.name}\n"
        
        # Basic package and import handling
        java_content += self.convert_package_and_imports(content)
        
        # Convert class declarations
        java_content += self.convert_classes(content)
        
        return java_content
    
    def convert_package_and_imports(self, content: str) -> str:
        """Convert package declaration and imports"""
        lines = content.split('\n')
        result = []
        
        for line in lines:
            line = line.strip()
            
            # Package declaration
            if line.startswith('package '):
                result.append(line + '\n')
            
            # Import statements
            elif line.startswith('import '):
                # Convert Kotlin-specific imports to Java equivalents
                java_import = self.convert_import(line)
                if java_import:
                    result.append(java_import)
            
            # Stop at first non-import/package line
            elif line and not line.startswith('//') and not line.startswith('/*'):
                break
        
        if result:
            result.append('\n')
        
        return '\n'.join(result)
    
    def convert_import(self, import_line: str) -> str:
        """Convert Kotlin imports to Java equivalents"""
        # Kotlin stdlib to Java equivalents
        conversions = {
            'kotlin.collections.': 'java.util.',
            'kotlin.text.': 'java.lang.',
            'kotlinx.coroutines.': 'java.util.concurrent.',
        }
        
        for kt_import, java_import in conversions.items():
            if kt_import in import_line:
                return import_line.replace(kt_import, java_import)
        
        # Remove Kotlin-specific imports
        kotlin_specific = [
            'kotlin.jvm.JvmStatic',
            'kotlin.jvm.JvmOverloads',
            'kotlin.Metadata'
        ]
        
        for specific in kotlin_specific:
            if specific in import_line:
                return None
        
        return import_line
    
    def convert_classes(self, content: str) -> str:
        """Convert Kotlin class declarations to Java"""
        # This is a simplified conversion - in practice, you'd need
        # a proper parser for complex Kotlin syntax
        
        # Remove Kotlin-specific annotations
        content = re.sub(r'@file:.*\n', '', content)
        
        # Convert data classes (simplified)
        content = re.sub(
            r'data class (\w+)\((.*?)\)',
            self.convert_data_class,
            content,
            flags=re.DOTALL
        )
        
        # Convert object declarations to classes with static methods
        content = re.sub(r'object (\w+)', r'public class \1', content)
        
        # Convert class declarations
        content = re.sub(r'class (\w+)', r'public class \1', content)
        content = re.sub(r'interface (\w+)', r'public interface \1', content)
        
        # Convert function declarations
        content = re.sub(r'fun (\w+)', r'public void \1', content)
        
        # Convert val/var to appropriate Java declarations
        content = re.sub(r'\bval\s+(\w+)', r'final var \1', content)
        content = re.sub(r'\bvar\s+(\w+)', r'var \1', content)
        
        # Convert nullable types (simplified)
        content = re.sub(r'(\w+)\?', r'Optional<\1>', content)
        
        # Convert string templates (simplified)
        content = re.sub(r'\$\{([^}]+)\}', r'" + \1 + "', content)
        content = re.sub(r'\$(\w+)', r'" + \1 + "', content)
        
        return content
    
    def convert_data_class(self, match) -> str:
        """Convert Kotlin data class to Java class with getters/setters"""
        class_name = match.group(1)
        params = match.group(2)
        
        # Parse parameters (simplified)
        fields = []
        for param in params.split(','):
            param = param.strip()
            if param:
                # Extract field name and type (simplified parsing)
                parts = param.split(':')
                if len(parts) == 2:
                    field_name = parts[0].strip().replace('val ', '').replace('var ', '')
                    field_type = parts[1].strip()
                    fields.append((field_name, field_type))
        
        # Generate Java class
        java_class = f"public class {class_name} {{\n"
        
        # Fields
        for field_name, field_type in fields:
            java_class += f"    private {field_type} {field_name};\n"
        
        java_class += "\n"
        
        # Constructor
        if fields:
            java_class += f"    public {class_name}("
            java_class += ", ".join(f"{field_type} {field_name}" for field_name, field_type in fields)
            java_class += ") {\n"
            for field_name, _ in fields:
                java_class += f"        this.{field_name} = {field_name};\n"
            java_class += "    }\n\n"
        
        # Getters and setters
        for field_name, field_type in fields:
            # Getter
            getter_name = f"get{field_name.capitalize()}"
            java_class += f"    public {field_type} {getter_name}() {{\n"
            java_class += f"        return {field_name};\n"
            java_class += "    }\n\n"
            
            # Setter
            setter_name = f"set{field_name.capitalize()}"
            java_class += f"    public void {setter_name}({field_type} {field_name}) {{\n"
            java_class += f"        this.{field_name} = {field_name};\n"
            java_class += "    }\n\n"
        
        java_class += "}"
        return java_class
    
    def get_java_path(self, kt_file: Path) -> Path:
        """Convert Kotlin file path to Java file path"""
        # Convert kotlin directory to java
        parts = list(kt_file.parts)
        for i, part in enumerate(parts):
            if part == 'kotlin':
                parts[i] = 'java'
                break
        
        # Change extension
        java_path = Path(*parts)
        return java_path.with_suffix('.java')
    
    def print_stats(self):
        """Print conversion statistics"""
        print(f"\n=== Conversion Statistics ===")
        print(f"Files converted: {self.conversion_stats['files_converted']}")
        print(f"Files failed: {self.conversion_stats['files_failed']}")
        print(f"Lines processed: {self.conversion_stats['lines_converted']}")
        print(f"Success rate: {self.conversion_stats['files_converted'] / (self.conversion_stats['files_converted'] + self.conversion_stats['files_failed']) * 100:.1f}%")

def main():
    project_root = "/workspace/project/telco-core"
    converter = KotlinToJavaConverter(project_root)
    
    print("Starting Kotlin to Java conversion...")
    print("This will convert Kotlin files to Java while preserving the original files.")
    
    converter.convert_project()
    
    print("\nConversion completed!")
    print("Next steps:")
    print("1. Review converted Java files")
    print("2. Update Maven POMs to include Java source directories")
    print("3. Compile and test the converted code")
    print("4. Remove Kotlin files once Java conversion is verified")

if __name__ == "__main__":
    main()