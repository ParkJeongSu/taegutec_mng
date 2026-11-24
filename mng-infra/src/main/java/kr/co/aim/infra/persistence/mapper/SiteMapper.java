package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Site;
import kr.co.aim.infra.persistence.entity.SiteEntity;
import kr.co.aim.infra.persistence.entity.SiteHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface SiteMapper {

    Site toDomain(SiteEntity entity);

    SiteEntity toEntity(Site domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    SiteHistoryEntity toHistoryEntity(Site domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    SiteHistoryEntity toHistoryEntity(SiteEntity entity);
}
