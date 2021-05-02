package pl.atom.atomes.eventstore.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.atom.atomes.aggregate.DomainEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface DbEventEntityRepository extends JpaRepository<DbEventEntity, Long> {
    @Query("""
        SELECT dbe FROM DbEventEntity dbe
        WHERE dbe.aggregateId = :aggregateId
        """)
    List<DbEventEntity> findByAggregateId(@Param("aggregateId") UUID aggregateId);

    @Query("""
        SELECT dbe
        FROM DbEventEntity dbe
        WHERE dbe.aggregateId = :aggregateId
        AND dbe.version > :version
        """)
    List<DbEventEntity> findByAggregateIdAndFromVersion(@Param("aggregateId") UUID aggregateId,
                                                        @Param("version") Long fromVersion);
    @Query("""
        SELECT dbe FROM DbEventEntity dbe
        WHERE dbe.aggregateId = :aggregateId
        """)
    Stream<DbEventEntity> findAllByAggregateIdAsStream(UUID aggregateId);

    @Query("""
        SELECT dbe
        FROM DbEventEntity dbe
        WHERE dbe.aggregateId = :aggregateId
        AND dbe.version > :version
        """)
    Stream<DbEventEntity> findByAggregateIdAndFromVersionAsStream(@Param("aggregateId") UUID aggregateId,
                                                        @Param("version") Long fromVersion);
}
