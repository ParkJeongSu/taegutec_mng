package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.ShelfCreateCommand;
import kr.co.aim.domain.command.ShelfUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Shelf implements HasTransactionInfo {
    private Long id;
    private String locationId;
    private String equipmentName;
    private Long zoneId;
    private Long areaId;
    private Integer capacity;
    private String shelfState;
    private String communicationState;
    private String holdState;
    private Integer length;
    private Integer width;
    private Integer height;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Shelf create(ShelfCreateCommand command) {
        Shelf shelf = Shelf.builder()
                .id(TsidUtils.nextId())
                .locationId(command.getLocationId())
                .equipmentName(command.getEquipmentName())
                .zoneId(command.getZoneId())
                .areaId(command.getAreaId())
                .capacity(command.getCapacity())
                .shelfState(command.getShelfState())
                .communicationState(command.getCommunicationState())
                .holdState(command.getHoldState())
                .length(command.getLength())
                .width(command.getWidth())
                .height(command.getHeight())
                .build();

        if (command.getTransactionInfo() != null) {
            shelf.setEventName(command.getTransactionInfo().eventName());
            shelf.setEventTime(command.getTransactionInfo().eventTime());
            shelf.setEventUser(command.getTransactionInfo().eventUser());
            shelf.setEventComment(command.getTransactionInfo().eventComment());
        }
        return shelf;
    }

    public Shelf update(ShelfUpdateCommand command) {
        if (command.getLocationId() != null) {
            setLocationId(command.getLocationId());
        }
        if (command.getEquipmentName() != null) {
            setEquipmentName(command.getEquipmentName());
        }
        if (command.getZoneId() != null) {
            setZoneId(command.getZoneId());
        }
        if (command.getAreaId() != null) {
            setAreaId(command.getAreaId());
        }
        if (command.getCapacity() != null) {
            setCapacity(command.getCapacity());
        }
        if (command.getShelfState() != null) {
            setShelfState(command.getShelfState());
        }
        if (command.getCommunicationState() != null) {
            setCommunicationState(command.getCommunicationState());
        }
        if (command.getHoldState() != null) {
            setHoldState(command.getHoldState());
        }
        if (command.getLength() != null) {
            setLength(command.getLength());
        }
        if (command.getWidth() != null) {
            setWidth(command.getWidth());
        }
        if (command.getHeight() != null) {
            setHeight(command.getHeight());
        }
        if (command.getTransactionInfo() != null) {
            setEventName(command.getTransactionInfo().eventName());
            setEventTime(command.getTransactionInfo().eventTime());
            setEventUser(command.getTransactionInfo().eventUser());
            setEventComment(command.getTransactionInfo().eventComment());
        }
        return this;
    }
}
