package com.example.music.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Table("listen_contexts")
@Data
@Builder
public class ListenContextEntity {
    @Id
    private Long contextId;
    private boolean party;
    private boolean workOrStudy;
    private boolean relaxationOrMeditation;
    private boolean exercise;
    private boolean running;
    private boolean yogaStretching;
    private boolean driving;
    private boolean socialGathering;
    private boolean morningRoutine;
    private Long songId;
}
