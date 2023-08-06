package gitfollower.server.repository;

import gitfollower.server.entity.Info;
import gitfollower.server.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InfoRepository extends JpaRepository<Info, Long> {
    @EntityGraph(attributePaths = "follower")
    List<Info> findAllByOwner(Member owner);

    @Transactional
    void deleteByFollower(Member follower);
}
