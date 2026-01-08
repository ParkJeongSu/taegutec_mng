package kr.co.aim.api.service;

import kr.co.aim.common.dto.*;
import kr.co.aim.common.enums.EventName;
import kr.co.aim.common.error.EntityExistException;
import kr.co.aim.common.error.EntityNotFoundException;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.AuthorityCreateCommand;
import kr.co.aim.domain.command.AuthorityUpdateCommand;
import kr.co.aim.domain.model.Authority;
import kr.co.aim.domain.repository.AuthorityRepository;
import kr.co.aim.infra.persistence.mapper.AuthorityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final AuthorityMapper authorityMapper;

    @Transactional(readOnly = true)
    public Page<AuthorityResponseDto> findAuthority(AuthoritySearchConditionDto condition, Pageable pageable) {
        //1. Repository에서 Page<Entity>를 조회합니다.
        Page<AuthorityResponseDto> pages = authorityRepository.findAuthsWithConditions(condition,pageable);

        return pages;
    }

    @Transactional
    public void deleteUsersByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 삭제할 ID가 없으면 아무 작업도 하지 않음
        }
        // 여러 건을 삭제할 때는 이 메서드가 성능상 가장 효율적입니다.
        // DELETE ... WHERE id IN (...) 쿼리를 한 번에 실행합니다.
        authorityRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional
    public Authority createAuthority(AuthorityCreateRequestDto requestDto){

        Optional<Authority> optionalAuthority = authorityRepository.findByAuthorityName(requestDto.getAuthorityName());
        if(optionalAuthority.isPresent()){
            throw new EntityExistException("이미 존재하는 Entity입니다.");
        }

        String eventName = EventName.CREATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());

        AuthorityCreateCommand command =
                AuthorityCreateCommand.builder()
                        .authorityName(requestDto.getAuthorityName())
                        .description(requestDto.getDescription())
                        .transactionInfo(tx)
                        .build();

        Authority authority = Authority.create(command);

        return authorityRepository.save(authority);
    }

    @Transactional
    public Authority chagneAuthority(Long id, AuthorityUpdateRequestDto requestDto){
        Optional<Authority> optionalAuthority = authorityRepository.findById(id);
        Authority authority;
        if(optionalAuthority.isPresent()){
            authority = optionalAuthority.get();
        }
        else{
            throw new EntityNotFoundException("찾을 수 없습니다.");
        }
        String eventName = EventName.UPDATED.getValue();

        TransactionInfo tx = TransactionInfo.now(eventName,requestDto.getEventUser(),requestDto.getEventComment());

        AuthorityUpdateCommand command =
                AuthorityUpdateCommand.builder()
                        .authorityName(requestDto.getAuthorityName())
                        .description(requestDto.getDescription())
                        .transactionInfo(tx)
                        .build();
        authority.changeAuthority(command);
        return authorityRepository.save(authority);
    }

}