package com.projectmanager.service;

import com.projectmanager.model.TeamMember;
import com.projectmanager.repository.TeamMemberRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TeamMemberService {

    private final TeamMemberRepository repository;

    public TeamMemberService(TeamMemberRepository repository) {
        this.repository = repository;
    }

    public List<TeamMember> findAll() {
        return repository.findAllByOrderByNameAsc();
    }

    public Optional<TeamMember> findById(Long id) {
        return repository.findById(id);
    }

    public TeamMember save(TeamMember member) {
        return repository.save(member);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public long count() {
        return repository.count();
    }

    public boolean isEmailTaken(String email, Long excludeId) {
        if (excludeId != null) {
            return repository.existsByEmailAndIdNot(email, excludeId);
        }
        return repository.existsByEmail(email);
    }
}
