package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsRouteLinkSearchCondition;
import kr.co.aim.domain.model.WcsRouteLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsRouteLinkRepository {

    List<WcsRouteLink> findAll();

    Optional<WcsRouteLink> findById(String factoryName, Long routeLinkId);

    List<WcsRouteLink> findByFactoryName(String factoryName);

    boolean existsById(String factoryName, Long routeLinkId);

    WcsRouteLink save(WcsRouteLink routeLink);

    void deleteById(String factoryName, Long routeLinkId);

    Page<WcsRouteLink> findRouteLinks(WcsRouteLinkSearchCondition condition, Pageable pageable);
}
