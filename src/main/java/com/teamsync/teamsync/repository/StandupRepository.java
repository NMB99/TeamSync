package com.teamsync.teamsync.repository;

import com.teamsync.teamsync.entity.Standup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StandupRepository extends JpaRepository<Standup, Long> {

    List<Standup> findByUserId(Long userId);

    List<Standup> findByTeamIdAndDate(Long teamId, LocalDate date);

    List<Standup> findByTeamId(Long teamId);

    List<Standup> findAllByTeamId(Long teamId);
}
