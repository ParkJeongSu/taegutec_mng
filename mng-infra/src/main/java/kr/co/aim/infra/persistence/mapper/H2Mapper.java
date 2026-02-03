package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.common.dto.*;
import kr.co.aim.infra.persistence.entitydb2.H2OrderDEntity;
import kr.co.aim.infra.persistence.entitydb2.H2OrderMEntity;
import kr.co.aim.infra.persistence.entitydb2.H2TransEntity;
import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface H2Mapper {

    // 1. H2OrderM 매핑
    @Mapping(source = "CClient", target = "cClient")
    @Mapping(source = "COrderId", target = "cOrderId")
    @Mapping(source = "COrderTy", target = "cOrderTy")
    @Mapping(source = "CDtPick", target = "cDtPick")
    @Mapping(source = "COrderPrio", target = "cOrderPrio")
    @Mapping(source = "CTCode", target = "cTCode")
    @Mapping(source = "CLocId", target = "cLocId")
    @Mapping(source = "CWcId", target = "cWcId")
    @Mapping(source = "CGalId", target = "cGalId")
    @Mapping(source = "CGalWhs", target = "cGalWhs")
    @Mapping(source = "CHostUsr", target = "cHostUsr")
    @Mapping(source = "CUsrNo", target = "cUsrNo")
    H2OrderMResponseDto toDto(H2OrderMEntity entity);

    // 2. H2OrderD 매핑
    @Mapping(source = "CClient", target = "cClient")
    @Mapping(source = "COrderId", target = "cOrderId")
    @Mapping(source = "COrderTy", target = "cOrderTy")
    @Mapping(source = "COrderLn", target = "cOrderLn")
    @Mapping(source = "CCoId", target = "cCoId")
    @Mapping(source = "CCoTy", target = "cCoTy")
    @Mapping(source = "CZone", target = "cZone")
    @Mapping(source = "CDrivingProfile", target = "cDrivingProfile")
    H2OrderDResponseDto toDto(H2OrderDEntity entity);

    // 3. H2Trans 매핑
    @Mapping(source = "CTransTy", target = "cTransTy")
    @Mapping(source = "CClient", target = "cClient")
    @Mapping(source = "COrderId", target = "cOrderId")
    @Mapping(source = "COrderTy", target = "cOrderTy")
    @Mapping(source = "CErrId", target = "cErrId")
    @Mapping(source = "CText1", target = "cText1")
    @Mapping(source = "CTCode", target = "cTCode")
    @Mapping(source = "COrderLn", target = "cOrderLn")
    @Mapping(source = "CGaId", target = "cGaId")
    @Mapping(source = "CGalWhs", target = "cGalWhs")
    @Mapping(source = "CCoId", target = "cCoId")
    @Mapping(source = "CGrWgAct", target = "cGrWgAct")
    @Mapping(source = "CReqZone", target = "cReqZone")
    @Mapping(source = "CZone", target = "cZone")
    @Mapping(source = "CLocId", target = "cLocId")
    @Mapping(source = "CErrDsc", target = "cErrDsc")
    @Mapping(source = "CWcId", target = "cWcId")
    H2TransResponseDto toDto(H2TransEntity entity);
}