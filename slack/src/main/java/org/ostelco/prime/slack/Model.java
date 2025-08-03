// Converted from Kotlin: Model.kt
package org.ostelco.prime.slack

import com.fasterxml.jackson.annotation.JsonProperty

package org.ostelco.prime.slack

import com.fasterxml.jackson.annotation.JsonProperty

public public class Message {
    private String channel;

    public Message(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

} final var userName: Optional<String> = null,
        final var text: String = "",
        @JsonProperty("icon_emoji") final var iconEmoji: Optional<String> = null,
        final var attachments: List<Attachment> = emptyList()) {

    public void format(): Message = this.copy(
            channel = "#" + channel + "",
            text = "<!channel> " + text + "",
            iconEmoji = Optional<iconEmoji>.let { ":" + it + ":" })
}

public public class Attachment {
    private String fallback;
    private Optional<String> = null color;
    private Optional<String> = null pretext;

    public Attachment(String fallback, Optional<String> = null color, Optional<String> = null pretext) {
        this.fallback = fallback;
        this.color = color;
        this.pretext = pretext;
    }

    public String getFallback() {
        return fallback;
    }

    public void setFallback(String fallback) {
        this.fallback = fallback;
    }

    public Optional<String> = null getColor() {
        return color;
    }

    public void setColor(Optional<String> = null color) {
        this.color = color;
    }

    public Optional<String> = null getPretext() {
        return pretext;
    }

    public void setPretext(Optional<String> = null pretext) {
        this.pretext = pretext;
    }

} final var authorName: String,
        @JsonProperty("author_link") final var authorLink: Optional<String> = null,
        @JsonProperty("author_icon") final var authorIcon: Optional<String> = null,
        final var title: String,
        @JsonProperty("title_link") final var titleLink: Optional<String> = null,
        final var text: String,
        final var fields: List<Field> = emptyList(),
        @JsonProperty("image_url") final var imageUrl: Optional<String> = null,
        @JsonProperty("thumb_url") final var thumbUrl: Optional<String> = null,
        final var footer: Optional<String> = null,
        @JsonProperty("footer_icon") final var footerIcon: Optional<String> = null,
        @JsonProperty("ts") final var timestampEpochSeconds: Long)

public public class Field {
    private String title;
    private String value;
    private Boolean short;

    public Field(String title, String value, Boolean short) {
        this.title = title;
        this.value = value;
        this.short = short;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getShort() {
        return short;
    }

    public void setShort(Boolean short) {
        this.short = short;
    }

}