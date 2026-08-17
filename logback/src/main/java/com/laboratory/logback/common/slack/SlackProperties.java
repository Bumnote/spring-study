package com.laboratory.logback.common.slack;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Slack Incoming Webhook 설정. webhookUrl은 시크릿이므로 환경변수로 외부화한다.
 * (application.yml: slack.webhook-url=${SLACK_WEBHOOK_URL:})
 */
@ConfigurationProperties(prefix = "slack")
public record SlackProperties(
        boolean enabled,
        String webhookUrl
) {
}
