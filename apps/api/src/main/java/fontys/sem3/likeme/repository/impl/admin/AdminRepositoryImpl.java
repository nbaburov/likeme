package fontys.sem3.likeme.repository.impl.admin;

import fontys.sem3.likeme.domain.user.admin.Admin;
import fontys.sem3.likeme.repository.interfaces.admin.AdminRepository;
import fontys.sem3.likeme.repository.impl.mapper.AdminMapper;
import fontys.sem3.likeme.repository.jpa.admin.AdminRepositoryJPA;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {
    private final AdminRepositoryJPA jpaRepository;

    @Override
    public Admin save(Admin admin) {
        return AdminMapper.mapToDomain(
                jpaRepository.save(AdminMapper.mapToEntity(admin)));
    }

    @Override
    public Optional<Admin> findById(Long id) {
        return jpaRepository.findById(id)
                .map(AdminMapper::mapToDomain);
    }

    @Override
    public List<Admin> findAll() {
        return jpaRepository.findAll().stream()
                .map(AdminMapper::mapToDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<Admin> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(AdminMapper::mapToDomain);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(AdminMapper::mapToDomain);
    }
}
