package kr.co.aim.api.service;

import kr.co.aim.api.dto.SiteCreateRequestDto;
import kr.co.aim.api.dto.SiteResponse;
import kr.co.aim.api.dto.SiteUpdateRequestDto;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.SiteCreateCommand;
import kr.co.aim.domain.command.SiteUpdateCommand;
import kr.co.aim.domain.model.Site;
import kr.co.aim.domain.repository.SiteRepository;
import kr.co.aim.domain.repository.condition.SiteSearchCondition;
import kr.co.aim.infra.persistence.entity.SiteHistoryEntity;
import kr.co.aim.infra.persistence.mapper.SiteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final SiteRepository siteRepository;
    private final HistoryService historyService;
    private final SiteMapper siteMapper;

    /**
     * 조건 및 페이징 기반 사이트(SITE) 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<SiteResponse> findSites(SiteSearchCondition condition, Pageable pageable) {
        Page<Site> pageResult = siteRepository.findSitesWithConditions(condition, pageable);
        List<SiteResponse> content = new ArrayList<>();
        for (Site item : pageResult.getContent()) {
            content.add(SiteResponse.fromDomain(item));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 사이트(SITE) 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public SiteResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 사이트(SITE) ID가 유효하지 않습니다.");
        }
        Optional<Site> optional = siteRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 사이트(SITE)이(가) 존재하지 않습니다. ID: " + id);
        }
        return SiteResponse.fromDomain(optional.get());
    }

    /**
     * 신규 사이트(SITE) 등록 (CREATE) 및 SITE_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public SiteResponse createSite(SiteCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("사이트(SITE) 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getSiteName() == null || dto.getSiteName().trim().isEmpty()) {
            throw new IllegalArgumentException("사이트명은(는) 필수 입력 항목입니다.");
        }

        String siteNameValue = dto.getSiteName().trim();
        Optional<Site> existing = siteRepository.findBySiteName(siteNameValue);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사이트(SITE)입니다: " + siteNameValue);
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "SiteCreated";
        }

        String eventUser;
        if (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) {
            eventUser = dto.getEventUser().trim();
        } else {
            eventUser = "SYSTEM";
        }

        String eventComment;
        if (dto.getEventComment() != null) {
            eventComment = dto.getEventComment().trim();
        } else {
            eventComment = "Site created";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String siteName = null;
        if (dto.getSiteName() != null) {
            siteName = dto.getSiteName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        SiteCreateCommand command = SiteCreateCommand.builder()
                .transactionInfo(tx)
                .siteName(siteName)
                .description(description)
                .build();

        Site site = Site.create(command);
        Site saved = siteRepository.save(site);

        SiteHistoryEntity historyEntity = siteMapper.toHistoryEntity(saved);
        historyService.saveHistory(historyEntity);

        log.info("Site created successfully: [id={}]", saved.getId());

        return SiteResponse.fromDomain(saved);
    }

    /**
     * TSID(ID) 기반 사이트(SITE) 수정 (UPDATE) 및 SITE_HISTORY 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public SiteResponse updateSite(Long id, SiteUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 사이트(SITE) ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 사이트(SITE) 정보가 유효하지 않습니다.");
        }

        Optional<Site> optional = siteRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 사이트(SITE)이(가) 존재하지 않습니다. ID: " + id);
        }

        Site site = optional.get();

        if (dto.getSiteName() != null && !dto.getSiteName().trim().isEmpty()) {
            String targetName = dto.getSiteName().trim();
            if (!targetName.equals(site.getSiteName())) {
                Optional<Site> duplicate = siteRepository.findBySiteName(targetName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 사이트(SITE)입니다: " + targetName);
                }
            }
        }

        String eventName;
        if (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) {
            eventName = dto.getEventName().trim();
        } else {
            eventName = "SiteModified";
        }

        String eventUser;
        if (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) {
            eventUser = dto.getEventUser().trim();
        } else {
            eventUser = "SYSTEM";
        }

        String eventComment;
        if (dto.getEventComment() != null) {
            eventComment = dto.getEventComment().trim();
        } else {
            eventComment = "Site modified";
        }

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        String siteName = null;
        if (dto.getSiteName() != null) {
            siteName = dto.getSiteName().trim();
        }

        String description = null;
        if (dto.getDescription() != null) {
            description = dto.getDescription().trim();
        }

        SiteUpdateCommand command = SiteUpdateCommand.builder()
                .transactionInfo(tx)
                .siteName(siteName)
                .description(description)
                .build();

        site.update(command);
        Site updated = siteRepository.save(site);

        SiteHistoryEntity historyEntity = siteMapper.toHistoryEntity(updated);
        historyService.saveHistory(historyEntity);

        log.info("Site updated successfully: [id={}]", updated.getId());

        return SiteResponse.fromDomain(updated);
    }

    /**
     * TSID(ID) 기반 사이트(SITE) 단건 삭제 (DELETE) 및 SITE_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteSite(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<Site> optional = siteRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 사이트(SITE)이(가) 존재하지 않습니다. ID: " + id);
        }

        Site site = optional.get();

        LocalDateTime now = LocalDateTime.now();
        String user;
        if (eventUser != null && !eventUser.trim().isEmpty()) {
            user = eventUser.trim();
        } else {
            user = "SYSTEM";
        }

        String comment;
        if (eventComment != null && !eventComment.trim().isEmpty()) {
            comment = eventComment.trim();
        } else {
            comment = "Site deleted";
        }

        site.setEventName("SiteDeleted");
        site.setEventTime(now);
        site.setEventUser(user);
        site.setEventComment(comment);

        SiteHistoryEntity historyEntity = siteMapper.toHistoryEntity(site);
        historyService.saveHistory(historyEntity);

        siteRepository.deleteById(id);

        log.info("Site deleted successfully: [id={}]", id);
    }

    /**
     * TSID(ID) 목록 기반 복수 사이트(SITE) 벌크 삭제 (DELETE batch) 및 SITE_HISTORY 삭제 이력 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteSites(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user;
        if (eventUser != null && !eventUser.trim().isEmpty()) {
            user = eventUser.trim();
        } else {
            user = "SYSTEM";
        }

        String comment;
        if (eventComment != null && !eventComment.trim().isEmpty()) {
            comment = eventComment.trim();
        } else {
            comment = "Batch Sites deleted";
        }

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<Site> optional = siteRepository.findById(id);
            if (optional.isPresent()) {
                Site site = optional.get();
                site.setEventName("SiteDeleted");
                site.setEventTime(now);
                site.setEventUser(user);
                site.setEventComment(comment);

                SiteHistoryEntity historyEntity = siteMapper.toHistoryEntity(site);
                historyService.saveHistory(historyEntity);
            }
        }

        siteRepository.deleteAllByIdInBatch(ids);
        log.info("Sites batch deleted successfully: [count={}]", ids.size());
    }
}
