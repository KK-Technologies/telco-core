// Converted from Kotlin: TextReader.kt
package org.ostelco.prime.kts.engine.reader

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.jackson.Discoverable

package org.ostelco.prime.kts.engine.reader

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.jackson.Discoverable

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
public interface TextReader : Discoverable {
    public void readText(): String
}

@JsonTypeName("classpathResource")
public public class ClasspathResourceTextReader {
    private String filename;

    public ClasspathResourceTextReader(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

} : TextReader {
    override public void readText(): String = {}.javaClass.getResource(filename).readText()
}

@JsonTypeName("file")
public public class FileTextReader {
    private String filename;

    public FileTextReader(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

} : TextReader {
    override public void readText() = java.io.FileReader(filename).readText()
}
