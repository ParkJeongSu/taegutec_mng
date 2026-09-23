package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsRouteNodeSearchCondition;
import kr.co.aim.domain.model.WcsRouteNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsRouteNodeRepository {

    List<WcsRouteNode> findAll();

    Optional<WcsRouteNode> findById(String factoryName, Long routeNodeId);

    List<WcsRouteNode> findByFactoryName(String factoryName);

    List<WcsRouteNode> findByFactoryNameAndNodeId(String factoryName, String nodeId);

    boolean existsById(String factoryName, Long routeNodeId);

    WcsRouteNode save(WcsRouteNode routeNode);

    void deleteById(String factoryName, Long routeNodeId);

    Page<WcsRouteNode> findRouteNodes(WcsRouteNodeSearchCondition condition, Pageable pageable);
}
