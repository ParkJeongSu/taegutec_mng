package kr.co.aim.api.service;

import kr.co.aim.common.condition.ProductDefSearchCondition;
import kr.co.aim.common.dto.ProductDefSaveRequestDto;
import kr.co.aim.common.dto.powder.IdocH2PartMResponseDto;
import kr.co.aim.common.enums.EventName;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProductDefCreateCommand;
import kr.co.aim.domain.command.ProductDefUpdateCommand;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.repository.ProductDefRepository;
import kr.co.aim.infra.persistence.db2entity.powder.H2PartMPEntity;
import kr.co.aim.infra.persistence.entity.ProductDefHistoryEntity;
import kr.co.aim.infra.persistence.mapper.ProductDefMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class ProductDefService {

    private final ProductDefRepository productDefRepository;
    private final HistoryService historyService;
    private final ProductDefMapper productDefMapper;

    @Transactional(value = "mssqlTransactionManager")
    public List<ProductDef> findAll(){
        return productDefRepository.findAll();
    }

    @Transactional(value = "mssqlTransactionManager")
    public Optional<ProductDef> findByProductDefName(String productDefName){
        return productDefRepository.findByProductDefName(productDefName);
    }

    @Transactional(value = "mssqlTransactionManager")
    public ProductDef findById(Long id) {
        Optional<ProductDef> optional = productDefRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("해당 제품 기준정보가 존재하지 않습니다. ID: " + id);
        }
        return optional.get();
    }

    @Transactional(value = "mssqlTransactionManager")
    public Page<ProductDef> findProductDefWithConditions(ProductDefSearchCondition condition, Pageable pageable) {
        return productDefRepository.findProductDefWithConditions(condition, pageable);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Optional<ProductDef> findByH2PartMPEntity(String productDefName){
        return productDefRepository.findByProductDefName(productDefName);
    }

    @Transactional(value = "mssqlTransactionManager")
    public ProductDef save(ProductDef productDef){
        return productDefRepository.save(productDef);
    }

    @Transactional(value = "mssqlTransactionManager")
    public List<ProductDef> save(List<ProductDef> productDefList){
        return productDefRepository.save(productDefList);
    }

    @Transactional
    public ProductDef createProductDef(ProductDefSaveRequestDto dto) {
        Optional<ProductDef> existing = productDefRepository.findByProductDefName(dto.getProductDefName());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 Product Def Name입니다: " + dto.getProductDefName());
        }
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(), dto.getEventUser(), dto.getEventComment());
        ProductDefCreateCommand command = ProductDefCreateCommand.builder()
                .transactionInfo(tx)
                .productDefName(dto.getProductDefName())
                .factoryName(dto.getFactoryName())
                .description1(dto.getDescription1())
                .description2(dto.getDescription2())
                .ratio(dto.getRatio())
                .defaultReceiveQuantity(dto.getDefaultReceiveQuantity())
                .build();
        ProductDef productDef = ProductDef.create(command);
        productDef = productDefRepository.save(productDef);
        ProductDefHistoryEntity historyEntity = productDefMapper.toHistoryEntity(productDef);
        historyService.saveHistory(historyEntity);
        return productDef;
    }

    @Transactional
    public void deleteProductDefs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        productDefRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional
    public ProductDef updateProductDef(ProductDefSaveRequestDto dto) {
        Optional<ProductDef> optional = productDefRepository.findById(dto.getId());
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 제품 기준정보가 없습니다. ID: " + dto.getId());
        }

        ProductDef productDef = optional.get();
        TransactionInfo tx = TransactionInfo.now(dto.getEventName(), dto.getEventUser(), dto.getEventComment());
        ProductDefUpdateCommand command = ProductDefUpdateCommand.builder()
                .transactionInfo(tx)
                .factoryName(dto.getFactoryName())
                .description1(dto.getDescription1())
                .description2(dto.getDescription2())
                .ratio(dto.getRatio())
                .defaultReceiveQuantity(dto.getDefaultReceiveQuantity())
                .build();

        productDef.update(command);
        productDef = productDefRepository.save(productDef);
        ProductDefHistoryEntity historyEntity = productDefMapper.toHistoryEntity(productDef);
        historyService.saveHistory(historyEntity);
        return productDef;
    }


    @Transactional(value = "mssqlTransactionManager")
    public ProductDef update(IdocH2PartMResponseDto dto, TransactionInfo tx){
        Optional<ProductDef> optionalProductDef = productDefRepository.findByProductDefName(dto.getCPartId());
        if(optionalProductDef.isEmpty()){
            return null;
        }

        ProductDef productDef = optionalProductDef.get();
        productDef.setDescription1(dto.getCPartDsc());
        productDef.setDescription2(dto.getCPartDsc2());
        productDef.setDefaultReceiveQuantity(dto.getDefaultReceiveQty());
        productDef.setRatio(dto.getCratIo());
        productDef.setEventTime(tx.eventTime());
        productDef.setEventUser(tx.eventUser());
        productDef.setEventName(tx.eventName());

        return productDefRepository.save(productDef);
    }



    @Transactional(value = "mssqlTransactionManager")
    public Optional<ProductDef> findByH2PartMPEntity(H2PartMPEntity h2PartMPEntity){
        return productDefRepository.findByProductDefName(h2PartMPEntity.getCPartId());
    }

    @Transactional(value = "mssqlTransactionManager")
    public ProductDef save(H2PartMPEntity h2PartMPEntity){
        ProductDef  productDef = null;
        Optional<ProductDef> optionalProductDef = findByH2PartMPEntity(h2PartMPEntity.getCPartId());
        TransactionInfo tx =TransactionInfo.now(EventName.TRANSFER.getValue(), SystemName.GAL.getValue(), "");
        if(optionalProductDef.isEmpty()){
            // 생성 케이스
            ProductDefCreateCommand command =
                    ProductDefCreateCommand
                    .builder()
                    .transactionInfo(tx)
                    .productDefName(h2PartMPEntity.getCPartId())
                    //.factoryName()
                    .description1(h2PartMPEntity.getCPartDsc())
                    .description2(h2PartMPEntity.getCPartDsc2())
                    .ratio(h2PartMPEntity.getCratIo())
                    .defaultReceiveQuantity(h2PartMPEntity.getDefaultReceiveQty())
                    .build();
            productDef = ProductDef.create(command);
            productDef = productDefRepository.save(productDef);
        }else{
            // 변경 케이스
            productDef =  optionalProductDef.get();
            ProductDefUpdateCommand command =
                    ProductDefUpdateCommand
                            .builder()
                            .transactionInfo(tx)
                            .productDefName(h2PartMPEntity.getCPartId())
                            //.factoryName()
                            .description1(h2PartMPEntity.getCPartDsc())
                            .description2(h2PartMPEntity.getCPartDsc2())
                            .ratio(h2PartMPEntity.getCratIo())
                            .defaultReceiveQuantity(h2PartMPEntity.getDefaultReceiveQty())
                            .build();

            productDef.update(command);
            productDef = productDefRepository.save(productDef);
        }

        return productDef;
    }




}