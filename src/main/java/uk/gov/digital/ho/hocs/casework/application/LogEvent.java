package uk.gov.digital.ho.hocs.casework.application;

public enum LogEvent {
    CASE_RETRIEVED,
    CASE_CREATED,
    CASE_UPDATE_FAILURE,
    CASE_CREATE_FAILURE,
    CASE_NOT_FOUND,
    CASE_UPDATED,
    CASE_DELETED,
    CASE_DATA_JSON_PARSE_ERROR,
    CASE_TOPICS_RETRIEVED,
    CASE_TOPICS_NOT_FOUND,
    CASE_TOPIC_RETRIEVED,
    CASE_TOPIC_NOT_FOUND,
    CASE_PRIMARY_TOPIC_RETRIEVED,
    CASE_PRIMARY_TOPIC_NOT_FOUND,
    CASE_TOPIC_CREATE,
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
    SECURITY_CASE_NOT_ALLOCATED_TO_TEAM;
    public static final String EVENT = "event_id";
}