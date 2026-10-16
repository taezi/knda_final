package com.knda.ax.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.codex-cli")
public record CodexCliProperties(
        boolean enabled,
        String command,
        String nodeDirectory,
        String workingDirectory,
        long timeoutSeconds
) {
    public String commandOrDefault() {
        return command == null || command.isBlank() ? "codex" : command;
    }

    public long timeoutSecondsOrDefault() {
        return timeoutSeconds > 0 ? timeoutSeconds : 90;
    }
}
