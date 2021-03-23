package kr.co.theplay.domain.images;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostTestRepository extends JpaRepository<PostTest, Long> {

    Optional<PostTest> findById(Long id);
}
