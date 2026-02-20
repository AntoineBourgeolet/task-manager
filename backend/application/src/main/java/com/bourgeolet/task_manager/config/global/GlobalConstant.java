package com.bourgeolet.task_manager.config.global;

public class GlobalConstant {

    private GlobalConstant(){}

    // AGGREGATE TYPE
    public static final String AGGREGATE_TYPE_AUDIT_EVENT = "AUDIT";

    // TICKET EVENT TYPE
    public static final String TICKET_CHANGED_STATUS = "TICKET_CHANGED_STATUS";
    public static final String TICKET_CREATED = "TICKET_CREATED";
    public static final String TICKET_CHANGED_USER_AFFECTEE = "TICKET_CHANGED_USER_AFFECTEE";
    public static final String TICKET_DELETED = "TICKET_DELETED" ;


    // VERSION
    public static final int SCHEMA_VERSION_AUDIT_EVENT = 1;

    // COMMON CONSTANT
    public static final String LOWER_CAMEL_CASE_OLD_STATUS = "oldStatus";
    public static final String LOWER_CAMEL_CASE_NEW_STATUS = "newStatus";
    public static final String LOWER_CAMEL_CASE_OLD_USER = "oldUser";
    public static final String LOWER_CAMEL_CASE_NEW_USER = "newUser";
}