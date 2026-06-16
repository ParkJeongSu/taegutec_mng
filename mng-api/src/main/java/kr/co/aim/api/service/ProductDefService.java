package kr.co.aim.api.service;

import kr.co.aim.common.condition.ProductDefSearchCondition;
import kr.co.aim.common.dto.powder.IdocH2PartMResponseDto;
import kr.co.aim.common.enums.SystemName;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProductDefCreateCommand;
import kr.co.aim.domain.command.ProductDefUpdateCommand;
import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.domain.repository.ProductDefRepository;
import kr.co.aim.infra.persistence.db2entity.powder.H2PartMPEntity;
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

    @Transactional(value = "mssqlTransactionManager")
    public List<ProductDef> findAll(){
        return productDefRepository.findAll();
    }

    @Transactional(value = "mssqlTransactionManager")
    public Optional<ProductDef> findById(Long id){
        return productDefRepository.findById(id);
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
    public void deleteAllByIdInBatch(List<Long>ids){
        productDefRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Page<ProductDef> findProductDefByCondition(ProductDefSearchCondition condition, Pageable pageable){
        return productDefRepository.findProductDefByCondition(condition, pageable);
    }

    @Transactional(value = "mssqlTransactionManager")
    public Optional<ProductDef> findByH2PartMPEntity(H2PartMPEntity h2PartMPEntity){
        return productDefRepository.findByProductDefName(h2PartMPEntity.getCPartId());
    }

    @Transactional(value = "mssqlTransactionManager")
    public ProductDef save(H2PartMPEntity h2PartMPEntity){
        ProductDef  productDef = null;
        Optional<ProductDef> optionalProductDef = findByH2PartMPEntity(h2PartMPEntity.getCPartId());
        TransactionInfo tx =TransactionInfo.now("Transfer", SystemName.GAL.getValue(), "");
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