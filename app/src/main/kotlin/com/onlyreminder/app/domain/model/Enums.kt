package com.onlyreminder.app.domain.model

enum class ContactStatus {
    ACTIVE,
    ARCHIVED
}

enum class BirthdayRunStatus {
    PENDING,
    COMPLETED,
    NOT_REVIEWED
}

enum class BirthdayItemStatus {
    PENDING,
    SENT,
    SENT_MANUAL,
    FAILED,
    SKIPPED
}

enum class TaskStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}

enum class MessageStatus {
    PENDING,
    SENT,
    FAILED,
    DELIVERED,
    READ
}
