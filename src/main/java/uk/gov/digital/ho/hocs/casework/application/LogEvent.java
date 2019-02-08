package uk.gov.digital.ho.hocs.casework.application;

public enum LogEvent {
    CASE_RETRIEVED,
    CASE_CREATED,
    CASE_UPDATE_FAILURE,
    CASE_CREATE_FAILURE,
    CASE_NOT_FOUND,
    CASE_UPDATED,
    CASE_NOT_UPDATED_NULL_DATA,
    CASE_DELETED,
    CASE_SUMMARY_RETRIEVED,
    CASE_TYPE_LOOKUP_FAILED,
    CASE_DATA_JSON_PARSE_ERROR,
    CASE_TOPICS_RETRIEVED,
    CASE_TOPICS_NOT_FOUND,
    CASE_TOPIC_RETRIEVED,
    CASE_TOPIC_NOT_FOUND,
    CASE_PRIMARY_TOPIC_RETRIEVED,
    CASE_PRIMARY_TOPIC_NOT_FOUND,
    CASE_TOPIC_CREATE,
    CASE_TOPIC_UUID_NOT_GIVEN,
    CASE_TOPIC_DELETED,
    TOPIC_CREATE_FAILED,
    STAGE_RETRIEVED,
    STAGE_UPDATE_FAILURE,
    STAGE_CREATE_FAILURE,
    STAGE_NOT_FOUND,
    STAGE_CREATED,
    STAGE_UPDATED,
    STAGE_DEADLINE_UPDATED,
    STAGE_ASSIGNED_TEAM,
    STAGE_ASSIGNED_USER,
    STAGE_TRANSITION_NOTE_UPDATED,
    STAGE_LIST_EMPTY,
    STAGE_LIST_RETRIEVED,
    STAGE_COMPLETED,
    CASE_NOTE_CREATED,
    CASE_NOTE_CREATE_FAILURE,
    CASE_NOTE_RETRIEVED,
    CASE_NOTE_NOT_FOUND,
    PRIORITY_UPDATED,
    CORRESPONDENTS_RETRIEVED,
    CORRESPONDENT_RETRIEVED,
    CORRESPONDENT_NOT_FOUND,
    CORRESPONDENT_CREATE_FAILURE,
    CORRESPONDENT_CREATED,
    CORRESPONDENT_DELETED,
    UNCAUGHT_EXCEPTION,
    SECURITY_PARSE_ERROR,
    SECURITY_UNAUTHORISED,
    SECURITY_CASE_NOT_ALLOCATED_TO_USER,
    SECURITY_CASE_NOT_ALLOCATED_TO_TEAM,
    INVALID_ACCESS_LEVEL_FOUND,
    AUDIT_EVENT_CREATED,
    AUDIT_FAILED,
    AUDIT_CLIENT_GET_AUDITS_FOR_CASE_SUCCESS,
    AUDIT_CLIENT_GET_AUDITS_FOR_CASE_FAILURE,
    REST_HELPER_NOT_FOUND,
    REST_HELPER_INTERNAL_SERVER_ERROR,
    REST_HELPER_MALFORMED_RESPONSE,
    INFO_CLIENT_GET_CASE_TYPES_SUCCESS,
    INFO_CLIENT_GET_CASE_TYPES_FAILURE,
    INFO_CLIENT_GET_CASE_TYPE_SUCCESS,
    INFO_CLIENT_GET_CASE_TYPE_FAILURE,
    INFO_CLIENT_GET_TOPIC_SUCCESS,
    INFO_CLIENT_GET_TOPIC_FAILURE,
    INFO_CLIENT_GET_STANDARD_LINE_SUCCESS,
    INFO_CLIENT_GET_STANDARD_LINE_FAILURE,
    INFO_CLIENT_GET_TEMPLATE_SUCCESS,
    INFO_CLIENT_GET_TEMPLATE_FAILURE,
    INFO_CLIENT_GET_SUMMARY_FIELDS_SUCCESS,
    INFO_CLIENT_GET_SUMMARY_FIELDS_FAILURE,
    INFO_CLIENT_GET_DEADLINES_SUCCESS,
    INFO_CLIENT_GET_DEADLINES_FAILURE,
    INFO_CLIENT_GET_CONTACTS_SUCCESS,
    INFO_CLIENT_GET_CONTACTS_FAILURE,
    INFO_CLIENT_GET_USER_SUCCESS,
    INFO_CLIENT_GET_USER_FAILURE,
    CACHE_PRIME_FAILED,
    PRIMARY_CORRESPONDENT_UPDATED,
    PRIMARY_TOPIC_UPDATED;
    public static final String EVENT = "event_id";
}
