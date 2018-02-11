package matterlink

enum class Level(val level: Int) {
    /**
     * No events will be logged.
     */
    OFF(0),

    /**
     * A severe error that will prevent the application from continuing.
     */
    FATAL(1),

    /**
     * An error in the application, possibly recoverable.
     */
    ERROR(2),

    /**
     * An event that might possible lead to an error.
     */
    WARN(3),

    /**
     * An event for informational purposes.
     */
    INFO(4),

    /**
     * A general debugging event.
     */
    DEBUG(5),

    /**
     * A fine-grained debug message, typically capturing the flow through the application.
     */
    TRACE(6),
}