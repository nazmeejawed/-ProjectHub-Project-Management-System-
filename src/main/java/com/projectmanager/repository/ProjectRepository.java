package com.projectmanager.repository;

import com.projectmanager.model.Project;
import com.projectmanager.model.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByOrderByCreatedAtDesc();

    List<Project> findByStatus(ProjectStatus status);

    long countByStatus(ProjectStatus status);
}
