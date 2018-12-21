package uk.gov.digital.ho.hocs.casework.domain.exception;

import uk.gov.digital.ho.hocs.casework.application.LogEvent;

public interface ApplicationExceptions {
    class EntityCreationException extends RuntimeException {
        private final LogEvent event;

        public EntityCreationException(String msg, LogEvent event, Object... args) {
            super(String.format(msg, args));
            this.event = event;
        }

        public LogEvent getEvent() {
            return event;
        }
    }

    class CorrespondentCreationException extends RuntimeException {
        private final LogEvent event;

        public CorrespondentCreationException(String msg, LogEvent event, Object... args) {
            super(String.format(msg, args));
            this.event = event;
        }

        public LogEvent getEvent() {
            return event;
        }
    }

    class EntityNotFoundException extends RuntimeException {
        private final LogEvent event;

        public EntityNotFoundException(String msg, LogEvent event, Object... args) {
            super(String.format(msg, args));
            this.event = event;
        }

        public LogEvent getEvent() {
            return event;
        }
    }

    class ClientException extends RuntimeException {
        private final LogEvent event;

        public ClientException(String msg, LogEvent event, Object... args) {
            super(String.format(msg, args));
            this.event = event;
        }

        public LogEvent getEvent() {
            return event;
        }
    }
}