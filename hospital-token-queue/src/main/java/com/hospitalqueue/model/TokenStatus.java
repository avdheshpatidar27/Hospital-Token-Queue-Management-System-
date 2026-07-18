package com.hospitalqueue.model;

// An enum is a special Java type for a fixed, known set of values.
// A token can ONLY ever be in one of these 3 states -- nothing else.
public enum TokenStatus {
    WAITING,
    IN_CONSULTATION,
    COMPLETED
}
