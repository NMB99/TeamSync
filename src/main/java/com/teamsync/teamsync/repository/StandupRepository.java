package com.teamsync.teamsync.repository;

import com.teamsync.teamsync.entity.Standup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StandupRepository extends JpaRepository<Standup, Long> {
}
