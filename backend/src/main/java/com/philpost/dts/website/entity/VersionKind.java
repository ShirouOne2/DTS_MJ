package com.philpost.dts.website.entity;

public enum VersionKind {
    ORIGINAL("Original"), REVISION("Revision"), SIGNED("Signed copy"),
    EDITED("Edited copy"), DRAFT_REPLY("Draft reply"), ATTACHMENT("Attachment");

    private final String label;

    VersionKind(String label) { this.label = label; }

    public String getLabel() { return label; }
}